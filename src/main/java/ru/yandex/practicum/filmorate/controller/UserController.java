package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-контроллер для работы с пользователями
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserService getUserService() {
        return userService;
    }

    /**
     * Метод для получения пользователя по его id
     */
    @GetMapping("/{userId}")
    public User getById(@PathVariable Long userId) throws UserNotFoundException {
        log.debug("Входящий запрос на получение информации по пользователю с id = {}", userId);
        return userService.getById(userId);
    }

    /**
     * Метод для добавления пользователя в друзья другому пользователю
     */
    @PutMapping("/{userId}/friends/{friendId}")
    public User addToFriends(@PathVariable Long userId, @PathVariable Long friendId) throws UserNotFoundException {
        log.debug("Входящий запрос на добавление в друзья пользователя с id = {} пользователю c id = {}",
                friendId, userId);
        return userService.addToFriends(userId, friendId);
    }

    /**
     * Метод для удаления пользователя из друзей другого пользователя
     */
    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable Long userId, @PathVariable Long friendId) throws UserNotFoundException {
        log.debug("Входящий запрос на удаление из друзей пользователя с id = {} у пользователя c id = {}",
                friendId, userId);
        return userService.deleteFromFriends(userId, friendId);
    }

    /**
     * Метод удаления пользователя
     */
    @DeleteMapping("/{userId}")
    public void delete (@PathVariable Long userId)
            throws UserNotFoundException {
        log.debug("Входящий запрос на удаление пользователя с id = {}", userId);
        userService.delete(userId);
    }

    /**
     * Метод для получения списка друзей пользователя
     */
    @GetMapping("/{userId}/friends")
    public List<User> getFriendsForUser(@PathVariable Long userId) throws UserNotFoundException {
        log.debug("Входящий запрос на получения списка друзей для пользователя с id = {}", userId);
        return userService.getFriendsForUser(userId);
    }

    /**
     * Метод для получения списка общих друзей для двух пользователей
     */
    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> getCommonFriends(@PathVariable Long userId, @PathVariable Long otherUserId)
            throws UserNotFoundException {
        log.debug("Входящий запрос на получения списка общих друзей для пользователей с id = {} и пользователя "
                + "с id = {}", userId, otherUserId);
        return userService.getCommonFriends(userId, otherUserId);
    }

    /**
     * Метод для получения списка всех пользователей
     */
    @GetMapping
    public List<User> getAll() {
        log.debug("Входящий запрос на получение списка всех пользователей");
        return userService.getAll();
    }

    /**
     * Метод для создания нового пользователя
     */
    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        log.debug("Входящий запрос на создание пользователя");
        log.debug(user.toString());
        return userService.create(user);
    }

    /**
     * Метод для редактирования пользователя. Чтобы отредактировать пользователя, в теле запроса надо передать
     * id пользователя, который нужно отредактировать
     */
    @PutMapping
    public User update(@RequestBody User user) throws ValidationException, UserNotFoundException {
        log.debug("Входящий запрос на редактирование пользователя");
        log.debug(user.toString());
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

    /**
     * Метод для получения рекомендаций для пользователя
     * Выводит топ 10 рекомендаций, отсортированные по убыванию количества лайков
     */
    @GetMapping("/{id}/recommendations")
    public List<Film> getRecomendation(@PathVariable Long id) {
        log.debug("Входящий запрос на получение рекомендаций");
        return userService.getRecommendation(id);
    }
}
