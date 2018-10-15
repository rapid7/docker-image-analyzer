package com.rapid7.container.analyzer.docker.model;

import java.util.Objects;
import static java.util.Objects.requireNonNull;

/**
 * An identifier that is a hash value.
 */
public class HashId {

  protected HashType type;
  protected String id;

  public HashId(String id, HashType type) {
    this.id = requireNonNull(id, "id");
    this.type = requireNonNull(type, "type");
  }

  public HashId(String hashId) {
    String[] components = requireNonNull(hashId, "id").split(":");
    type = components.length == 2 ? HashType.fromValue(components[0]) : HashType.SHA256;
    id = components[components.length - 1];
  }

  public String getId() {
    return id;
  }

  public HashType getType() {
    return type;
  }

  public String getString() {
    return type + ":" + id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof HashId) {
      HashId other = (HashId)obj;
      return Objects.equals(id, other.id)
          && Objects.equals(type, other.type);
    } else
      return false;
  }

  @Override
  public String toString() {
    return getString();
  }
}
