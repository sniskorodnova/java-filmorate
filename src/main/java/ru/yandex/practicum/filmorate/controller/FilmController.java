package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * Класс-контроллер для работы с фильмами
 */
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmService getFilmService() {
        return filmService;
    }

    @Autowired
    public FilmController(FilmService filmService){
        this.filmService = filmService;
    }

    /**
     * Метод для получения списка всех фильмов
     */
    @GetMapping
    public List<Film> getAll() {
        log.info("Входящий запрос на получение списка всех фильмов");
        return filmService.getAll();
    }

    /**
     * Метод для создания фильма
     */
    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Входящий запрос на создание фильма");
        log.info(film.toString());
        return filmService.create(film);
    }

    /**
     * Метод для редактирования фильма. Чтобы отредактировать фильм, в теле запроса надо передать id фильма,
     * который нужно отредактировать
     */
    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Входящий запрос на редактирование фильма");
        log.info(film.toString());
        return filmService.update(film);
    }

    /**
     * Метод для получения фильма по его id
     */
    @GetMapping("/{filmId}")
    public Film getById(@PathVariable Long filmId) {
        log.info("Входящий запрос на получение фильма по id = " + filmId);
        return filmService.getById(filmId);
    }

    /**
     * Метод для проставления лайка фильму пользователем
     */
    @PutMapping("/{filmId}/like/{userId}")
    public Film likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Входящий запрос на проставление лайка пользователем с id = " + userId + " для фильма"
                + " с id = " + filmId);
        return filmService.likeFilm(filmId, userId);
    }

    /**
     * Метод для удаления лайка фильму пользователем
     */
    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Входящий запрос на удаление лайка пользователем с id = " + userId + " для фильма"
                + " с id = " + filmId);
        return filmService.deleteLike(filmId, userId);
    }

    /**
     * Метод для получения списка count фильмов с наибольшим количеством лайков
     */
    @GetMapping("/popular")
    public List<Film> getCountFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Входящий запрос на получение первых " + count + " популярных фильмов");
        return filmService.getCountFilms(count);
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
