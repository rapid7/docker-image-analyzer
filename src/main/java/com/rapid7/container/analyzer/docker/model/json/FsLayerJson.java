
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class FsLayerJson {

  private String blobSum;

  @JsonProperty("blobSum")
  public String getBlobSum() {
    return blobSum;
  }

  public void setBlogSum(String blobSum) {
    this.blobSum = blobSum;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", FsLayerJson.class.getSimpleName() + "[", "]")
        .add("Blob Sum=" + blobSum)
        .toString();
  }
}
