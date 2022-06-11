package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если жанр не найден
 */
public class GenreNotFoundException extends Exception {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
