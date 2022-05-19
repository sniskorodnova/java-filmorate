package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс-сервис, отвечающий за логику работы с пользователями. Для реализации логики используются методы хранилища
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    /**
     * Метод для получения списка всех пользователей
     */
    public List<User> getAll() {
        return userStorage.getAll();
    }

    /**
     * Метод для создания нового пользователя. Перед добавлением пользователь валидируется
     */
    public User create(User user) throws ValidationException {
        validate(user);
        return userStorage.create(user);
    }

    /**
     * Метод для редактирования существующего пользователя. Перед редактированием новый объект пользователя
     * валидируется
     */
    public User update(User user) throws ValidationException, UserNotFoundException {
        if (userStorage.getById(user.getId()) != null) {
            validate(user);
            return userStorage.update(user);
        } else {
            throw new UserNotFoundException("User with id = " + user.getId() + " not found");
        }
    }

    /**
     * Метод для получения информации о пользователе по его id
     */
    public User getById(Long userId) throws UserNotFoundException {
        final User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }
        return user;
    }

    /**
     * Метод для добавления пользователя в друзья другому пользователю. Данная операция взаимна, то есть
     * второму пользователю первый также добавляется в друзья
     */
    public User addToFriends(Long userId, Long friendId) throws UserNotFoundException {
        final User user = userStorage.getById(userId);
        final User friend = userStorage.getById(friendId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new UserNotFoundException("User with id = " + friendId + " not found");
        } else {
            Set<Long> idFriendsForUser = new HashSet<>();
            if (user.getFriends() != null) {
                idFriendsForUser = user.getFriends();
            }
            idFriendsForUser.add(friendId);
            user.setFriends(idFriendsForUser);

            Set<Long> idFriendsForFriend = new HashSet<>();
            if (friend.getFriends() != null) {
                idFriendsForFriend = friend.getFriends();
            }
            idFriendsForFriend.add(userId);
            friend.setFriends(idFriendsForFriend);
            userStorage.update(friend);
            return userStorage.update(user);
        }
    }

    /**
     * Метод для удаления пользователя из друзей у другого пользователя. Данная операция взаимна, то есть
     * у второго пользователя первый пользователь также удаляется из друзей
     */
    public User deleteFromFriends(Long userId, Long friendId) throws UserNotFoundException {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new UserNotFoundException("User with id = " + friendId + " not found");
        } else {
            Set<Long> idFriendsForUser = new HashSet<>();
            if (user.getFriends() != null) {
                idFriendsForUser = user.getFriends();
            }
            idFriendsForUser.remove(friendId);
            user.setFriends(idFriendsForUser);

            Set<Long> idFriendsForFriend = new HashSet<>();
            if (friend.getFriends() != null) {
                idFriendsForFriend = friend.getFriends();
            }
            idFriendsForFriend.remove(userId);
            friend.setFriends(idFriendsForFriend);
            userStorage.update(friend);
            return userStorage.update(user);
        }
    }

    /**
     * Метод для получения списка друзей пользователя
     */
    public List<User> getFriendsForUser(Long userId) throws UserNotFoundException {
        User user = userStorage.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else {
            List<User> friendsList = new ArrayList<>();
            if (user.getFriends() != null) {
                for (Long id : user.getFriends()) {
                    friendsList.add(userStorage.getById(id));
                }
            }
            return friendsList;
        }
    }

    /**
     * Метод для получения списка общих друзей двух пользователей
     */
    public List<User> getCommonFriends(Long userId, Long otherUserId) throws UserNotFoundException {
        List<User> commonFriends = new ArrayList<>();
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherUserId);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (otherUser == null) {
            throw new UserNotFoundException("User with id = " + otherUserId + " not found");
        } else {
            if (user.getFriends() == null) {
                if (otherUser.getFriends() == null) {
                    return List.of();
                } else {
                    for (Long id : otherUser.getFriends()) {
                        commonFriends.add(userStorage.getById(id));
                    }
                    return commonFriends;
                }
            } else {
                Set<Long> duplicateFriendsUser = new HashSet<>(user.getFriends());
                duplicateFriendsUser.retainAll(otherUser.getFriends());

                for (Long id : duplicateFriendsUser) {
                    commonFriends.add(userStorage.getById(id));
                }
                return commonFriends;
            }
        }
    }

    /**
     * Метод для валидации данных при создании и редактрования пользователя. Если какая-либо валидация не пройдена,
     * то выбрасывается исключение ValidationException
     */
    private void validate(User user) throws ValidationException {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.debug("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("Почта пользователя не может быть пустой и должна содержать символ @");
        } else if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("Логин пользователя не может быть пустым и не должен содержать пробелов");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Произошла ошибка валидации для пользователя:");
            throw new ValidationException("День рождения пользователя не может быть в будущем");
        }
    }
}
