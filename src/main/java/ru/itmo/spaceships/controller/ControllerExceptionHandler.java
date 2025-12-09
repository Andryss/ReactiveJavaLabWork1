package ru.itmo.spaceships.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.itmo.spaceships.exception.BaseException;
import ru.itmo.spaceships.exception.Errors;
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

        return handleBaseExceptionInternal(ex);
    }

    /**
     * Обрабатывает ошибки валидации ConstraintViolationException.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorObject>> handleConstraintViolation(ConstraintViolationException ex) {
        log.error("ConstraintViolationException: {}", ex.getMessage(), ex);
        return handleBaseExceptionInternal(Errors.validationError(ex.getMessage()));
    }

    /**
     * Обрабатывает ошибки чтения HTTP сообщения (например, невалидный JSON).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<ErrorObject>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException: {}", ex.getMessage(), ex);
        String message = ex.getMessage();
        if (message != null && message.contains("JSON")) {
            return handleBaseExceptionInternal(Errors.invalidJsonError());
        }
        return handleBaseExceptionInternal(Errors.invalidRequestBodyError(message));
    }

    /**
     * Обрабатывает ошибки валидации аргументов метода (MethodArgumentNotValidException).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<ErrorObject>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage(), ex);
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return handleBaseExceptionInternal(Errors.validationError(message));
    }

    /**
     * Обрабатывает ошибки валидации WebFlux (WebExchangeBindException).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorObject>> handleWebExchangeBind(WebExchangeBindException ex) {
        log.error("WebExchangeBindException: {}", ex.getMessage(), ex);
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return handleBaseExceptionInternal(Errors.validationError(message));
    }

    /**
     * Обрабатывает ошибки несоответствия типа аргумента метода.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Mono<ResponseEntity<ErrorObject>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException: {}", ex.getMessage(), ex);
        String expectedType = ex.getRequiredType().getSimpleName();
        return handleBaseExceptionInternal(Errors.invalidParameterTypeError(ex.getName(), expectedType));
    }

    /**
     * Обрабатывает ошибки ввода WebFlux (ServerWebInputException).
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorObject>> handleServerWebInput(ServerWebInputException ex) {
        log.error("ServerWebInputException: {}", ex.getMessage(), ex);
        return handleBaseExceptionInternal(Errors.invalidInputError(ex.getReason()));
    }

    /**
     * Обрабатывает ResponseStatusException (Spring статусные исключения).
     */
    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorObject>> handleResponseStatus(ResponseStatusException ex) {
        log.error("ResponseStatusException: status={}, reason={}", ex.getStatusCode(), ex.getReason(), ex);
        int code = ex.getStatusCode().value();
        String message = ex.getReason();
        return handleBaseExceptionInternal(Errors.responseStatusError(code, message));
    }

    /**
     * Обрабатывает все остальные необработанные исключения.
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorObject>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);

        return handleBaseExceptionInternal(Errors.unhandledExceptionError());
    }

    /**
     * Обрабатывает BaseException и возвращает ErrorObject.
     */
    private static Mono<ResponseEntity<ErrorObject>> handleBaseExceptionInternal(BaseException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setCode(ex.getCode());
        errorObject.setMessage(ex.getMessage());
        errorObject.setHumanMessage(ex.getHumanMessage());

        HttpStatus status = HttpStatus.valueOf(errorObject.getCode());
        return Mono.just(ResponseEntity.status(status).body(errorObject));
    }

}

