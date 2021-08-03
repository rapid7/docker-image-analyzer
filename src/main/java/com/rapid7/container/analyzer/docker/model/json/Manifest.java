package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
    defaultImpl = TarManifestJson.class,
    property = "schemaVersion")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TarManifestJson.class),
    @JsonSubTypes.Type(value = ManifestJsonV2S1.class, name = "1"),
    @JsonSubTypes.Type(value = ManifestJsonV2S2.class, name = "2")
})
public interface Manifest {

  public List<LayerId> getLayers();

  public default List<LayerId> getLayerBlobIds() {
    return getLayers();
  }

  public ImageId getImageId();

  public long getSize();

  public String getType();
}
