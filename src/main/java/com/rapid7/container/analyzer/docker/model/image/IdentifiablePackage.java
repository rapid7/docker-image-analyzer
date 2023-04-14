package com.rapid7.container.analyzer.docker.model.image;

import static java.util.Objects.requireNonNull;


public class IdentifiablePackage extends Package {

  private PackageId id;

  public IdentifiablePackage(PackageId id, String source, PackageType type, OperatingSystem os, String name, String version, String description, Long size, String maintainer,
      String homepage, String license) throws PackageValidationException {
    super(source, type, os, name, version, description, size, maintainer, homepage, license);
    if (id == null) {
      throw new PackageValidationException("Invalid input when attempting to create a package. Packages require an id and no id was provided.");
    } else {
      this.id = requireNonNull(id);
    }
  }
  
  public IdentifiablePackage(PackageId id, String source, PackageType type, String osVendor, String osFamily, String osName, String osVersion, String osArchitecture, String name,
      String version, String description, Long size, String maintainer, String homepage, String license) throws PackageValidationException {
    super(source, type, osVendor, osFamily, osName, osVersion, osArchitecture, name, version, description, size, maintainer, homepage, license);
    if (id == null) {
      throw new PackageValidationException("Invalid input when attempting to create a package. Packages require an id and no id was provided.");
    } else {
      this.id = requireNonNull(id);
    }
  }
  
  public IdentifiablePackage(PackageId id, String source, PackageType type, String osVendor, String osFamily, String osName, String osVersion, String osArchitecture, String name,
      String version, String description, Long size, String maintainer, String homepage, String license, String epoch, String release) throws PackageValidationException {
    super(source, type, osVendor, osFamily, osName, osVersion, osArchitecture, name, version, description, size, maintainer, homepage, license, epoch, release);
    if (id == null) {
      throw new PackageValidationException("Invalid input when attempting to create a package. Packages require an id and no id was provided.");
    } else {
      this.id = requireNonNull(id);
    }
  }

  public PackageId getId() {
    return id;
  }
}
