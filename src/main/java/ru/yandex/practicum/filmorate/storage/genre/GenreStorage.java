package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

/**
 * Интерфейс, описывающий логику работы с хранилищем для жанров
 */
public interface GenreStorage {
    Genre getById(int id);

    List<Genre> getAll();
}
