package ru.itmo.spaceships.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Wrapper around ObjectMapper. Isolates exception handling and simplifies serialization.
 */
@Service
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper mapper;

    /**
     * Serialize object to JSON string.
     *
     * @param obj object to serialize
     * @return JSON string
     */
    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    /**
     * Deserialize object from JSON string.
     *
     * @param data JSON string
     * @param type type reference
     * @param <T> type
     * @return deserialized object
     */
    @SneakyThrows
    public <T> T readValue(String data, TypeReference<T> type) {
        return mapper.readValue(data, type);
    }

    /**
     * Deserialize object from JSON string.
     *
     * @param data JSON string
     * @param cls class
     * @param <T> type
     * @return deserialized object
     */
    @SneakyThrows
    public <T> T readValue(String data, Class<T> cls) {
        return mapper.readValue(data, cls);
    }

    /**
     * Deserialize object from JSON string to Map.
     *
     * @param data JSON string
     * @return map
     */
    @SneakyThrows
    public Map<String, Object> readMap(String data) {
        return readValue(data, new TypeReference<>() {
        });
    }

    /**
     * Deserialize JSON tree from string.
     *
     * @param data JSON string
     * @return JSON node
     */
    @SneakyThrows
    public JsonNode readTree(String data) {
        return mapper.readTree(data);
    }
}

