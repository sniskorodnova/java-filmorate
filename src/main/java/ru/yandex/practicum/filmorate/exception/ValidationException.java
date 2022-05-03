package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение для валидаций
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
