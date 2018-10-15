package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.ApkgParser;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;


public class ApkgFingerprinter implements LayerFileHandler {
  private ApkgParser apkgParser;

  public ApkgFingerprinter(ApkgParser apkgParser) {
    this.apkgParser = apkgParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, Layer layer) throws IOException {
    if (!entry.isSymbolicLink() && name.endsWith("lib/apk/db/installed"))
      layer.addPackages(apkgParser.parse(contents, image.getOperatingSystem() == null ? layer.getOperatingSystem() : image.getOperatingSystem()));
  }
}
