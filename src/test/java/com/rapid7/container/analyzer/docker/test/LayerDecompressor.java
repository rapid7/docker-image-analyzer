package com.rapid7.container.analyzer.docker.test;

import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LayerDecompressor implements com.rapid7.container.analyzer.docker.test.ImageAnalyzerTester.DownloadCallback<File> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LayerDecompressor.class);
  private File outputDirectory;
  private LayerId layerId;
  private static final long MAX_SIZE = 2147483648L;

  public LayerDecompressor(File outputDirectory, LayerId layerId) {
    this.outputDirectory = outputDirectory;
    this.layerId = layerId;
  }

  @Override
  public File onDownload(File file) throws IOException {

    File layerTar = new File(outputDirectory, "layer-" + layerId.getId() + ".tar");
    long available = layerTar.getParentFile().getUsableSpace();
    LOGGER.info("Decompressing {} to {}.", file.getAbsolutePath(), layerTar.getAbsolutePath());
    LOGGER.info("Total space: {} - Free space: {} - Required space: {}", layerTar.getParentFile().getTotalSpace(), layerTar.getParentFile().getFreeSpace(), file.length());

    try (FileOutputStream output = new FileOutputStream(layerTar)) {
      try (GZIPInputStream input = new GZIPInputStream(new FileInputStream(file))) {
        byte[] buf = new byte[8192];
        int len;
        long total = 0;
        while ((len = input.read(buf)) > 0) {
          total += len;
          if (total > MAX_SIZE) {
            throw new IOException("GZip file decompression exceeded maximum permitted size.");
          } else if (total * 3 > available) {
            throw new IOException("GZip file decompression exceeded safe available disk space.");
          }
          output.write(buf, 0, len);
        }
      }
    } catch (IOException ioException) {
      LOGGER.warn("Failed to analyze layer {}.", layerId, ioException);
      throw ioException;
    }

    return layerTar;
  }
}
