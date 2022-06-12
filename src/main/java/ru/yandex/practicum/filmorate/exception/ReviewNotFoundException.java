package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если отзыв не найден
 */
public class ReviewNotFoundException extends Exception{
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
