package com.rapid7.container.analyzer.docker.model.image;

public enum FileType {

  REGULAR_FILE('-'),
  DIRECTORY('d'),
  SYMBOLIC_LINK('l'),
  CHARACTER_DEVICE('c'),
  BLOCK_DEVICE('b'),
  WHITEOUT('w'),
  WHITEOUT_OPAQUE('o');

  private char code;

  private FileType(char code) {
    this.code = code;
  }

  public char getCode() {
    return code;
  }

  public static FileType fromString(String value) {
    for (FileType type : values())
      if (type.name().equalsIgnoreCase(value))
        return type;

    return REGULAR_FILE;
  }
}
