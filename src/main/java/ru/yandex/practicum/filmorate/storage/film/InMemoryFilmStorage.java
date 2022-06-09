package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с хранилищем фильмов. Фильмы сохранаются в мапу
 */
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private HashMap<Long, Film> films = new HashMap<>();

    /**
     * Метод, возвращающий список всех фильмов в хранилище
     */
    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    /**
     * Метод для создания нового фильма
     */
    @Override
    public Film create(Film film) {
        Long generatedId = Film.setIdCounter();
        film.setId(generatedId);
        films.put(generatedId, film);
        return film;
    }

    /**
     * Метод для редактирования существующего фильма
     */
    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Метод для получения информации о фильме по его id
     */
    @Override
    public Film getById (Long id) {
        return films.get(id);
    }

    /**
     * Метод для удаления всех фильмов
     */
    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public void delete(Long id) {
        // заглушка
    }
}
