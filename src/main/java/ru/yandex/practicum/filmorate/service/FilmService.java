package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.genrefilm.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.userfilmlikes.UserFilmLikesStorage;

import java.time.LocalDate;
import java.util.*;

/**
 * Класс-сервис, отвечающий за логику работы с фильмами
 */
@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserFilmLikesStorage userFilmLikesStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("userFilmLikesDbStorage") UserFilmLikesStorage userFilmLikesStorage,
                       FilmGenreStorage filmGenreStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.userFilmLikesStorage = userFilmLikesStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.genreStorage = genreStorage;
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
    public Film create(Film film) throws ValidationException, FilmNotFoundException {
        validate(film);
        Film filmWithId = filmStorage.create(film);
        film.setId(filmWithId.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                filmGenreStorage.create(film.getId(), genre.getId());
            }
        }
        return film;
    }

    /**
     * Метод редактирования существующего фильма. Перед редактированием новый объект фильма валидируется
     */
    public Film update(Film film) throws ValidationException, FilmNotFoundException {
        if (filmStorage.getById(film.getId()) != null) {
            validate(film);
            filmGenreStorage.deleteByFilmId(film.getId());
            filmStorage.update(film);
            if (film.getGenres() != null) {
                for (Genre genre : film.getGenres()) {
                    filmGenreStorage.create(film.getId(), genre.getId());
                }
            }
            return film;
        } else {
            throw new FilmNotFoundException("Film with id = " + film.getId() + " not found");
        }
    }

    /**
     * Метод удаления фильма
     */
    public void delete(Long id) throws FilmNotFoundException {
        if (filmStorage.getById(id) != null) {
            filmStorage.delete(id);
        } else {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        }
    }

    /**
     * Метод получения фильма по его id
     */
    public Film getById(Long filmId) throws FilmNotFoundException {
        final Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        } else {
            TreeSet<Integer> genresId = filmGenreStorage.getByFilmId(filmId);
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            if (!genresId.isEmpty()) {
                for (Integer genreId : genresId) {
                    genres.add(genreStorage.getById(genreId));
                }
                film.setGenres(genres);
            } else {
                film.setGenres(null);
            }
        }
        return film;
    }

    /**
     * Метод для получения списка первых count фильмов в порядке убывания количества лайков
     */
    public List<Film> getCountFilms(int count) {
        return userFilmLikesStorage.getCount(count);
    }

    /**
     * Метод для получения общих с другом фильмов с сортировкой по их популярности
     */
    public List<Film> getCommonFilms(long userId, long friendId)
            throws UserNotFoundException {

        User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }

        User friend = userStorage.getById(friendId);
        if (friend == null) {
            throw new UserNotFoundException("User with id = " + friendId + " not found");
        }

        // Владимир Иванов сказал, что требования к друзъям больше нет
        //if ((!user.getFriends().contains(friendId)) || (!friend.getFriends().contains(userId))) {
        //    throw new ValidationException("Users must be friends");
        //}

        return userFilmLikesStorage.getCommonFilms(userId, friendId);

    }

    /**
     * Метод добавления лайка фильму от пользователя
     */
    public Film likeFilm(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film film = filmStorage.getById(filmId);

        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        } else if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else {
            userFilmLikesStorage.saveLike(filmId, userId);
            return filmStorage.update(film);
        }
    }

    /**
     * Метод удаления лайка пользователя у фильма
     */
    public Film deleteLike(Long filmId, Long userId) throws UserNotFoundException, FilmNotFoundException {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + filmId + " not found");
        } else if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else {
            userFilmLikesStorage.removeLike(filmId, userId);
            return filmStorage.update(film);
        }
    }

    /**
     * Метод для валидации данных при создании и редактировании фильма. Если какая-либо валидация не пройдена,
     * то выбрасывается исключение ValidationException
     */
    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Имя фильма не может быть пустым");
        } else if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
            log.debug("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Описание фильма должно содержать символы и не может быть "
                    + "больше 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.debug("Произошла ошибка валидации для фильма:");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю");
        } else if (film.getMpa() == null) {
            log.debug("Произошла ошибка валидации для фильма:");
            throw new ValidationException("MPAA рейтинг фильма должен быть заполнен");
        }
    }
}
