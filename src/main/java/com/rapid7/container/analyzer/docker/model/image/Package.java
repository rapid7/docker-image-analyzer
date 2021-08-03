package com.rapid7.container.analyzer.docker.model.image;

import java.util.Objects;
import java.util.StringJoiner;
import static java.util.Objects.requireNonNull;

public class Package implements Comparable<Package> {

  private PackageType type;
  private String name;
  private String osVendor;
  private String osFamily;
  private String osVersion;
  private String osName;
  private String osArchitecture;
  private String source;
  private String version;
  private String description;
  private Long size;
  private String maintainer;
  private String homepage;
  private String license;
  /**
   * Exclusive to RPM package types, the epoch tag is a monotonically increasing integer where the largest epoch
   * number means it's the newest version of a package. When multiple packages have the same epoch value, then the
   * release field is compared to identify the newest package.
   */
  private String epoch;
  /**
   * Exclusive to RPM package types, the release field is a build version representing patches not in the upstream
   * version (major version number). Is used to identify a newer version of a package when multiple packages have the
   * same epoch value.
   */
  private String release;

  public Package(String source, PackageType type, OperatingSystem os, String name, String version, String description, Long size, String maintainer, String homepage, String license) {
    this(source, type, os == null ? null : os.getVendor(), os == null ? null : os.getFamily(), os == null ? null : os.getName(), os == null ? null : os.getVersion(), os == null ? null : os.getArchitecture(), name, version, description, size, maintainer, homepage, license, null, null);
  }

  public Package(String source, PackageType type, OperatingSystem os, String name, String version, String description, Long size, String maintainer, String homepage, String license, String epoch, String release) {
    this(source, type, os == null ? null : os.getVendor(), os == null ? null : os.getFamily(), os == null ? null : os.getName(), os == null ? null : os.getVersion(), os == null ? null : os.getArchitecture(), name, version, description, size, maintainer, homepage, license, epoch, release);
  }

  public Package(String source, PackageType type, String osVendor, String osFamily, String osName, String osVersion, String osArchitecture, String name, String version, String description, Long size, String maintainer, String homepage, String license) {
    this(source, type, osVendor, osFamily, osName, osVersion, osArchitecture, name, version, description, size, maintainer, homepage, license, null, null);
  }

  public Package(String source, PackageType type, String osVendor, String osFamily, String osName, String osVersion, String osArchitecture, String name, String version, String description, Long size, String maintainer, String homepage, String license, String epoch, String release) {
    this.type = requireNonNull(type, "type");
    this.source = source;
    this.osVendor = osVendor;
    this.osFamily = osFamily;
    this.osVersion = osVersion;
    this.osArchitecture = osArchitecture;
    this.osName = osName;
    this.name = requireNonNull(name, "name");
    this.version = requireNonNull(version, "version");
    this.description = description;
    this.maintainer = maintainer;
    this.homepage = homepage;
    this.size = size;
    this.license = license;
    this.epoch = epoch;
    this.release = release;
  }

  public PackageType getType() {
    return type;
  }

  public String getPackage() {
    return name;
  }

  public String getOsVendor() {
    return osVendor;
  }

  public String getOsFamily() {
    return osFamily;
  }

  public String getOsName() {
    return osName;
  }

  public String getOsVersion() {
    return osVersion;
  }

  public String getOsArchitecture() {
    return osArchitecture;
  }

  public String getSource() {
    return source;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription() {
    return description;
  }

  public Long getSize() {
    return size;
  }

  public String getMaintainer() {
    return maintainer;
  }

  public String getHomepage() {
    return homepage;
  }

  public String getLicense() {
    return license;
  }

  public String getEpoch() {
    return epoch;
  }

  public String getRelease() {
    return release;
  }

  public Package setOperatingSystem(OperatingSystem operatingSystem) {
    osVendor = operatingSystem.getVendor();
    osFamily = operatingSystem.getFamily();
    osVersion = operatingSystem.getVersion();
    osName = operatingSystem.getName();
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, osVendor, osName, osVersion, osArchitecture, name, version, epoch, release, source);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (!(obj instanceof Package)) {
      return false;
    } else {
      Package other = (Package) obj;
      return Objects.equals(type, other.type)
          && Objects.equals(osVendor, other.osVendor)
          && Objects.equals(osFamily, other.osFamily)
          && Objects.equals(osName, other.osName)
          && Objects.equals(osVersion, other.osVersion)
          && Objects.equals(osArchitecture, other.osArchitecture)
          && Objects.equals(name, other.name)
          && Objects.equals(version, other.version)
          && Objects.equals(epoch, other.epoch)
          && Objects.equals(release, other.release)
          && Objects.equals(source, other.source);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Package.class.getSimpleName() + "[", "]")
        .add("Type=" + type)
        .add("OS Vendor=" + osVendor)
        .add("OS Family=" + osFamily)
        .add("OS Name=" + osName)
        .add("OS Version=" + osVersion)
        .add("OS Architecture=" + osArchitecture)
        .add("Package=" + name)
        .add("Version=" + version)
        .add("Source=" + source)
        .add("Size=" + size)
        .add("Maintainer=" + maintainer)
        .add("Home Page=" + homepage)
        .add("License=" + license)
        .add("Epoch=" + epoch)
        .add("Release=" + release)
        .toString();
  }

  @Override
  public int compareTo(Package other) {
    return name.compareTo(other.name);
  }
}
