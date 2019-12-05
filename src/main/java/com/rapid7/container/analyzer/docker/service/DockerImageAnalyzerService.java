package com.rapid7.container.analyzer.docker.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapid7.container.analyzer.docker.analyzer.ImageHandler;
import com.rapid7.container.analyzer.docker.analyzer.LayerExtractor;
import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.fingerprinter.ApkgFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.DpkgFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.FileFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.OsReleaseFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.PacmanFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.RpmFingerprinter;
import com.rapid7.container.analyzer.docker.fingerprinter.WhiteoutImageHandler;
import com.rapid7.container.analyzer.docker.model.Digest;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.ImageType;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import com.rapid7.container.analyzer.docker.model.image.json.ImageModelObjectMapper;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.model.json.ConfigurationJsonV2;
import com.rapid7.container.analyzer.docker.model.json.HistoryJson;
import com.rapid7.container.analyzer.docker.model.json.LayerJson;
import com.rapid7.container.analyzer.docker.model.json.Manifest;
import com.rapid7.container.analyzer.docker.model.json.TarManifestJson;
import com.rapid7.container.analyzer.docker.os.Fingerprinter;
import com.rapid7.container.analyzer.docker.packages.ApkgParser;
import com.rapid7.container.analyzer.docker.packages.DpkgParser;
import com.rapid7.container.analyzer.docker.packages.PacmanPackageParser;
import com.rapid7.container.analyzer.docker.packages.RpmPackageParser;
import com.rapid7.container.analyzer.docker.util.InstantParser;
import com.rapid7.container.analyzer.docker.util.InstantParserModule;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.slf4j.helpers.MessageFormatter.format;

public class DockerImageAnalyzerService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DockerImageAnalyzerService.class);
  private static final String WHITEOUT_AUFS_PREFIX = ".wh.";
  private static final long MAX_READ_FILE_SIZE = 1_048_576 * 25; // 25 MB
  private static final long MAX_EXTRACT_FILE_SIZE = 1_048_576 * 256; // 256 MB
  private ObjectMapper objectMapper;
  private List<LayerFileHandler> layerHandlers;
  private List<ImageHandler> imageHandlers;

  public DockerImageAnalyzerService(String rpmDockerImage) {
    objectMapper = new ObjectMapper();
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    objectMapper.registerModule(new InstantParserModule());
    imageHandlers = new ArrayList<>(1);
    imageHandlers.add(new WhiteoutImageHandler());
    layerHandlers = new ArrayList<>(6);
    layerHandlers.add(new OsReleaseFingerprinter(new Fingerprinter()));
    layerHandlers.add(new RpmFingerprinter(new RpmPackageParser(), rpmDockerImage));
    layerHandlers.add(new DpkgFingerprinter(new DpkgParser()));
    layerHandlers.add(new ApkgFingerprinter(new ApkgParser()));
    layerHandlers.add(new PacmanFingerprinter(new PacmanPackageParser()));
  }

  public void addFileHandler(LayerFileHandler handler) {
    layerHandlers.add(Objects.requireNonNull(handler));
  }

  public void addFileHashFingeprinter() {
    if (layerHandlers.stream().noneMatch(h -> h instanceof FileFingerprinter))
      layerHandlers.add(new FileFingerprinter());
  }

  public ImageId getId(File imageTar) throws IOException {

    try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(imageTar)))) {
      TarArchiveEntry entry = null;
      while ((entry = tarIn.getNextTarEntry()) != null) {
        String name = entry.getName();

        if (name.equals("manifest.json")) {

          List<TarManifestJson> manifests = objectMapper.readValue(new NonClosingInputStream(tarIn), objectMapper.getTypeFactory().constructCollectionType(List.class, TarManifestJson.class));
          TarManifestJson manifest = manifests.get(0);

          return new ImageId(manifest.getConfig().replaceAll(".json", ""));
        }
      }
    }

    return null;
  }

  public void writeJson(Image image, File destination) throws IOException {
    ObjectMapper imageMapper = new ImageModelObjectMapper();
    imageMapper.writeValue(destination, image);
  }

  public Image analyze(File imageTar, String tempFilePath) throws IOException {

    File outputDirectory = Files.createTempDirectory(Paths.get(tempFilePath), FilenameUtils.removeExtension(imageTar.getName())).toFile();
    try {
      LOGGER.info("Extracting image to {}.", outputDirectory.getAbsolutePath());
      // extract the tar
      untar(imageTar, outputDirectory);
      LOGGER.info("Extracted image {}.", outputDirectory.getAbsolutePath());
      TarManifestJson manifest = parseTarManifest(imageTar.length(), new File(outputDirectory, "manifest.json"));
      Image image = analyze(
          outputDirectory,
          manifest.getImageId(),
          null,
          manifest,
          parseConfiguration(new File(outputDirectory, manifest.getConfig())),
          layerId -> new File(outputDirectory, layerId.getId() + "/layer.tar"));

      LOGGER.info("Completed analyzing image {}", image.getId());
      return image;
    } finally {
      deleteQuietly(outputDirectory);
    }
  }

  public <M extends Manifest> Image analyze(File outputDirectory, ImageId id, Digest digest, M manifest, Configuration configuration, LayerExtractor layerSupplier) throws IOException {

    try {
      // parse the image configuration
      List<HistoryJson> layerHistories = configuration.getHistory();

      // the ordered identifiers of non-empty layers (which are presumed to have file-system changes requiring a blob)
      List<LayerId> layers = manifest.getLayers();

      // the ordered identifiers of the blobs to retrieve for each non-empty layer
      List<LayerId> layerBlobIds = manifest.getLayerBlobIds();

      // TODO: not pulling out the repository in the digest reference
      Image image = new Image(id, ImageType.DOCKER, digest, manifest.getSize(), configuration.getCreated());

      int layerIndex = 0;
      int emptyLayerIdSuffix = 0;
      Layer previousLayer = null;
      for (HistoryJson layerHistory : layerHistories) {
        Instant start = Instant.now();
        boolean empty = layerHistory.isEmpty();
        LayerId layerId = null;
        if (!empty)
          layerId = layers.get(layerIndex);
        else
          layerId = new LayerId(id.getString() + "_empty_" + emptyLayerIdSuffix++);

        LOGGER.debug("Processing layer {}.", layerId);

        Layer layer = new Layer(layerId);
        layer.setCommand(layerHistory.getCommand());
        layer.setComment(layerHistory.getComment());
        layer.setAuthor(layerHistory.getAuthor());
        layer.setEmpty(empty);

        // Images built with a tool called buildkit can have history entries with empty created date, so fall back to previous layer or epoch
        if (layer.getCreated() != null)
          layer.setCreated(InstantParser.parse(layerHistory.getCreated()));
        else if (previousLayer != null && previousLayer.getCreated() != null)
          layer.setCreated(previousLayer.getCreated());
        else
          layer.setCreated(Instant.EPOCH);

        // if the layer is non-empty, process it
        if (!empty) {

          // locate the layer to process
          LayerId layerRetrievalId = layerBlobIds.get(layerIndex);
          File layerTar = layerSupplier.getLayer(layerRetrievalId);
          layer.setSize(layerTar.length());
          LOGGER.debug("Processing layer tar.");

          // extract layer
          processLayer(image, configuration, layer, layerTar);

          // attach additional layer information
          File layerInfoFile = new File(layerTar.getParentFile(), "json");
          if (layerInfoFile.exists()) {
            try {
              LayerJson layerInfo = parseLayerConfiguration(layerInfoFile);
              layer.setCreated(layerInfo.getCreated());
              layer.setParentId(layerInfo.getParentId());
            } catch (Exception exception) {
              LOGGER.warn("Failed to parse layer configuration json. Skipping setting of created data and parent identifier.", exception);
            }
          } else if (previousLayer != null) {
            layer.setParentId(previousLayer.getId());
          }

          layerIndex++;
          previousLayer = layer;
        } else if (previousLayer != null) {
          layer.setParentId(previousLayer.getId());
        }

        image.addLayer(layer);
      }

      // post-process image handlers
      for (ImageHandler handler : imageHandlers)
        handler.handle(image);

      // if the size on the image isn't known (e.g. from a manifest that does not support it), sum up layers as a last resort
      if (image.getSize() == null || image.getSize() <= 0L)
        image.setSize(image.getLayers().stream().mapToLong(Layer::getSize).sum());

      // if the created date on an image isn't known, use the last created date from the last layer
      if (image.getCreated() == null && !image.getLayers().isEmpty())
        image.setCreated(image.getLayers().get(image.getLayers().size() - 1).getCreated());

      return image;
    } catch (IOException exception) {
      LOGGER.error("Failed to analyze image.", exception);
      throw exception;
    }
  }

  public Manifest parseManifest(File file) throws JsonParseException, JsonMappingException, IOException {
    return objectMapper.readValue(file, Manifest.class); // TODO: polymorphic
  }

  public Configuration parseConfiguration(File file) throws JsonParseException, JsonMappingException, IOException {
    return objectMapper.readValue(file, ConfigurationJsonV2.class);
  }

  public TarManifestJson parseTarManifest(long tarSize, File file) throws JsonParseException, JsonMappingException, IOException {
    List<TarManifestJson> manifests = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, TarManifestJson.class));
    return manifests.get(0).setSize(tarSize);
  }

  public LayerJson parseLayerConfiguration(File file) throws JsonParseException, JsonMappingException, IOException {
    return objectMapper.readValue(file, LayerJson.class);
  }

  public void untar(File tar, File destination) throws FileNotFoundException, IOException {

    try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tar)))) {
      TarArchiveEntry entry = null;
      while ((entry = tarIn.getNextTarEntry()) != null) {
        String name = entry.getName();
        File file = new File(destination, name);
        try {
          if (!file.toPath().toAbsolutePath().normalize().startsWith(destination.toPath().toAbsolutePath().normalize())) {
            LOGGER.debug(format("[Image: {}] Skipping extraction of {} due to directory traversal.", tar.getName(), name).getMessage());
            continue;
          }
        } catch (InvalidPathException exception) {
          LOGGER.debug(format("[Image: {}] Skipping extraction of {} due to invalid filename.", tar.getName(), name).getMessage());
          continue;
        }

        LOGGER.debug(arrayFormat("[Image: {}] Extracting {} {}.", new Object[]{tar.getName(), entry.isDirectory() ? "directory" : "file", file.getAbsolutePath()}).getMessage());

        long usableSpace = file.getParentFile().getFreeSpace();
        long objectSize = entry.getRealSize();
        if (objectSize * 3 > usableSpace) {
          throw new IOException("Insufficient disk space to extract resource. " + usableSpace + " available, " + objectSize * 3 + " required.");
        }

        try {
          if (entry.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
          } else if (!name.contains(WHITEOUT_AUFS_PREFIX)) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
              IOUtils.copy(tarIn, outputStream);
            }
          } else {
            LOGGER.debug(format("[Image: {}] Skipping whiteout file {}.", tar.getName(), file.getAbsolutePath()).getMessage());
          }
        } catch (FileNotFoundException exception) {
          // can occur if there is case sensitivity collision, etc
          LOGGER.warn(format("[Image: {}] File failed to extract {}.", tar.getName(), file.getAbsolutePath()).getMessage());
        }
      }
    }
  }

  private void processLayer(Image image, Configuration configuration, Layer layer, File tar) throws FileNotFoundException, IOException {

    try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(tar), 65536))) {
      processLayerTar(image, configuration, layer, tar, tarIn);
    } catch (ZipException ze) {
      try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(tar), 65536))) {
        processLayerTar(image, configuration, layer, tar, tarIn);
      }
    }
  }

  private void processLayerTar(Image image, Configuration configuration, Layer layer, File tar, TarArchiveInputStream tarIn) throws IOException {
    TarArchiveEntry entry = null;
    while ((entry = tarIn.getNextTarEntry()) != null) {
      String name = entry.getName();
      if (LOGGER.isTraceEnabled())
        LOGGER.trace(arrayFormat("[Image: {}] Processing {} {}.", new Object[]{tar.getName(), entry.isDirectory() ? "directory" : "file", name}).getMessage());

      // extract 25 MB+ files instead of reading fully into memory
      if (entry.getSize() > MAX_READ_FILE_SIZE) {
        if (entry.getSize() > MAX_EXTRACT_FILE_SIZE) {
          LOGGER.debug("Skipping file {} with size {} bytes because exceeds max of {} bytes.", name, entry.getSize(), MAX_EXTRACT_FILE_SIZE);
          continue;
        }
        LOGGER.debug(format("Extracting large file {} ({} bytes)", entry.getName(), entry.getSize()).getMessage());
        try {
          File bigFile = new File(tar.getParentFile(), entry.getName());
          if (!bigFile.toPath().toAbsolutePath().normalize().startsWith(bigFile.getParentFile().toPath().toAbsolutePath().normalize())) {
            LOGGER.debug(format("[Image: {}] Skipping extraction of {} due to directory traversal.", tar.getName(), name).getMessage());
            continue;
          }

          bigFile.getParentFile().mkdirs();
          try (FileOutputStream outputStream = new FileOutputStream(bigFile)) {
            IOUtils.copy(tarIn, outputStream);
          }

          try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(bigFile), 65536)) {
            inputStream.mark((int) (bigFile.getTotalSpace() + 1));
            for (LayerFileHandler handler : layerHandlers) {
              handler.handle(name, entry, inputStream, image, configuration, layer);
              inputStream.reset();
            }
          }

          bigFile.delete();
        } catch (IOException e) {
          LOGGER.info("Failed to handle file {}", entry.getName());
        }
      }
      else {
        // read small files in memory
        try (ConvertibleOutputStream out = new ConvertibleOutputStream((int) entry.getSize())) {
          IOUtils.copy(tarIn, out);
          InputStream contents = out.toInputStream();
          for (LayerFileHandler handler : layerHandlers) {
            handler.handle(name, entry, contents, image, configuration, layer);
            contents.reset();
          }
        }
      }
    }
  }

  private static class ConvertibleOutputStream extends ByteArrayOutputStream {
    public ConvertibleOutputStream(int size) {
      super(size);
    }
    // Creates InputStream without actually copying the buffer and using up memory for that.
    public InputStream toInputStream() {
      return new ByteArrayInputStream(buf, 0, count);
    }
  }

  private static class NonClosingInputStream extends FilterInputStream {
    NonClosingInputStream(InputStream in) {
      super(in);
    }

    @Override
    public void close() throws IOException {
    }
  }
}
