package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * Класс-контроллер для работы с фильмами
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmService getFilmService() {
        return filmService;
    }

    /**
     * Метод для получения списка всех фильмов
     */
    @GetMapping
    public List<Film> getAll() {
        log.debug("Входящий запрос на получение списка всех фильмов");
        return filmService.getAll();
    }

    /**
     * Метод для создания фильма
     */
    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException, FilmNotFoundException {
        log.debug("Входящий запрос на создание фильма");
        log.debug(film.toString());
        return filmService.create(film);
    }

    /**
     * Метод для редактирования фильма. Чтобы отредактировать фильм, в теле запроса надо передать id фильма,
     * который нужно отредактировать
     */
    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException, FilmNotFoundException {
        log.debug("Входящий запрос на редактирование фильма");
        log.debug(film.toString());
        return filmService.update(film);
    }

    /**
     * Метод для получения фильма по его id
     */
    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Long filmId) throws FilmNotFoundException {
        log.debug("Входящий запрос на получение фильма по id = {}", filmId);
        return filmService.getById(filmId);
    }

    /**
     * Метод для проставления лайка фильму пользователем
     */
    @PutMapping("/{filmId}/like/{userId}")
    public Film likeFilm(@PathVariable Long filmId, @PathVariable Long userId)
            throws UserNotFoundException, FilmNotFoundException {
        log.debug("Входящий запрос на проставление лайка пользователем с id = {} для фильма с id = {}", userId, filmId);
        return filmService.likeFilm(filmId, userId);
    }

    /**
     * Метод для удаления лайка фильму пользователем
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Long filmId, @PathVariable Long userId)
            throws UserNotFoundException, FilmNotFoundException {
        log.debug("Входящий запрос на удаление лайка пользователем с id = {} для фильма с id = {}", userId, filmId);
        return filmService.deleteLike(filmId, userId);
    }

    /**
     * Метод для получения списка count фильмов с наибольшим количеством лайков
     */
    @GetMapping("/popular")
    public List<Film> getCountFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Входящий запрос на получение первых {} популярных фильмов", count);
        return filmService.getCountFilms(count);
    }

    /**
     * Метод удаления фильма по id
     */
    @DeleteMapping("/{filmId}")
    public void delete(@PathVariable Long filmId)
            throws FilmNotFoundException {
        log.debug("Входящий запрос на удаление фильма с id = {}", filmId);
        filmService.delete(filmId);
    }

    /**
     * Обработка ошибки, если фильм не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки сервера
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки, если пользователь не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
