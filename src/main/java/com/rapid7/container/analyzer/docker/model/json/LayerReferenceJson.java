
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.StringJoiner;

public class LayerReferenceJson {

  private String mediaType;
  private long size;
  private String digest;

  @JsonProperty("mediaType")
  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  @JsonProperty("size")
  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  @JsonProperty("digest")
  public String getDigest() {
    return digest;
  }

  public void setDigest(String digest) {
    this.digest = digest;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LayerReferenceJson.class.getSimpleName() + "[", "]")
        .add("Media Type=" + mediaType)
        .add("Size=" + size)
        .add("Digest=" + digest)
        .toString();
  }
}
