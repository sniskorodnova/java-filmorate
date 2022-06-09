package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

/**
 * Класс-контроллер для работы с mpa рейтингом
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    /**
     * Метод для получения mpa рейтинга по его id
     */
    @GetMapping("/{mpaId}")
    public Mpa getById(@PathVariable int mpaId) throws MpaNotFoundException {
        log.debug("Входящий запрос на получение mpa рейтинга по id = {}", mpaId);
        return mpaService.getById(mpaId);
    }

    /**
     * Метод для получения всех значений рейтинга mpa
     */
    @GetMapping
    public List<Mpa> getAll() {
        log.debug("Входящий запрос на получение списка всего рейтинга");
        return mpaService.getAll();
    }

    /**
     * Обработка ошибки, если mpa рейтинг не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMpaNotFound(final MpaNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
