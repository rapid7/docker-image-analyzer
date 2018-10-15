package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;

public abstract class OperatingSystemMixin extends OperatingSystem {

  @JsonCreator
  public OperatingSystemMixin(@JsonProperty("vendor") String vendor, @JsonProperty("family") String family, @JsonProperty("name") String name, @JsonProperty("architecture") String architecture, @JsonProperty("version") String version, @JsonProperty("description") String description) {
    super(vendor, family, name, architecture, version, description);
  }

  @Override
  @JsonProperty("vendor")
  public abstract String getVendor();

  @Override
  @JsonProperty("family")
  public abstract String getFamily();

  @Override
  @JsonProperty("name")
  public abstract String getName();

  @Override
  @JsonProperty("architecture")
  public abstract String getArchitecture();

  @Override
  @JsonProperty("version")
  public abstract String getVersion();

  @Override
  @JsonProperty("description")
  public abstract String getDescription();
}
