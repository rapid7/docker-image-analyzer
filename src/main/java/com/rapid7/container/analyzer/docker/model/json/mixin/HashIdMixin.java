package com.rapid7.container.analyzer.docker.model.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.HashId;
import com.rapid7.container.analyzer.docker.model.HashType;

public abstract class HashIdMixin extends HashId {

  @JsonCreator
  public HashIdMixin(String idStr) {
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
