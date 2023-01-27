package com.rapid7.container.analyzer.docker.model.image.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.rapid7.container.analyzer.docker.model.image.IdentifiablePackage;
import com.rapid7.container.analyzer.docker.model.image.OperatingSystem;
import com.rapid7.container.analyzer.docker.model.image.Package;
import com.rapid7.container.analyzer.docker.model.image.PackageId;
import com.rapid7.container.analyzer.docker.model.image.PackageType;
import com.rapid7.container.analyzer.docker.model.image.PackageValidationException;

@JsonTypeName("id-package")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
public abstract class IdentifiablePackageMixin extends IdentifiablePackage {

  @JsonCreator
  public IdentifiablePackageMixin(
      @JsonProperty("id") PackageId id,
      @JsonProperty("source") String source,
      @JsonProperty("type") PackageType type,
      @JsonProperty("osVendor") String osVendor,
      @JsonProperty("osFamily") String osFamily,
      @JsonProperty("osName") String osName,
      @JsonProperty("osVersion") String osVersion,
      @JsonProperty("osArchitecture") String osArchitecture,
      @JsonProperty("name") String name,
      @JsonProperty("version") String version,
      @JsonProperty("description") String description,
      @JsonProperty("size") Long size,
      @JsonProperty("maintainer") String maintainer,
      @JsonProperty("homePage") String homepage,
      @JsonProperty("license") String license,
      @JsonProperty("epoch") String epoch,
      @JsonProperty("release") String release) throws PackageValidationException {
    super(id, source, type, osVendor, osFamily, osName, osVersion, osArchitecture, name, version, description, size, maintainer, homepage, license, epoch, release);
  }

  @Override
  @JsonProperty("id")
  public abstract PackageId getId();

  @Override
  @JsonProperty("type")
  public abstract PackageType getType();

  @Override
  @JsonProperty("osVendor")
  public abstract String getOsVendor();

  @Override
  @JsonProperty("osFamily")
  public abstract String getOsFamily();

  @Override
  @JsonProperty("osName")
  public abstract String getOsName();

  @Override
  @JsonProperty("osVersion")
  public abstract String getOsVersion();

  @Override
  @JsonProperty("osArchitecture")
  public abstract String getOsArchitecture();

  @Override
  @JsonProperty("source")
  public abstract String getSource();

  @Override
  @JsonProperty("name")
  public abstract String getPackage();

  @Override
  @JsonProperty("version")
  public abstract String getVersion();

  @Override
  @JsonProperty("description")
  public abstract String getDescription();

  @Override
  @JsonProperty("maintainer")
  public abstract String getMaintainer();

  @Override
  @JsonProperty("homePage")
  public abstract String getHomepage();

  @Override
  @JsonProperty("size")
  public abstract Long getSize();

  @Override
  @JsonProperty("license")
  public abstract String getLicense();

  @Override
  @JsonProperty("epoch")
  public abstract String getEpoch();

  @Override
  @JsonProperty("release")
  public abstract String getRelease();

  // ignored

  @Override
  @JsonIgnore
  public abstract Package setOperatingSystem(OperatingSystem operatingSystem);
}
