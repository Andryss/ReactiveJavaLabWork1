package ru.itmo.spaceships.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Status of maintenance request.
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
     * Check if transition from this status to new status is allowed.
     *
     * @param newStatus new status to transition to
     * @return true if transition is allowed, false otherwise
     */
    public boolean isTransitionAllowed(MaintenanceStatus newStatus) {
        if (this == newStatus) {
            return true; // No change is always allowed
        }
        return transitions.contains(newStatus);
    }

    /**
     * Validate transition to new status and throw exception if not allowed.
     *
     * @param newStatus new status to transition to
     * @throws IllegalArgumentException if transition is not allowed
     */
    public void validateTransition(MaintenanceStatus newStatus) {
        if (!isTransitionAllowed(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Status transition from %s to %s is not allowed", this, newStatus));
        }
    }
}
