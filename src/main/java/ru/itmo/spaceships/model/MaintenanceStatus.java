package ru.itmo.spaceships.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Статус заявки на обслуживание.
 */
public enum MaintenanceStatus {
    /**
     * Новая
     */
    NEW,
    /**
     * Принята в работу
     */
    ACCEPTED,
    /**
     * Диагностика
     */
    DIAGNOSTICS,
    /**
     * Согласование работ
     */
    APPROVAL,
    /**
     * В ожидании запчастей
     */
    WAITING_PARTS,
    /**
     * В ремонте
     */
    IN_REPAIR,
    /**
     * Тестирование / контроль качества
     */
    QUALITY_CHECK,
    /**
     * Готово / ожидает выдачи
     */
    READY_FOR_PICKUP,
    /**
     * Завершено / выдано
     */
    COMPLETED,
    /**
     * Отменена
     */
    CANCELLED;

    private Set<MaintenanceStatus> transitions;

    static {
        NEW.transitions = EnumSet.of(
                ACCEPTED, CANCELLED
        );
        ACCEPTED.transitions = EnumSet.of(
                DIAGNOSTICS, CANCELLED
        );
        DIAGNOSTICS.transitions = EnumSet.of(
                APPROVAL, CANCELLED
        );
        APPROVAL.transitions = EnumSet.of(
                WAITING_PARTS, IN_REPAIR, CANCELLED
        );
        WAITING_PARTS.transitions = EnumSet.of(
                IN_REPAIR, CANCELLED
        );
        IN_REPAIR.transitions = EnumSet.of(
                QUALITY_CHECK, CANCELLED
        );
        QUALITY_CHECK.transitions = EnumSet.of(
                READY_FOR_PICKUP, CANCELLED
        );
        READY_FOR_PICKUP.transitions = EnumSet.of(
                COMPLETED, CANCELLED
        );
        COMPLETED.transitions = EnumSet.noneOf(MaintenanceStatus.class);
        CANCELLED.transitions = EnumSet.noneOf(MaintenanceStatus.class);
    }

    /**
     * Проверяет, разрешён ли переход из текущего статуса в новый статус.
     *
     * @param newStatus новый статус для перехода
     * @return true, если переход разрешён, false в противном случае
     */
    public boolean isTransitionAllowed(MaintenanceStatus newStatus) {
        if (this == newStatus) {
            return true; // Отсутствие изменений всегда разрешено
        }
        return transitions.contains(newStatus);
    }

    /**
     * Валидирует переход в новый статус и выбрасывает исключение, если переход не разрешён.
     *
     * @param newStatus новый статус для перехода
     * @throws IllegalArgumentException если переход не разрешён
     */
    public void validateTransition(MaintenanceStatus newStatus) {
        if (!isTransitionAllowed(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Status transition from %s to %s is not allowed", this, newStatus));
        }
    }
}
