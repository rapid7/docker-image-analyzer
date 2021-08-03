package com.rapid7.container.analyzer.docker.model.json;

import java.time.Instant;
import java.util.List;

public interface Configuration {

  public List<HistoryJson> getHistory();

  public Instant getCreated();

  public String getOperatingSystem();

  public String getArchitecture();
}
