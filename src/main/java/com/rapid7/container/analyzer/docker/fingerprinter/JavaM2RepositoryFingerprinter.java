package com.rapid7.container.analyzer.docker.fingerprinter;

import com.rapid7.container.analyzer.docker.analyzer.LayerFileHandler;
import com.rapid7.container.analyzer.docker.model.LayerPath;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import com.rapid7.container.analyzer.docker.packages.M2RepositoryParser;
import com.rapid7.container.analyzer.docker.packages.OwaspDependencyParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public class JavaM2RepositoryFingerprinter implements LayerFileHandler {

  private final M2RepositoryParser m2RepositoryParser;

  public JavaM2RepositoryFingerprinter(M2RepositoryParser m2RepositoryParser){
    this.m2RepositoryParser = m2RepositoryParser;
  }

  @Override
  public void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPath layerPath) throws IOException {
    if (m2RepositoryParser.supports(name, entry)) {
      File tmpFile = Paths.get(layerPath.getPath(), name).toFile();
      if (tmpFile.isFile()) {
        layerPath.getLayer().addPackages(m2RepositoryParser.parse(tmpFile, image.getOperatingSystem() == null
            ? layerPath.getLayer().getOperatingSystem() : image.getOperatingSystem()));
      }
    }
  }
}
