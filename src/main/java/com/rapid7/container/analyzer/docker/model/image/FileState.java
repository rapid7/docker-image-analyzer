package com.rapid7.container.analyzer.docker.model.image;

public enum FileState {

  ADDED,
  REMOVED,
  UPDATED,
  UNKNOWN;

  public static FileState fromString(String value) {
    for (FileState type : values())
      if (type.name().equalsIgnoreCase(value))
        return type;

    return UNKNOWN;
  }
}
