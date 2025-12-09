package ru.itmo.spaceships.converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.Dimensions;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

/**
 * Конвертер для чтения Dimensions из JSONB.
 */
@ReadingConverter
@RequiredArgsConstructor
public class DimensionsReadingConverter implements Converter<Json, Dimensions> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public Dimensions convert(@NonNull Json source) {
        return objectMapper.readValue(source.asString(), Dimensions.class);
    }
}

