package ru.itmo.spaceships.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.exception.BaseException;
import ru.itmo.spaceships.generated.model.ErrorObject;

/**
 * Глобальный обработчик исключений для REST API.
 * Преобразует исключения в стандартизированные ответы ErrorObject.
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    /**
     * Обрабатывает BaseException и возвращает ErrorObject.
     */
    @ExceptionHandler(BaseException.class)
    public Mono<ResponseEntity<ErrorObject>> handleBaseException(BaseException ex) {
        log.error("BaseException: code={}, message={}, humanMessage={}",
                ex.getCode(), ex.getMessage(), ex.getHumanMessage());
        
        ErrorObject errorObject = new ErrorObject();
        errorObject.setCode(ex.getCode());
        errorObject.setMessage(ex.getMessage());
        errorObject.setHumanMessage(ex.getHumanMessage());
        
        HttpStatus status = HttpStatus.valueOf(ex.getCode());
        return Mono.just(ResponseEntity.status(status).body(errorObject));
    }

    /**
     * Обрабатывает IllegalArgumentException и преобразует в BaseException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorObject>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        
        ErrorObject errorObject = new ErrorObject();
        errorObject.setCode(400);
        errorObject.setMessage("validation.error");
        errorObject.setHumanMessage(ex.getMessage() != null ? ex.getMessage() : "Ошибка валидации");
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorObject));
    }

    /**
     * Обрабатывает все остальные необработанные исключения.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorObject>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        
        ErrorObject errorObject = new ErrorObject();
        errorObject.setCode(500);
        errorObject.setMessage("internal.error");
        errorObject.setHumanMessage("Что-то пошло не так...");
        
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorObject));
    }
}

