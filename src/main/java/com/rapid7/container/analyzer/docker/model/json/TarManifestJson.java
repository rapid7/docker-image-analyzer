
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class TarManifestJson implements Manifest {

  private String config;
  private List<String> repoTags;
  private List<String> layers;
  private long size;

  public TarManifestJson() {
    size = -1L;
    repoTags = new ArrayList<>();
    layers = new ArrayList<>();
  }

  @JsonProperty("Config")
  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = requireNonNull(config, "config");
  }

  @JsonProperty("RepoTags")
  public List<String> getRepoTags() {
    return repoTags;
  }

  public void setRepoTags(List<String> repoTags) {
    this.repoTags = repoTags;
  }

  @JsonProperty("Layers")
  public List<String> getLayerIds() {
    return layers;
  }

  public void setLayerIds(List<String> layers) {
    this.layers = requireNonNull(layers, "layers");
  }

  @Override
  public List<LayerId> getLayers() {
    return getLayerIds().stream().map(layer -> layer.replaceAll("/layer.tar", "")).map(LayerId::new).collect(toList());
  }

  @Override
  public ImageId getImageId() {
    return new ImageId(getConfig().replaceAll(".json", ""));
  }

  @Override
  public long getSize() {
    return size;
  }

  public TarManifestJson setSize(long size) {
    this.size = size;
    return this;
  }

  @Override
  public String getType() {
    return "docker save tar";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", TarManifestJson.class.getSimpleName() + "[", "]")
        .add("Config=" + config)
        .add("Repo Tags=" + repoTags)
        .add("Layers=" + layers)
        .add("Size=" + size)
        .toString();
  }
}
