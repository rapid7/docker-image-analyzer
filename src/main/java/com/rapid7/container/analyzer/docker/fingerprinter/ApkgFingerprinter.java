package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.LayerPathWrapper;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.ApkgParser;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;


public class ApkgFingerprinter implements LayerFileHandler {
  private final ApkgParser apkgParser;

  public ApkgFingerprinter(ApkgParser apkgParser) {
    this.apkgParser = apkgParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPathWrapper layerPathWrapper) throws IOException {
    if (apkgParser.supports(name, entry)) {
      layerPathWrapper.getLayer().addPackages(apkgParser.parse(contents, image.getOperatingSystem() == null ? layerPathWrapper.getLayer().getOperatingSystem() : image.getOperatingSystem()));
    }
  }
}
