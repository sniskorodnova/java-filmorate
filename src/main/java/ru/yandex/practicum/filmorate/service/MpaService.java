package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

/**
 * Класс-сервис, отвечающий за логику работы с mpa рейтингом
 */
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    /**
     * Метод для получения mpa рейтинга по его id
     */
    public Mpa getById(int mpaId) throws MpaNotFoundException {
        final Mpa mpa = mpaStorage.getById(mpaId);
        if (mpa == null) {
            throw new MpaNotFoundException("Mpa with id = " + mpaId + " not found");
        }
        return mpa;
    }

    /**
     * Метод для получения списка значений mpa рейтинга
     */
    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
