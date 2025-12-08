package ru.itmo.spaceships.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.MaintenanceStatus;

/**
 * Converter for writing MaintenanceStatus enum to VARCHAR.
 */
@WritingConverter
public class MaintenanceStatusWritingConverter implements Converter<MaintenanceStatus, String> {
    @Override
    @NonNull
    public String convert(@NonNull MaintenanceStatus source) {
        return source.name();
    }
}

