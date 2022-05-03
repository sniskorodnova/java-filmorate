package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если фильм не найден
 */
public class FilmNotFoundException extends RuntimeException {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
