package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.Digest;
import com.rapid7.container.analyzer.docker.model.image.Image;
import com.rapid7.container.analyzer.docker.model.image.ImageId;
import com.rapid7.container.analyzer.docker.model.image.ImageType;
import com.rapid7.container.analyzer.docker.model.image.Layer;
import com.rapid7.container.analyzer.docker.model.image.LayerFile;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public abstract class ImageMixin extends Image {

  @JsonCreator
  public ImageMixin(
      @JsonProperty("id") ImageId id,
      @JsonProperty("type") ImageType type,
      @JsonProperty("size") Long size,
      @JsonProperty("created") Instant created) {
    super(id, type, size, created);
  }

  @Override
  @JsonProperty("id")
  public abstract ImageId getId();

  @Override
  @JsonProperty("digest")
  public abstract Digest getDigest();

  @Override
  @JsonProperty("type")
  public abstract ImageType getType();

  @Override
  @JsonProperty("size")
  public abstract Long getSize();

  @Override
  @JsonProperty("created")
  public abstract Instant getCreated();

  @Override
  @JsonProperty("layers")
  public abstract List<Layer> getLayers();

  @Override
  @JsonProperty("layers")
  public abstract Image addLayers(Iterable<? extends Layer> layers);

  @Override
  @JsonIgnore
  public abstract Set<Package> getPackages();

  @Override
  @JsonIgnore
  public abstract OperatingSystem getOperatingSystem();

  @Override
  @JsonIgnore
  public abstract boolean isAnalyzed();

  @Override
  @JsonIgnore
  public abstract List<LayerFile> getFiles();

  @Override
  @JsonIgnore
  public abstract List<Layer> getPackageLayer();

  @Override
  @JsonIgnore
  public abstract Layer getLayer(LayerId id);
}
