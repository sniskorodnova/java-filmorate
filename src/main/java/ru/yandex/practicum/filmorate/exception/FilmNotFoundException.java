package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если фильм не найден
 */
public class FilmNotFoundException extends Exception {
    public FilmNotFoundException(String message) {
        super(message);
    }
}
