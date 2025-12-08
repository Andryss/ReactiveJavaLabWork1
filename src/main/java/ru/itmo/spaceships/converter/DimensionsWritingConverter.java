package ru.itmo.spaceships.converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.Dimensions;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

@WritingConverter
@RequiredArgsConstructor
public class DimensionsWritingConverter implements Converter<Dimensions, Json> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public Json convert(@NonNull Dimensions source) {
        return Json.of(objectMapper.writeValueAsString(source));
    }
}
