package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с хранилищем пользователей. Пользователи сохранаются в мапу
 */
@Slf4j
@Component
@Data
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Long, User> users = new HashMap<>();

    /**
     * Метод для создания нового пользователя
     */
    @Override
    public User create(User user) {
        Long generatedId = User.setIdCounter();
        user.setId(generatedId);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(generatedId, user);
        return user;
    }

    /**
     * Метод для редактирования существующего пользователя
     */
    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Метод для получения пользователя по его id
     */
    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    /**
     * Метод для получения списка всех пользователей
     */
    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Метод для удаления всех пользователей
     */
    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public void delete(Long id) {
        // Заглушка
    }
}
