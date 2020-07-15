package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.LayerPathWrapper;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.OwaspDependencyParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class OwaspDependencyFingerprinter implements LayerFileHandler {
  private final OwaspDependencyParser dependencyParser;

  public OwaspDependencyFingerprinter(OwaspDependencyParser dependencyParser) {
    this.dependencyParser = dependencyParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPathWrapper layerPathWrapper) throws IOException {
    if (dependencyParser.supports(name, entry)) {
      File tmpFile = Paths.get(layerPathWrapper.getPath(), name).toFile();
      if (tmpFile.isFile()) {
        layerPathWrapper.getLayer().addPackages(dependencyParser.parse(tmpFile, image.getOperatingSystem() == null ?
            layerPathWrapper.getLayer().getOperatingSystem() : image.getOperatingSystem()));
      }
    }
  }
}
