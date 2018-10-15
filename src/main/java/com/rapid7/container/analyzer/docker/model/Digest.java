package com.rapid7.container.analyzer.docker.model;

public class Digest extends HashId {

  public Digest(String id, HashType type) {
    super(id, type);
  }

  public Digest(String hashId) {
    super(hashId);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof Digest) {
      Digest other = (Digest)obj;
      return super.equals(other);
    } else
      return false;
  }
}
