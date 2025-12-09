package ru.itmo.spaceships.converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.Engine;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

/**
 * Конвертер для записи Engine в JSONB.
 */
@WritingConverter
@RequiredArgsConstructor
public class EngineWritingConverter implements Converter<Engine, Json> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public Json convert(@NonNull Engine source) {
        return Json.of(objectMapper.writeValueAsString(source));
    }
}

