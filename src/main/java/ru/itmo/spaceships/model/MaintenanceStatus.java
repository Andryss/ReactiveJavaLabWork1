package ru.itmo.spaceships.model;

public enum MaintenanceStatus {
    NEW,                 // Новая
    ACCEPTED,            // Принята в работу
    DIAGNOSTICS,         // Диагностика
    APPROVAL,            // Согласование работ
    WAITING_PARTS,       // В ожидании запчастей
    IN_REPAIR,           // В ремонте
    QUALITY_CHECK,       // Тестирование / контроль качества
    READY_FOR_PICKUP,    // Готово / ожидает выдачи
    COMPLETED,           // Завершено / выдано
    CANCELLED            // Отменена
}
