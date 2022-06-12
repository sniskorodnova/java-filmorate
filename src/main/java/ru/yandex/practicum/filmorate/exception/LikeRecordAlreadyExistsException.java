package ru.yandex.practicum.filmorate.exception;

/**
 * Класс, описывающий исключение, если добавляемая запись в таблице лайков уже есть
 */
public class LikeRecordAlreadyExistsException extends Exception {
    public LikeRecordAlreadyExistsException(String message) {
        super(message);
    }
}
