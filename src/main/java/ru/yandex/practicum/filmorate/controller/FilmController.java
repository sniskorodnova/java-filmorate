package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс-контроллер фильма. Все фильмы хранятся в хэш-мапе с ключом Id
 */
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private HashMap<Long, Film> films = new HashMap<>();

    /**
     * Метод для получения списка всех фильмов
     */
    @GetMapping
    public List<Film> getAll() {
        log.info("Входящий запрос на получение списка всех фильмов");
        return new ArrayList<>(films.values());
    }

    /**
     * Метод для создания фильма. Фильм создается только если будут пройдены все валидации данных
     */
    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Входящий запрос на создание фильма");
        log.info(film.toString());
        validate(film);
        Long generatedId = Film.setIdCounter();
        film.setId(generatedId);
        films.put(generatedId, film);
        return film;
    }

    /**
     * Метод для редактирования фильма. Чтобы отредактировать фильм, в теле запроса надо передать id фильма,
     * который нужно отредактировать. Фильм будет отредактирован только если будут пройдены все валидации данных
     */
    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("Входящий запрос на редактирование фильма");
        log.info(film.toString());
        validate(film);
        films.put(film.getId(), film);
        return film;
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
