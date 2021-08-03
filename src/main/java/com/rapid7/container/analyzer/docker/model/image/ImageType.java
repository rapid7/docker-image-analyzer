
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.image;

public enum ImageType {

  /** Docker image. */
  DOCKER,
  /** Unknown. */
  UNKNOWN;

  @Override
  public String toString() {
    return name();
  }

  public static ImageType fromValue(String type) {
    if (type != null)
      for (ImageType value : values())
        if (value.name().equalsIgnoreCase(type))
          return value;

    return UNKNOWN;
  }
}
