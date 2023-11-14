package com.wanted.safewallet.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {}

    public static String asJsonString(Object value) throws JsonProcessingException {
        return objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(value);
    }
}
