package ru.itmo.spaceships.converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.CrewMember;
import ru.itmo.spaceships.service.ObjectMapperWrapper;

import java.util.List;

/**
 * Converter for writing List of CrewMember to JSONB.
 */
@WritingConverter
@RequiredArgsConstructor
public class CrewMemberListWritingConverter implements Converter<List<CrewMember>, Json> {
    private final ObjectMapperWrapper objectMapper;

    @Override
    @NonNull
    public Json convert(@NonNull List<CrewMember> source) {
        return Json.of(objectMapper.writeValueAsString(source));
    }
}

