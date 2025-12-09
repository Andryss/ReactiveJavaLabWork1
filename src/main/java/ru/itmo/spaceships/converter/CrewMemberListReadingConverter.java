package ru.itmo.spaceships.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

import java.util.List;

/**
 * Конвертер для чтения списка CrewMember из JSONB.
 */
@ReadingConverter
@RequiredArgsConstructor
public class CrewMemberListReadingConverter implements Converter<Json, List<CrewMember>> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public List<CrewMember> convert(@NonNull Json source) {
        return objectMapper.readValue(source.asString(), new TypeReference<>() {
        });
    }
}

