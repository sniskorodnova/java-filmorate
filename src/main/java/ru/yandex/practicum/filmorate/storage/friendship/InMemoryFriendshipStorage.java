package ru.yandex.practicum.filmorate.storage.friendship;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс, имплементирующий интерфейс для работы с хранилищем друзей пользователей
 */
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final UserStorage userStorage;

    public InMemoryFriendshipStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Метод для добавления пользователя в друзья
     */
    @Override
    public void addToFriends(Long userId, Long friendId) {
        Set<Long> idFriendsForUser = new HashSet<>();
        if (userStorage.getById(userId).getFriends() != null) {
            idFriendsForUser = userStorage.getById(userId).getFriends();
        }
        idFriendsForUser.add(friendId);
        userStorage.getById(userId).setFriends(idFriendsForUser);
    }

    /**
     * Метод получения списка друзей пользователя
     */
    @Override
    public List<User> getFriendsForUser(Long userId) {
        List<User> friendsList = new ArrayList<>();
        if (userStorage.getById(userId).getFriends() != null) {
            for (Long id : userStorage.getById(userId).getFriends()) {
                friendsList.add(userStorage.getById(id));
            }
        }
        return friendsList;
    }

    /**
     * Метод для получения списка общий друзей двух пользователей
     */
    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        List<User> commonFriends = new ArrayList<>();

        if (userStorage.getById(userId).getFriends() == null || userStorage.getById(otherUserId).getFriends() == null) {
                return List.of();
        } else {
            Set<Long> duplicateFriendsUser = new HashSet<>(userStorage.getById(userId).getFriends());
            duplicateFriendsUser.retainAll(userStorage.getById(otherUserId).getFriends());
            for (Long id : duplicateFriendsUser) {
                commonFriends.add(userStorage.getById(id));
            }
            return commonFriends;
        }
    }

    /**
     * Метод для удаления пользователя из друзей
     */
    @Override
    public void deleteFromFriends(Long userId, Long friendId) {
        Set<Long> idFriendsForUser = new HashSet<>();
        if (userStorage.getById(userId).getFriends() != null) {
            idFriendsForUser = userStorage.getById(userId).getFriends();
        }
        idFriendsForUser.remove(friendId);
        userStorage.getById(userId).setFriends(idFriendsForUser);
    }
}
