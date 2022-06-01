package ru.yandex.practicum.filmorate.storage.userfilmlikes;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс, описывающий логику работы с хранилищем для лайков фильмов от пользователей
 */
public interface UserFilmLikesStorage {
    void saveLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getCount(int count);
}
