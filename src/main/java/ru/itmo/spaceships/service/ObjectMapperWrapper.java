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
 * Обёртка вокруг ObjectMapper. Изолирует обработку исключений и упрощает сериализацию.
 */
@Service
@RequiredArgsConstructor
public class ObjectMapperWrapper {

    private final ObjectMapper mapper;

    /**
     * Сериализует объект в JSON строку.
     *
     * @param obj объект для сериализации
     * @return JSON строка
     */
    public String writeValueAsString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return String.valueOf(obj);
        }
    }

    /**
     * Десериализует объект из JSON строки.
     *
     * @param data JSON строка
     * @param type ссылка на тип
     * @param <T> тип
     * @return десериализованный объект
     */
    @SneakyThrows
    public <T> T readValue(String data, TypeReference<T> type) {
        return mapper.readValue(data, type);
    }

    /**
     * Десериализует объект из JSON строки.
     *
     * @param data JSON строка
     * @param cls класс
     * @param <T> тип
     * @return десериализованный объект
     */
    @SneakyThrows
    public <T> T readValue(String data, Class<T> cls) {
        return mapper.readValue(data, cls);
    }

    /**
     * Десериализует объект из JSON строки в Map.
     *
     * @param data JSON строка
     * @return карта
     */
    @SneakyThrows
    public Map<String, Object> readMap(String data) {
        return readValue(data, new TypeReference<>() {
        });
    }

    /**
     * Десериализует JSON дерево из строки.
     *
     * @param data JSON строка
     * @return JSON узел
     */
    @SneakyThrows
    public JsonNode readTree(String data) {
        return mapper.readTree(data);
    }
}

