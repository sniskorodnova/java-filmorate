package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private HashMap<Long, Film> films = new HashMap<>();

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
        Long generatedId = Film.setCounter();
        film.setId(generatedId);
        films.put(generatedId, film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        films.put(film.getId(), film);
        return film;
    }
}
