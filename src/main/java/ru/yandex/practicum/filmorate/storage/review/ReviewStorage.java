package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review getById(Long id);

    Review update(Review review);

    List<Review> getReviewsForFilm(Long id, int count);

    void deleteById(Long reviewId);
}