package ru.yandex.practicum.filmorate.controller;

/**
 * Класс, описывающий исключение для валидаций
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
