package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * Класс-контроллер для работы с пользователями
 */
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserService getUserService() {
        return userService;
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Метод для получения пользователя по его id
     */
    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) {
        log.info("Входящий запрос на получение информации по пользователю с id = " + userId);
        return userService.getById(userId);
    }

    /**
     * Метод для добавления пользователя в друзья другому пользователю
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Входящий запрос на добавление в друзья пользователя с id = " + friendId + " пользователю "
                + "c id = " + userId);
        return userService.addToFriends(userId, friendId);
    }

    /**
     * Метод для удаления пользователя из друзей другого пользователя
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        log.info("Входящий запрос на удаление из друзей пользователя с id = " + friendId + " у пользователя "
                + "c id = " + userId);
        return userService.deleteFromFriends(userId, friendId);
    }

    /**
     * Метод для получения списка друзей пользователя
     */
    @GetMapping("/{userId}/friends")
    public List<User> getFriendsForUser(@PathVariable Long userId) {
        log.info("Входящий запрос на получения списка друзей для пользователя с id = " + userId);
        return userService.getFriendsForUser(userId);
    }

    /**
     * Метод для получения списка общих друзей для двух пользователей
     */
    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherUserId) {
        log.info("Входящий запрос на получения списка общих друзей для пользователей с id = "
                + userId + " и пользователя с id = " + otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }

    /**
     * Метод для получения списка всех пользователей
     */
    @GetMapping
    public List<User> getAll() {
        log.info("Входящий запрос на получение списка всех пользователей");
        return userService.getAll();
    }

    /**
     * Метод для создания нового пользователя
     */
    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Входящий запрос на создание пользователя");
        log.info(user.toString());
        return userService.create(user);
    }

    /**
     * Метод для редактирования пользователя. Чтобы отредактировать пользователя, в теле запроса надо передать
     * id пользователя, который нужно отредактировать
     */
    @PutMapping
    public User update(@RequestBody User user) {
        log.info("Входящий запрос на редактирование пользователя");
        log.info(user.toString());
        return userService.update(user);
    }

    /**
     * Обработка ошибки, если пользователь не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки сервера
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleServerError(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }
}
