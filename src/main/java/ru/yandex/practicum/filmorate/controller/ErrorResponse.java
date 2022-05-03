package ru.yandex.practicum.filmorate.controller;

/**
 * Класс, описывающий вид возвращаемой пользователю ошибки
 */
public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
