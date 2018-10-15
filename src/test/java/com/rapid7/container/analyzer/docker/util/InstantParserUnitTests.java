package com.rapid7.container.analyzer.docker.util;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.api.Test;
import static com.rapid7.container.analyzer.docker.util.InstantParser.parse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InstantParserUnitTests {

  @Test
  public void parseNegativeZoneOffset() {
    assertThat(parse("2018-05-22T11:43:10.661876938-07:00"), equalTo(instant("2018-05-22T18:43:10.661876938Z")));
  }

  @Test
  public void parseShortNegativeOffset() {
    assertThat(parse("2018-05-22T11:43:10-07:00"), equalTo(instant("2018-05-22T18:43:10Z")));
  }

  @Test
  public void parsePositiveOffset() {
    assertThat(parse("2018-05-22T11:43:10.661876938+05:00"), equalTo(instant("2018-05-22T06:43:10.661876938Z")));
  }

  @Test
  public void parseShortPositiveOffset() {
    assertThat(parse("2018-05-22T11:43:10+05:00"), equalTo(instant("2018-05-22T06:43:10Z")));
  }

  @Test
  public void parseUtc() {
    assertThat(parse("2018-05-22T18:43:10.661876938"), equalTo(instant("2018-05-22T18:43:10.661876938Z")));
  }

  @Test
  public void parseShortUtc() {
    assertThat(parse("2018-05-22T18:43:10"), equalTo(instant("2018-05-22T18:43:10Z")));
  }

  @Test
  public void parseZoneOffset() {
    assertThat(parse("2018-05-22T18:43:10.661876938Z"), equalTo(instant("2018-05-22T18:43:10.661876938Z")));
  }

  @Test
  public void parseTimeWithoutNanosecondValue() {
    assertThat(parse("2018-05-22T18:43:10Z"), instanceOf(Instant.class));
  }

  @Test
  public void parseTimeShortNanosecondValue() {
    assertThat(parse("2018-05-22T11:43:10.2Z"), instanceOf(Instant.class));
  }

  @Test
  public void parseTimeWithoutSecondValue() {
    assertThat(parse("2018-05-22T11:43"), instanceOf(Instant.class));
  }

  @Test
  public void parseTimeHourValueOnly() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11"));
  }

  @Test
  public void parseInvalidMinuteValue() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:4"));
  }

  @Test
  public void parseInvalidNegativeTimeOffset() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:43:10.661876938-18:01"));
  }

  @Test
  public void parseInvalidPositiveTimeOffset() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:43:10.661876938+19:00"));
  }

  @Test
  public void parseInvalidValueNegativeTimeOffset() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:43:10.661876938-10:71"));
  }

  @Test
  public void parseInvalidValuePositiveTimeOffset() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:43:10.661876938+04:90"));
  }

  @Test
  public void parseInvalidFormatZoneTimeOffset() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T11:43:10.661876938+-04:20"));
  }

  @Test
  public void parseOutOfOrderCharacters() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T:11-43:10.66"));
  }

  @Test
  public void parseUnknownCharacters() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22T!11:43:10.661876938+04:20"));
  }

  @Test
  public void parseDateOnly() {
    assertThrows(DateTimeParseException.class, () -> parse("2018-05-22"));
  }

  @Test
  public void parseEmptyString() {
    assertThrows(DateTimeParseException.class, () -> parse(""));
  }

  @Test
  public void parseNull() {
    assertThrows(NullPointerException.class, () -> parse(null));
  }

  private Instant instant(String string) {
    return Instant.parse(string);
  }
}
