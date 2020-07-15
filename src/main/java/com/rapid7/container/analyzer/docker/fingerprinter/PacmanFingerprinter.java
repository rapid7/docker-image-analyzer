package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.LayerPathWrapper;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.PacmanPackageParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class PacmanFingerprinter implements LayerFileHandler {
  private final PacmanPackageParser pacmanPackageParser;

  public PacmanFingerprinter(PacmanPackageParser pacmanPackageParser) {
    this.pacmanPackageParser = pacmanPackageParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPathWrapper layerPathWrapper) throws IOException {
    if (pacmanPackageParser.supports(name, entry)) {
      layerPathWrapper.getLayer().addPackages(pacmanPackageParser.parse(contents, image.getOperatingSystem() == null ? layerPathWrapper.getLayer().getOperatingSystem() : image.getOperatingSystem()));
    }
  }
}
