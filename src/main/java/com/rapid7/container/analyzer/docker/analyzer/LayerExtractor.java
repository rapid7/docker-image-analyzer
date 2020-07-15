package com.rapid7.container.analyzer.docker.analyzer;

import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.io.File;
import java.io.IOException;

public interface LayerExtractor {

  File getLayer(LayerId layerId) throws IOException;
}
