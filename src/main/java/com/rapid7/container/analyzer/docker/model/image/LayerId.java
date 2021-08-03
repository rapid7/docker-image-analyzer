
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.image;

import com.rapid7.container.analyzer.docker.model.HashId;
import com.rapid7.container.analyzer.docker.model.HashType;

public class LayerId extends HashId implements Comparable<LayerId> {

  public LayerId(String id, HashType type) {
    super(id, type);
  }

  public LayerId(String hashId) {
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
    else if (!(obj instanceof LayerId))
      return false;
    else
      return super.equals(obj);
  }

  @Override
  public int compareTo(LayerId other) {
    return id.compareTo(other.id);
  }
}
