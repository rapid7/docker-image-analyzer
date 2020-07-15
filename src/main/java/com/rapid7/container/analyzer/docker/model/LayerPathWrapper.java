package com.rapid7.container.analyzer.docker.model;

import com.rapid7.container.analyzer.docker.model.image.Layer;

public class LayerPathWrapper {
  private final Layer layer;
  private final String basePath;

  public LayerPathWrapper(String basePath, Layer layer) {
    this.basePath = basePath;
    this.layer = layer;
  }

  public Layer getLayer() {
    return layer;
  }

  public String getPath() {
    return basePath;
  }
}
