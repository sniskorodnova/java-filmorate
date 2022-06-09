package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если mpa рейтинг не найден
 */
public class MpaNotFoundException extends Exception {
    public MpaNotFoundException(String message) {
        super(message);
    }
}
