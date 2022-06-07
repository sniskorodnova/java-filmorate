package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
    Review create(Review review);

    Review getById(Long id);
}
