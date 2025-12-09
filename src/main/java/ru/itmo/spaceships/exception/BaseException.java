package ru.itmo.spaceships.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Базовое исключение для обработки ошибок API.
 * Содержит код ошибки, текстовый идентификатор и человеко-читаемое сообщение.
 */
@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final int code;
    private final String message;
    private final String humanMessage;
}

