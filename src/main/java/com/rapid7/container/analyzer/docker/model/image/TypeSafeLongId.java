package com.rapid7.container.analyzer.docker.model.image;

/**
 * A type-safe ID with a {@link Long} value. Implementations should override the {@link TypeSafeLongId#equals(Object)}
 * method to ensure that identifiers of different types are not equivalent, even if the underlying values are
 * the same.
 */
public abstract class TypeSafeLongId extends TypeSafeId<Long> {
  
  public TypeSafeLongId() {

  }

  public TypeSafeLongId(Long id) {
    super(id);
  }

  /////////////////////////////////////////////////////////////////////////
  // Object overrides
  /////////////////////////////////////////////////////////////////////////

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof TypeSafeLongId) {
      TypeSafeLongId other = (TypeSafeLongId)obj;
      return super.equals(other);
    } else
      return false;
  }
}
