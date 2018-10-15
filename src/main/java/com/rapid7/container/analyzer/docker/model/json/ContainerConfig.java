
/**
 *
 */
package com.rapid7.container.analyzer.docker.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.StringJoiner;

public class ContainerConfig {

  private String[] commands;

  @JsonProperty("Cmd")
  public String[] getCommands() {
    return commands;
  }

  public ContainerConfig setCommands(String[] commands) {
    this.commands = commands;
    return this;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ContainerConfig.class.getSimpleName() + "[", "]")
        .add("Commands=" + Arrays.toString(commands))
        .toString();
  }
}
