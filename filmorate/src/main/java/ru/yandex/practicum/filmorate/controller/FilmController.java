package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Long, Film> films = new HashMap<>();
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getAll() {
        ArrayList<Film> filmList = new ArrayList<>();

        for (Long id : films.keySet()) {
            filmList.add(films.get(id));
        }

        return filmList;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Имя фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getFilmDuration().isNegative() || film.getFilmDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю");
        } else {
            Long generatedId = Film.setCounter();
            film.setId(generatedId);
            films.put(generatedId, film);
            return film;
        }
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Имя фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getFilmDuration().isNegative() || film.getFilmDuration().isZero()) {
            throw new ValidationException("Продолжительность фильма не может быть отрицательной или равной нулю");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }
}
