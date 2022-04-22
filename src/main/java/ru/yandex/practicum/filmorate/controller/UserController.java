package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс-контроллер пользователя. Все пользователи хранятся в хэш-мапе с ключом Id
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private HashMap<Long, User> users = new HashMap<>();

    /**
     * Метод для получения списка всех пользователей
     */
    @GetMapping
    public List<User> getAll() {
        log.info("Входящий запрос на получение списка всех пользователей");
        return new ArrayList<>(users.values());
    }

    /**
     * Метод для создания пользователя. Пользователь создается только если будут пройдены все валидации данных
     */
    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Входящий запрос на создание пользователя");
        log.info(user.toString());
        validate(user);
        Long generatedId = User.setIdCounter();
        user.setId(generatedId);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(generatedId, user);
        return user;
    }

    /**
     * Метод для редактирования пользователя. Чтобы отредактировать пользователя, в теле запроса надо передать
     * id пользователя, который нужно отредактировать. Пользоаватель будет отредактирован, только если будут
     * пройдены все валидации данных
     */
    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Входящий запрос на редактирование пользователя");
        log.info(user.toString());
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    /**
     * Метод для валидации данных при создании и редактрования пользователя. Если какая-либо валидация не пройдена,
     * то выбрасывается исключение ValidationException
     */
    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.info("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("Почта пользователя не может быть пустой и должна содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.info("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("Логин пользователя не может быть пустым и не должен содержать пробелов");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("День рождения пользователя не может быть в будущем");
        }
    }
}
