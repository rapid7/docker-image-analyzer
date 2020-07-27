package com.rapid7.container.analyzer.docker.model;

import com.rapid7.container.analyzer.docker.model.image.Layer;

/**
 * Wraps a {@link Layer} with the path to it
 */
public class LayerPath {
  private final Layer layer;
  private final String basePath;

  public LayerPath(String basePath, Layer layer) {
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
