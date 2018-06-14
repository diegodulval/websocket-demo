package com.dulval.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Helpers {

    private Helpers() {
    }

    // Jackson JSON converter
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static SignalMessage getObject(final String message) throws Exception {
        return objectMapper.readValue(message, SignalMessage.class);
    }

    public static String getString(final SignalMessage message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
