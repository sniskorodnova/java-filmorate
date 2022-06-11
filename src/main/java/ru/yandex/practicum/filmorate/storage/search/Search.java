package ru.yandex.practicum.filmorate.storage.search;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс определяет методы поиска различной информации в кинопоиске
 * Пока реализован только поиск фильмов
 */
public interface Search {
    List<Film> searchFilmByParam(String query, KindOfSearchFilm by);
}
