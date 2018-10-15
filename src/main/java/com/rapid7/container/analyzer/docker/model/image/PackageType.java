
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.image;

public enum PackageType {

  APGK,
  DPGK,
  PACMAN,
  RPM,
  UNKNOWN;

  public static PackageType fromString(String string) {
    for (PackageType type : values())
      if (type.name().equalsIgnoreCase(string))
        return type;
    
    return UNKNOWN;
  }
  
  @Override
  public String toString() {
    return name();
  }
}
