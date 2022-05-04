package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс, описывающий логику работы с хранилищем для пользователей
 */
public interface UserStorage {

    User create(User user);

    User update(User user);

    User getById(Long id);

    List<User> getAll();

    void deleteAll();
}
