package com.rapid7.container.analyzer.docker.test;

import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Manifest;
import com.rapid7.container.analyzer.docker.model.json.TarManifestJson;
import com.rapid7.container.analyzer.docker.service.DockerImageAnalyzerService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.io.FileUtils.deleteQuietly;

public class ImageAnalyzerTester {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageAnalyzerTester.class);
  private DockerImageAnalyzerService analyzer;
  private static File recogContentDirectory;
  private static final long MAX_SIZE = 2147483648L;

  /**
   * Test image extraction and fingerprinting. Provide the full path to a local checkout of
   * github.com/rapid7/recog and the full path to either a docker image tar file (from docker save)
   * or a directory containing manifest, config, and layer files (from image puller lambda - S3
   * bucket).
   *
   * @param args Path to Recog, Path to Image file(s)
   */
  public static void main(String[] args) throws IOException {
    if (args.length < 2)
      throw new IllegalArgumentException("Required arguments: recog path, image file path");

    recogContentDirectory = new File(args[0]);
    ImageAnalyzerTester tester = new ImageAnalyzerTester();
    tester.analyzeImage(new File(args[1]));
  }

  public File recogContent() {
    return recogContentDirectory;
  }

  public void analyzeImage(File imagePath) throws IOException {
    if (!imagePath.exists())
      throw new FileNotFoundException("File does not exist: " + imagePath);

    File outputDirectory = Files.createTempDirectory(null).toFile();
    outputDirectory.deleteOnExit();
    try {
      if (imagePath.isFile()) {
        LOGGER.info("Working with single tar file at {}", imagePath);
        File unzippedTar = new File(outputDirectory, imagePath.getName().replaceAll("\\.gz$", ""));
        long available = unzippedTar.getParentFile().getUsableSpace();
        try (FileOutputStream output = new FileOutputStream(unzippedTar)) {
          try (GZIPInputStream input = new GZIPInputStream(new FileInputStream(imagePath))) {
            byte[] buf = new byte[8192];
            int len;
            long total = 0;
            while ((len = input.read(buf)) > 0) {
              total += len;
              if (total > MAX_SIZE) {
                LOGGER.warn("GZip decompression of {} exceeded threshold of {} bytes.", unzippedTar, MAX_SIZE);
                throw new IOException("GZip file decompression exceeded maximum permitted size.");
              } else if (total * 3 > available) {
                LOGGER.warn("GZip decompression of {} exceeded safe available disk space of {} bytes.", unzippedTar, available - (total * 3));
                throw new IOException("GZip file decompression exceeded safe available disk space.");
              }
              output.write(buf, 0, len);
            }
            LOGGER.info("Decompressed gzipped file to {}", unzippedTar);
          } catch (ZipException zipException) {
            unzippedTar = imagePath;
            LOGGER.warn("Failed to decompress gzip {}.", imagePath, zipException);
          }
        } catch (IOException ioException) {
          LOGGER.warn("Failed to decompress image tar file to {}.", unzippedTar, ioException);
          throw ioException;
        }
        LOGGER.info("Untarring {}", unzippedTar);
        analyzer.untar(unzippedTar, outputDirectory);
      } else {
        LOGGER.info("Working with collection of layers at {}", imagePath);
      }

      File manifestFile = null;
      if (imagePath.isDirectory())
        for (File file : imagePath.listFiles())
          if (file.getName().startsWith("manifest-") && file.getName().endsWith(".json"))
            manifestFile = file;

      Image image = null;
      if (manifestFile == null) {
        TarManifestJson manifest = analyzer.parseTarManifest(imagePath.length(), new File(outputDirectory, "manifest.json"));
        image = analyzer.analyze(
            // place output in this directory
            outputDirectory,
            // image id
            manifest.getImageId(),
            // image digest (unknown)
            null,
            // parse the manifest file
            manifest,
            // parse the configuration file
            analyzer.parseConfiguration(new File(outputDirectory, manifest.getConfig())),
            // locate each referenced layer
            layerId -> new File(outputDirectory, layerId.getId() + "/layer.tar"));
      } else {
        Manifest manifest = analyzer.parseManifest(manifestFile);
        image = analyzer.analyze(
            // place output in this directory
            outputDirectory,
            // image id
            manifest.getImageId(),
            // image digest (unknown)
            null,
            // parse the manifest file
            manifest,
            // parse the configuration file
            analyzer.parseConfiguration(new File(imagePath, "config-" + manifest.getImageId().getId() + ".json")),
            // locate each referenced layer
            layerId -> download(imagePath.getAbsolutePath() + "/" + layerId.getId() + ".tar.gz", new LayerDecompressor(outputDirectory, layerId)));
      }

      System.out.println(image.toString());
      System.out.println(image.getOperatingSystem());
    } finally {
      deleteQuietly(outputDirectory);
    }
  }

  @FunctionalInterface
  public interface DownloadCallback<T> {
    T onDownload(File file) throws IOException;
  }

  public <T> T download(String file, DownloadCallback<T> callback) throws IOException {
    T value = callback.onDownload(new File(file));
    return value;

  }
}
