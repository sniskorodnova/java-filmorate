package ru.yandex.practicum.filmorate.storage.userfilmlikes;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmComparator;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс, имплементирующий интерфейс для работы с хранилищем лайков по фильмам от пользователей
 */
@Component
public class InMemoryUserFilmLikesStorage implements UserFilmLikesStorage {
    private final FilmStorage filmStorage;

    public InMemoryUserFilmLikesStorage(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    /**
     * Метод для сохранения лайка для фильма от пользователя
     */
    @Override
    public void saveLike(Long filmId, Long userId) {
        Set<Long> idLikesFilm = new HashSet<>();

        if (filmStorage.getById(filmId).getLikesFromUsers() != null) {
            idLikesFilm = filmStorage.getById(filmId).getLikesFromUsers();
        }
        idLikesFilm.add(userId);
        filmStorage.getById(filmId).setLikesFromUsers(idLikesFilm);
    }

    /**
     * Метод для удаления лайка для фильма от пользователя
     */
    @Override
    public void removeLike(Long filmId, Long userId) {
        Set<Long> idLikesFilm = new HashSet<>();

        if (filmStorage.getById(filmId).getLikesFromUsers() != null) {
            idLikesFilm = filmStorage.getById(filmId).getLikesFromUsers();
        }
        idLikesFilm.remove(userId);
        filmStorage.getById(filmId).setLikesFromUsers(idLikesFilm);
    }

    /**
     * Метод для получения count первых фильмов по количеству лайков
     */
    @Override
    public List<Film> getCount(int count) {
        List<Film> films = filmStorage.getAll();
        films.sort(new FilmComparator());
        return films.stream().limit(count).collect(Collectors.toList());
    }
}
