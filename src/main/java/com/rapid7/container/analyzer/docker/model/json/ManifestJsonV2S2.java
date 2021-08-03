package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;

/**
 * Models an image manifest, schema version 2.
 *
 * @see <a href="https://docs.docker.com/registry/spec/manifest-v2-2">Docker Image Manifest v2</a>
 */
public class ManifestJsonV2S2 implements Manifest {

  private String mediaType;
  private LayerReferenceJson config;
  private List<LayerReferenceJson> layers;

  public ManifestJsonV2S2() {
    layers = new ArrayList<>();
  }

  @JsonProperty("mediaType")
  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  @JsonProperty("config")
  public LayerReferenceJson getConfig() {
    return config;
  }

  public void setConfig(LayerReferenceJson config) {
    this.config = config;
  }

  @JsonProperty("layers")
  public List<LayerReferenceJson> getLayerReferences() {
    return layers;
  }

  public void setLayerReferences(List<LayerReferenceJson> layers) {
    this.layers = layers;
  }

  @Override
  public List<LayerId> getLayers() {
    return getLayerReferences().stream().map(reference -> reference.getDigest()).map(LayerId::new).collect(toList());
  }

  @Override
  public ImageId getImageId() {
    return config == null ? null : config.getDigest() == null ? null : new ImageId(config.getDigest());
  }

  @Override
  public long getSize() {
    return layers.stream().collect(summingLong(LayerReferenceJson::getSize));
  }

  @Override
  public String getType() {
    return "v2s2";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ManifestJsonV2S2.class.getSimpleName() + "[", "]")
        .add("Media Type=" + mediaType)
        .add("Config=" + config)
        .add("Layers=" + layers)
        .toString();
  }
}
