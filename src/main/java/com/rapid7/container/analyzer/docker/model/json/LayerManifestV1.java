package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rapid7.container.analyzer.docker.model.image.LayerId;
import java.util.Arrays;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class LayerManifestV1 {

  private ContainerConfig containerConfig;
  private LayerId id;
  private LayerId parentId;
  private String created;
  private String architecture;
  private String os;
  private long size;
  private boolean throwaway;

  @JsonProperty("id")
  public LayerId getId() {
    return id;
  }

  public void setId(LayerId id) {
    this.id = requireNonNull(id);
  }

  @JsonProperty("parentId")
  public LayerId getParentId() {
    return parentId;
  }

  public void setParentId(LayerId parentId) {
    this.parentId = requireNonNull(parentId);
  }

  @JsonProperty("parent")
  public LayerId getParent() {
    return parentId;
  }

  public void setParent(LayerId parentId) {
    this.parentId = requireNonNull(parentId);
  }

  @JsonProperty("created")
  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = requireNonNull(created);
  }

  @JsonProperty("architecture")
  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture(String architecture) {
    this.architecture = requireNonNull(architecture);
  }

  @JsonProperty("os")
  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = requireNonNull(os);
  }

  @JsonProperty("Size")
  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = requireNonNull(size);
  }

  @JsonProperty("throwaway")
  public boolean getThrowaway() {
    return throwaway;
  }

  public void setThrowaway(boolean throwaway) {
    this.throwaway = throwaway;
  }

  @JsonProperty("container_config")
  public ContainerConfig getContainerConfig() {
    return containerConfig;
  }

  public void setContainerConfig(ContainerConfig containerConfig) {
    this.containerConfig = containerConfig;
  }

  public String getCommands() {
    return containerConfig == null || containerConfig.getCommands() == null ? null : Arrays.stream(containerConfig.getCommands()).collect(joining(" "));
  }
}
