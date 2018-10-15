package com.rapid7.container.analyzer.docker.model.image.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ImageModelObjectMapper extends ObjectMapper {

  public ImageModelObjectMapper() {
    enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
    disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    registerModule(new JavaTimeModule());
    registerModule(new JacksonImageModelModule());
  }
}
