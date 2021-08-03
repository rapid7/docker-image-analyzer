package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.rapid7.container.analyzer.docker.model.image.LayerId;

public abstract class LayerIdMixin extends LayerId {

  @JsonCreator
  public LayerIdMixin(String idStr) {
    super(idStr);
  }
}
