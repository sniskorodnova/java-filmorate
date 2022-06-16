package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

/**
 * Класс-сервис, отвечающий за логику работы с жанрами
 */
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    /**
     * Метод для получения жанра по его id
     */
    public Genre getById(int genreId) throws GenreNotFoundException {
        final Genre genre = genreStorage.getById(genreId);
        if (genre == null) {
            throw new GenreNotFoundException("Genre with id = " + genreId + " not found");
        }
        return genre;
    }

    /**
     * Метод для получения списка всех жанров
     */
    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}
