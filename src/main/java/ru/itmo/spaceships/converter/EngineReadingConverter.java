package ru.itmo.spaceships.converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.Engine;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

/**
 * Converter for reading Engine from JSONB.
 */
@ReadingConverter
@RequiredArgsConstructor
public class EngineReadingConverter implements Converter<Json, Engine> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public Engine convert(@NonNull Json source) {
        return objectMapper.readValue(source.asString(), Engine.class);
    }
}

