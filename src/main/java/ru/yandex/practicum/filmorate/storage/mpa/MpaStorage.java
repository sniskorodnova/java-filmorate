package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

/**
 * Интерфейс, описывающий логику работы с хранилищем для mpa рейтинга
 */
public interface MpaStorage {
    Mpa getById(int id);

    List<Mpa> getAll();
}
