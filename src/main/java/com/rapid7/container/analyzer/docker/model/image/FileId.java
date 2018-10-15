package com.rapid7.container.analyzer.docker.model.image;

public class FileId extends TypeSafeLongId {

  public FileId(long id) {
    super(id);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof FileId)
      return super.equals(obj);
    else
      return false;
  }
}
