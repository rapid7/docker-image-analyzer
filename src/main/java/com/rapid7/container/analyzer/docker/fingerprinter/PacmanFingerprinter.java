package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.PacmanPackageParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class PacmanFingerprinter implements LayerFileHandler {
  private PacmanPackageParser pacmanPackageParser;
  private static final Pattern PACMAN_DESC_PATTERN = Pattern.compile(".*/lib/pacman/local/(?<name>.*)/desc");

  public PacmanFingerprinter(PacmanPackageParser pacmanPackageParser) {
    this.pacmanPackageParser = pacmanPackageParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, Layer layer) throws IOException {
    if (!entry.isSymbolicLink() && PACMAN_DESC_PATTERN.matcher(name).matches())
      layer.addPackages(pacmanPackageParser.parse(contents, image.getOperatingSystem() == null ? layer.getOperatingSystem() : image.getOperatingSystem()));
  }
}
