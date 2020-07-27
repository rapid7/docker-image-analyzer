package com.rapid7.container.analyzer.docker.analyzer;

import com.rapid7.container.analyzer.docker.model.LayerPath;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.json.Configuration;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

public interface LayerFileHandler {

  void handle(String name, TarArchiveEntry entry, InputStream contents, Image image, Configuration configuration, LayerPath layerPath) throws IOException;
}
