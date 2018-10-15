package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;

/**
 * Models an image manifest, schema version 2, schema 1.
 *
 * @see <a href="https://docs.docker.com/registry/spec/manifest-v2-1">Docker Image Manifest v2 Schema 1</a>
 */
public class ManifestJsonV2S1 implements Manifest, Configuration {

  private static final LayerId EMPTY_LAYER_ID = new LayerId("sha256:a3ed95caeb02ffe68cdd9fd84406680ae93d633cb16422d00e8a7c22955b46d4");
  private String name;
  private String tag;
  private String architecture;
  private List<FsLayerJson> fsLayers;
  private List<V1CompatibleHistory> v1history;
  private static ObjectMapper objectMapper;
  private List<LayerId> layers;
  private List<HistoryJson> history;
  private List<LayerManifestV1> layerManifests;

  static {
    objectMapper = new ObjectMapper()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .registerModule(new JavaTimeModule());
  }

  public ManifestJsonV2S1() {
    fsLayers = new ArrayList<>();
    v1history = new ArrayList<>();
    history = new ArrayList<>();
    layers = new ArrayList<>();
    layerManifests = new ArrayList<>();
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("tag")
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  @JsonProperty("architecture")
  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture(String architecture) {
    this.architecture = architecture;
  }

  @JsonProperty("fsLayers")
  public List<FsLayerJson> getFsLayers() {
    return fsLayers;
  }

  public void setFsLayers(List<FsLayerJson> fsLayers) {
    this.fsLayers = fsLayers;
  }

  @JsonProperty("history")
  public List<V1CompatibleHistory> getV1History() {
    return v1history;
  }

  public void setV1History(List<V1CompatibleHistory> v1history) {
    this.v1history = v1history;

    try {
      for (V1CompatibleHistory v1History : this.v1history) {

        LayerManifestV1 layerManifest = objectMapper.readValue(v1History.getV1Compatibility(), LayerManifestV1.class);

        HistoryJson layerHistory = new HistoryJson();
        layerHistory.setCreated(layerManifest.getCreated());
        layerHistory.setCommands(layerManifest.getCommands());
        layerHistory.setEmpty(layerManifest.getThrowaway());

        if (!layerManifest.getThrowaway())
          layers.add(layerManifest.getId());

        history.add(layerHistory);
      }

      // the histories are in last-first order (top-most to bottom-most)
      Collections.reverse(history);

      // the layer are in last-first order (top-most to bottom-most)
      Collections.reverse(layers);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  @Override
  public List<LayerId> getLayerBlobIds() {
    // some very old instances of the manifest have no "throwaway" (empty layer) declarations, in this
    // case empty layers are not filtered out from the fs layer blob ids
    boolean filterEmpty = layers.size() != history.size();

    // in normal situations, just filter out all layer blob IDs for empty layers (the layer ID of
    // an empty layer is consistently hashed)
    List<LayerId> retrievalIds = new ArrayList<>();
    for (FsLayerJson fsLayer : fsLayers) {
      LayerId layerId = new LayerId(fsLayer.getBlobSum());
      if (!filterEmpty || !layerId.equals(EMPTY_LAYER_ID))
        retrievalIds.add(layerId);
    }

    // there are also cases where the number of layer histories subtract the number of throwaway layers
    // does not equal the number of layer blob IDs; if the number of non-empty layers is not equivalent
    // to the number of non-empty layer blobs, then treat them as one-to-one with no filtering
    if (retrievalIds.size() != layers.size())
      retrievalIds = fsLayers.stream().map(fsLayer -> new LayerId(fsLayer.getBlobSum())).collect(toList());

    // retrieval blob ids are in reverse order (top-most to bottom-most)
    Collections.reverse(retrievalIds);

    return retrievalIds;
  }

  @Override
  public List<LayerId> getLayers() {
    return layers;
  }

  @Override
  public ImageId getImageId() {
    // does not have an ID
    return null;
  }

  @Override
  public long getSize() {
    return layerManifests.stream().collect(summingLong(LayerManifestV1::getSize));
  }

  @Override
  public List<HistoryJson> getHistory() {
    return history;
  }

  @Override
  public Instant getCreated() {
    return null;
  }

  @Override
  public String getOperatingSystem() {
    return null;
  }

  @Override
  public String getType() {
    return "v2s1";
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ManifestJsonV2S1.class.getSimpleName() + "[", "]")
        .add("Name=" + name)
        .add("Tag=" + tag)
        .add("Architecture=" + architecture)
        .toString();
  }
}
