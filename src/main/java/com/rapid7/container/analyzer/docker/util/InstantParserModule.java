package com.rapid7.container.analyzer.docker.util;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import java.time.Instant;

public class InstantParserModule extends SimpleModule {

  public InstantParserModule() {
    super(PackageVersion.VERSION);
    addDeserializer(Instant.class, InstantCustomDeserializer.INSTANT);
  }

}
