
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.image;

import java.util.Objects;
import java.util.StringJoiner;
import static java.util.Objects.requireNonNull;

public class OperatingSystem {

  private String vendor;
  private String family;
  private String name;
  private String architecture;
  private String version;
  private String description;

  protected OperatingSystem() {
    // deserialization
  }

  public OperatingSystem(String vendor, String family, String name, String architecture, String version, String description) {
    this.vendor = sanitize(vendor);
    this.family = sanitize(family);
    this.name = sanitize(requireNonNull(name, "name"));
    this.architecture = sanitize(architecture);
    this.version = sanitize(version);
    this.description = sanitize(description);
  }

  public String getVendor() {
    return vendor;
  }

  public String getFamily() {
    return family;
  }

  public String getName() {
    return name;
  }

  public String getArchitecture() {
    return architecture;
  }

  public String getVersion() {
    return version;
  }

  public String getDescription() {
    return description;
  }

  static String sanitize(String value) {
    if (value == null)
      return value;
    else if (value.isEmpty())
      return null;
    // (none) a common value is replaced to null
    else if ("(none)".equalsIgnoreCase(value))
      return null;
    else {
      // remove wrapping single and double quotes
      value = value.replaceAll("^[\"']|[\"']$", "");

      if (value.isEmpty())
        return null;
      else
        return value.trim();
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(vendor, family, name, architecture, version, description);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    else if (!(obj instanceof OperatingSystem))
      return false;
    else {
      OperatingSystem other = (OperatingSystem)obj;
      return Objects.equals(vendor, other.vendor)
          && Objects.equals(family, other.family)
          && Objects.equals(name, other.name)
          && Objects.equals(architecture, other.architecture)
          && Objects.equals(version, other.version)
          && Objects.equals(description, other.description);
    }
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", OperatingSystem.class.getSimpleName() + "[", "]")
        .add("Vendor=" + vendor)
        .add("Family=" + family)
        .add("Name=" + name)
        .add("Architecture=" + architecture)
        .add("Version=" + version)
        .add("Description=" + description)
        .toString();
  }
}
