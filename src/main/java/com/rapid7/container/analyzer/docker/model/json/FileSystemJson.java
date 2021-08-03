
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.StringJoiner;

public class FileSystemJson {

  private String type;
  private List<String> layers;

  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("diff_ids")
  public List<String> getLayers() {
    return layers;
  }

  public void setLayers(List<String> layers) {
    this.layers = layers;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FileSystemJson.class.getSimpleName() + "[", "]")
        .add("Type=" + type)
        .add("Layers=" + layers)
        .toString();
  }
}
