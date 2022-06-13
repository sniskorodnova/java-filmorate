package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.recommendations.UserRecommendationStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Класс-сервис, отвечающий за логику работы с пользователями
 */
@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private final UserRecommendationStorage userRecommendationStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage,
                       UserRecommendationStorage userRecommendationStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
        this.userRecommendationStorage = userRecommendationStorage;
        this.filmStorage = filmStorage;
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
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        validate(user);
        return userStorage.create(user);
    }

    /**
     * Метод для редактирования существующего пользователя. Перед редактированием новый объект пользователя
     * валидируется
     */
    public User update(User user) throws ValidationException, UserNotFoundException {
        if (userStorage.getById(user.getId()) != null) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            validate(user);
            return userStorage.update(user);
        } else {
            throw new UserNotFoundException("User with id = " + user.getId() + " not found");
        }
    }

    /**
     * Метод удаления пользователя
     */
    public void delete(Long id) throws UserNotFoundException {
        if (userStorage.getById(id) != null) {
            userStorage.delete(id);
        } else {
            throw new UserNotFoundException("User with id = " + id + " not found");
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
     * Метод для добавления пользователя в друзья другому пользователю
     */
    public User addToFriends(Long userId, Long friendId) throws UserNotFoundException {
        final User user = userStorage.getById(userId);
        final User friend = userStorage.getById(friendId);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (friend == null) {
            throw new UserNotFoundException("User with id = " + friendId + " not found");
        } else {
            friendshipStorage.addToFriends(userId, friendId);
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
            friendshipStorage.deleteFromFriends(userId, friendId);
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
            List<User> friendsList = friendshipStorage.getFriendsForUser(userId);
            return friendsList;
        }
    }

    /**
     * Метод для получения списка общих друзей двух пользователей
     */
    public List<User> getCommonFriends(Long userId, Long otherUserId) throws UserNotFoundException {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherUserId);

        if (user == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (otherUser == null) {
            throw new UserNotFoundException("User with id = " + otherUserId + " not found");
        } else {
            return friendshipStorage.getCommonFriends(userId, otherUserId);
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

    public List<Film> getRecommendation(Long id) {
        List<Long> userFilm = userRecommendationStorage.getUserFilms(id); // список фильмов, которые лайкнул пользователь
        List<Long> listOfUsers = userRecommendationStorage.getListOfOtherUser(id); // список других пользователей
        Set<Long> recommendedFilms = new HashSet<>(); // список фильмов для рекомендации


        for (int i = 0; i < listOfUsers.size(); i++) {
            List<Long> userList = new ArrayList<>(List.copyOf(userFilm)); // копия листа с фильмами юзера

            List<Long> otherUserFilms = userRecommendationStorage.getFilmsOfOtherUser(listOfUsers, i);

            userList.retainAll(otherUserFilms); // проверяю пересечение по лайкам с другим юзером

            /**
             * Здесь я реализовал проверку на то, что количество совпадений по лайкам
             * с другим пользователем больше 50%, тогда рекомендация проходит
             */
            if (userList.size() > userFilm.size() / 2) {
                otherUserFilms.removeAll(userFilm);

                recommendedFilms.addAll(otherUserFilms);
            }

        }
        return List.copyOf(recommendedFilms).stream()
                .map(filmStorage::getById)
                .sorted((o1, o2) -> o2.getLikesFromUsers().size() - o1.getLikesFromUsers().size())
                .limit(10L)
                .collect(Collectors.toList());
    }
}
