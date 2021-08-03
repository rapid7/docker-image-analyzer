package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.HashType;
import com.rapid7.container.analyzer.docker.model.image.ImageId;

public abstract class ImageIdMixin extends ImageId {

  @JsonCreator
  public ImageIdMixin(String idStr) {
    super(idStr);
  }

  @Override
  @JsonProperty("id")
  public abstract String getString();

  // ignored

  @Override
  @JsonIgnore
  public abstract String getId();

  @Override
  @JsonIgnore
  public abstract HashType getType();
}
