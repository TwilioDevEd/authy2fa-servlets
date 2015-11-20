package com.authy.lib;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Mapper provides functionality for mapping a JSON to a basic
 * POJO (Plain Old Java Object).
 *
 * Usage is of form of:
 * <pre>
 *     Mapper mapper = new Mapper();
 *     Result result = mapper.fromJsonToObject(aJSONString, Result.class)
 * </pre>
 *
 * @author Agustin Camino
 */
public class Mapper {

    /**
     * Method to deserialize JSON content into a Java type.
     *
     * @param json        The JSON content
     * @param valueType   The type class of the target POJO
     * @param <T>         The type of the target POJO
     *
     * @return The Java type
     *
     * @throws IOException
     */
    public <T> T fromJsonToObject(String json, Class<T> valueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, valueType);
    }
}
