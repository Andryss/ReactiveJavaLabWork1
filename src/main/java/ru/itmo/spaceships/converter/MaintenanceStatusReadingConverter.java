package ru.itmo.spaceships.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.NonNull;
import ru.itmo.spaceships.model.MaintenanceStatus;

/**
 * Конвертер для чтения enum MaintenanceStatus из VARCHAR.
 */
@ReadingConverter
public class MaintenanceStatusReadingConverter implements Converter<String, MaintenanceStatus> {
    @Override
    @NonNull
    public MaintenanceStatus convert(@NonNull String source) {
        return MaintenanceStatus.valueOf(source);
    }
}

