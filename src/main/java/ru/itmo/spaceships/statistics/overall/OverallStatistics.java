package ru.itmo.spaceships.statistics.overall;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LongSummaryStatistics;
import java.util.Map;

import lombok.Data;

@Data
public class OverallStatistics {
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private Map<String, Long> countByManufacturer = new HashMap<>();
    private Map<String, Long> countByFuelType = new HashMap<>();
    private Map<String, Long> countByDate = new HashMap<>();
    private LongSummaryStatistics aggregateMaxSpeed = new LongSummaryStatistics();
    private LongSummaryStatistics aggregateCrewMembers = new LongSummaryStatistics();
    private LongSummaryStatistics aggregateLength = new LongSummaryStatistics();
    private Map<String, LongSummaryStatistics> aggregateCrewByShipType = new HashMap<>();
}