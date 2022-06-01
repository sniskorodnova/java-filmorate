package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс, описывающий логику работы с хранилищем для дружбы пользователей
 */
public interface FriendshipStorage {
    void addToFriends(Long userId, Long friendId);

    List<User> getFriendsForUser(Long userId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

    void deleteFromFriends(Long userId, Long friendId);
}
