package com.rapid7.container.analyzer.docker.model.image;

import com.rapid7.container.analyzer.docker.model.HashId;
import com.rapid7.container.analyzer.docker.model.HashType;

public class ImageId extends HashId {

  public ImageId(String id, HashType type) {
    super(id, type);
  }
  
  public ImageId(String idStr) {
    super(idStr);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    else if (!(obj instanceof ImageId))
      return false;
    else
      return super.equals(obj);
  }
}
