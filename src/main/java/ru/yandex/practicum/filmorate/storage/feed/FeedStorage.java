package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

/**
 * Интерфейс, описывающий логику работы с лентой событий
 */
public interface FeedStorage {
    void createEvent(Feed feed);

    List<Feed> findEventByUserId(Long userId);
}
