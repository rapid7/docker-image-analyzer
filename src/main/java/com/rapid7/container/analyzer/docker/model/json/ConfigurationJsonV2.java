
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.StringJoiner;
import static java.util.Objects.requireNonNull;

public class ConfigurationJsonV2 implements Configuration {

  private String architecture;
  private Instant created;
  private String dockerVersion;
  private List<HistoryJson> history;
  private String operatingSystem;
  private FileSystemJson fileSystem;

  @Override
  @JsonProperty("architecture")
  public String getArchitecture() {
    return architecture;
  }

  public void setArchitecture(String architecture) {
    this.architecture = requireNonNull(architecture, "architecture");
  }

  @Override
  @JsonProperty("created")
  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = requireNonNull(created, "created");
  }

  @JsonProperty("docker_version")
  public String getDockerVersion() {
    return dockerVersion;
  }

  public void setDockerVersion(String version) {
    dockerVersion = requireNonNull(version, "version");
  }

  @Override
  @JsonProperty("history")
  public List<HistoryJson> getHistory() {
    return history;
  }

  public void setHistory(List<HistoryJson> history) {
    this.history = requireNonNull(history, "history");
  }

  @Override
  @JsonProperty("os")
  public String getOperatingSystem() {
    return operatingSystem;
  }

  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  @JsonProperty("rootfs")
  public FileSystemJson getFileSystem() {
    return fileSystem;
  }

  public void setFileSysetm(FileSystemJson fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ConfigurationJsonV2.class.getSimpleName() + "[", "]")
        .add("Architecture=" + architecture)
        .add("Created=" + created)
        .add("Version=" + dockerVersion)
        .add("History=" + history)
        .add("Operating System=" + operatingSystem)
        .add("File System=" + fileSystem)
        .toString();
  }
}
