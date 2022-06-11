package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.SearchService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public List<Film> findFilmByParam(@RequestParam String query,
                                      @RequestParam List<String> by) {
        log.debug(String.format("Запрос на поиск фильмов по параметрам: query = %s, by = %s", query, by));
        return searchService.searchFilmByParam(query, by);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedException(final UnsupportedOperationException e) {
        return new ErrorResponse(e.getMessage());
    }
}
