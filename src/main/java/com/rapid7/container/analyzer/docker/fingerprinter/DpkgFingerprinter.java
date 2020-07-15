package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.LayerPathWrapper;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.DpkgParser;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class DpkgFingerprinter implements LayerFileHandler {
  private final DpkgParser dpkgParser;

  public DpkgFingerprinter(DpkgParser dpkgParser) {
    this.dpkgParser = dpkgParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPathWrapper layerPathWrapper) throws IOException {
    if (dpkgParser.supports(name, entry)) {
      layerPathWrapper.getLayer().addPackages(dpkgParser.parse(contents, image.getOperatingSystem() == null ? layerPathWrapper.getLayer().getOperatingSystem() : image.getOperatingSystem()));
    }
  }
}
