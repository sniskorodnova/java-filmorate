package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * Класс-контроллер для работы с жанрами
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    /**
     * Метод для получения жанра по его id
     */
    @GetMapping("/{genreId}")
    public Genre getById(@PathVariable int genreId) throws GenreNotFoundException {
        log.debug("Входящий запрос на получение жанра по id = {}", genreId);
        return genreService.getById(genreId);
    }

    /**
     * Метод для получения списка всех жанров
     */
    @GetMapping
    public List<Genre> getAll() {
        log.debug("Входящий запрос на получение списка всех жанров");
        return genreService.getAll();
    }

    /**
     * Обработка ошибки, если жанр не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGenreNotFound(final GenreNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
