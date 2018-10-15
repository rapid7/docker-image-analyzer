package com.rapid7.container.analyzer.docker.util;

import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class InstantCustomDeserializer extends InstantDeserializer<Instant> {

  public static final InstantCustomDeserializer INSTANT = new InstantCustomDeserializer(Instant.class, InstantParser.INSTANT_FORMATTER);

  protected InstantCustomDeserializer(Class<Instant> supportedType, DateTimeFormatter formatter) {
    super(supportedType, formatter, Instant::from, a -> Instant.ofEpochMilli(a.value), a -> Instant.ofEpochSecond(a.integer, a.fraction), null, true);
  }
}
