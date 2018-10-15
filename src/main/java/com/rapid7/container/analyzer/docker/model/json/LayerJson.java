
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.time.Instant;
import java.util.StringJoiner;
import static java.util.Objects.requireNonNull;

public class LayerJson {

  private LayerId id;
  private LayerId parentId;
  private Instant created;

  @JsonProperty("id")
  public LayerId getId() {
    return id;
  }

  public LayerJson setId(LayerId id) {
    this.id = requireNonNull(id);
    return this;
  }

  @JsonProperty("parentId")
  public LayerId getParentId() {
    return parentId;
  }

  public LayerJson setParentId(LayerId parentId) {
    this.parentId = requireNonNull(parentId);
    return this;
  }

  @JsonProperty("parent")
  public LayerId getParent() {
    return parentId;
  }

  public LayerJson setParent(LayerId parentId) {
    this.parentId = requireNonNull(parentId);
    return this;
  }


  public Instant getCreated() {
    return created;
  }

  public LayerJson setCreated(Instant created) {
    this.created = requireNonNull(created);
    return this;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LayerJson.class.getSimpleName() + "[", "]")
        .add("Id=" + id)
        .add("Paren tId=" + parentId)
        .add("Created=" + created)
        .toString();
  }
}
