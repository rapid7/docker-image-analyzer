package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class V1CompatibleHistory {

  String v1Compatibility;

  @JsonProperty("v1Compatibility")
  public String getV1Compatibility() {
    return v1Compatibility;
  }

  public void setV1Compatibility(String v1Compatibility) {
    this.v1Compatibility = v1Compatibility;
  }
}
