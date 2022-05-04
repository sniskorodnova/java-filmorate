package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение для валидаций
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
