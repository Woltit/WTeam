package com.wteam.backend.exception;

import com.wteam.backend.exception.base.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальний обробник винятків (Global Exception Handler).
 * <p>
 * Перехоплює винятки, що виникають на рівні контролерів, та перетворює їх
 * на стандартизовані HTTP-відповіді з використанням об'єкта {@link ErrorResponse}.
 * Забезпечує єдиний формат помилок для всього REST API.
 * </p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Обробляє помилки невірних облікових даних під час автентифікації.
     * Спрацьовує, якщо користувач ввів неправильний пароль.
     *
     * @param e виняток {@link BadCredentialsException}
     * @return HTTP 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Обробляє помилки, коли користувача не знайдено під час автентифікації.
     *
     * @param e виняток {@link UsernameNotFoundException}
     * @return HTTP 401 (Unauthorized)
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    /**
     * Обробляє всі кастомні винятки програми, що успадковуються від {@link AppException}.
     * Використовує HTTP-статус, визначений у самому винятку.
     *
     * @param e виняток {@link AppException}
     * @return HTTP-відповідь із відповідним статусом та повідомленням
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException e) {
        return buildResponse(e.getStatus(), e.getMessage());
    }

    /**
     * Обробляє помилки доступу.
     * Спрацьовує, коли автентифікований користувач намагається виконати дію,
     * на яку йому не вистачає прав (наприклад, відсутня необхідна роль).
     *
     * @param e виняток {@link AccessDeniedException}
     * @return HTTP 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        return buildResponse(HttpStatus.FORBIDDEN, "Access is denied");
    }

    /**
     * Обробляє помилки валідації вхідних DTO (анотації @Valid).
     * Витягує перше повідомлення про помилку з результатів валідації.
     *
     * @param e виняток {@link MethodArgumentNotValidException}
     * @return HTTP 400 (Bad Request) із текстом помилки валідатора
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
    }

    /**
     * Обробляє помилки читання HTTP-запиту.
     * Зазвичай спрацьовує при невалідному форматі JSON або несумісних типах даних.
     *
     * @param e виняток {@link HttpMessageNotReadableException}
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    /**
     * Обробляє базові помилки некоректних аргументів.
     *
     * @param e виняток {@link IllegalArgumentException}
     * @return HTTP 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Обробляє базові конфлікти стану системи або бізнес-логіки.
     *
     * @param e виняток {@link IllegalStateException}
     * @return HTTP 409 (Conflict)
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    /**
     * Обробник-запобіжник для всіх непередбачених помилок сервера (Fallback).
     * Гарантує, що клієнт завжди отримає відповідь у правильному JSON-форматі,
     * навіть якщо виняток не був явно оброблений.
     *
     * @param e загальний виняток {@link Exception}
     * @return HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled exception occurred: ", e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", e.getMessage());
    }

    /**
     * Допоміжний метод для формування базової відповіді з помилкою.
     *
     * @param status HTTP статус
     * @param message Коротке повідомлення про помилку
     * @return Сформований {@link ResponseEntity}
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message));
    }

    /**
     * Допоміжний метод для формування деталізованої відповіді з помилкою.
     *
     * @param status HTTP статус
     * @param message Коротке повідомлення про помилку
     * @param detailedMessage Розширений опис причини помилки
     * @return Сформований {@link ResponseEntity}
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String detailedMessage) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.of(status, message, detailedMessage));
    }
}
