package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-сервис, отвечающий за логику работы с фильмами. Для реализации логики используются методы хранилищ
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    /**
     * Метод для получения списка всех фильмов
     */
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    /**
     * Метод создания нового фильма. Перед добавлением фильм валидируется
     */
    public Film create(Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    /**
     * Метод редактирования существующего фильма. Перед редактированием новый объект фильма валидируется
     */
    public Film update(Film film) {
        validate(film);
        return filmStorage.update(film);
    }

    /**
     * Метод получения фильма по его id
     */
    public Film getById(Long filmId) {
        final Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        }
        return film;
    }

    /**
     * Метод для получения списка первых count фильмов в порядке убывания количества лайков
     */
    public List<Film> getCountFilms(int count) {
        List<Film> films = filmStorage.getAll();
        films.sort(new FilmComparator());
        return films.stream().limit(count).collect(Collectors.toList());
    }

    /**
     * Метод добавления лайка фильму от пользователя
     */
    public Film likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);

        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        } else if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else {
            Set<Long> idLikesFilm = new HashSet<>();
            if (film.getLikesFromUsers() != null) {
                idLikesFilm = film.getLikesFromUsers();
            }
            idLikesFilm.add(userId);
            film.setLikesFromUsers(idLikesFilm);
            return filmStorage.update(film);
        }
    }

    /**
     * Метод удаления лайка пользователя у фильма
     */
    public Film deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        } else if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else {
            Set<Long> idLikesFilm = new HashSet<>();
            if (film.getLikesFromUsers() != null) {
                idLikesFilm = film.getLikesFromUsers();
            }
            idLikesFilm.remove(userId);
            film.setLikesFromUsers(idLikesFilm);
            return filmStorage.update(film);
        }
    }

    /**
     * Метод для валидации данных при создании и редактировании фильма. Если какая-либо валидация не пройдена,
     * то выбрасывается исключение ValidationException
     */
    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Имя фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.info("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.info("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю");
        }
    }
}
