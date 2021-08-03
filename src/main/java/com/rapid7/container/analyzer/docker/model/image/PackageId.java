package com.rapid7.container.analyzer.docker.model.image;

public class PackageId extends TypeSafeLongId {

  public PackageId(long id) {
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
    else if (obj instanceof PackageId)
      return super.equals(obj);
    else
      return false;
  }
}
