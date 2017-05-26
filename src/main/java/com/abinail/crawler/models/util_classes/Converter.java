package com.abinail.crawler.models.util_classes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Converter {

  public static String toJSON(Object user) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(user);
  }

  public static <T> T toJavaObject(String json, Class<T> clazz) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, clazz);
  }
}
