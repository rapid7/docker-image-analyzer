package com.rapid7.container.analyzer.docker.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Parses {@link Instant} objects from string representations using a flexible ISO 8601 based
 * format. This parser allows for any of the following "extended" formats:
 * - YYYY-MM-DD'T'hh:mm:ss[.nnn]Z UTC date and time
 * - YYYY-MM-DD'T'hh:mm:ss[.nnn][+|-]hh:mm local date time w/ zone-offset
 * - YYYY-MM-DD'T'hh:mm:ss[.nnn][+|-]hh:mm[zone-id]
 * When an input format does not have a time specified, the default is 12 am.
 */
public class InstantParser {

  public static Instant parse(CharSequence text) {
    return INSTANT_FORMATTER.parse(text, Instant::from);
  }

  public static final DateTimeFormatter INSTANT_FORMATTER =
      new DateTimeFormatterBuilder().parseCaseInsensitive()
      .append(DateTimeFormatter.ISO_LOCAL_DATE)
      .appendLiteral('T')
      .append(DateTimeFormatter.ISO_LOCAL_TIME)
      .optionalStart()
        .appendOffsetId()
        .optionalStart()
          .appendLiteral('[')
          .parseCaseSensitive()
          .appendZoneRegionId()
          .appendLiteral(']')
        .optionalEnd()
      .optionalEnd()
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
      .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
      .toFormatter();
}
