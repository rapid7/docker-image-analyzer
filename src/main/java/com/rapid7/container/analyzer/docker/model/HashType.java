package com.rapid7.container.analyzer.docker.model;

public enum HashType {

  /** An MD5 based hash. */
  MD5,
  /** A SHA-1 based hash. */
  SHA1,
  /** A SHA-256 based hash. */
  SHA256,
  /** An unknown hash type. */
  UNKNOWN;

  public static HashType fromValue(String type) {
    if (type != null)
      for (HashType value : values())
        if (value.toString().equalsIgnoreCase(type))
          return value;

    return UNKNOWN;
  }

  @Override
  public String toString() {
    return name().toLowerCase();
  }
}
