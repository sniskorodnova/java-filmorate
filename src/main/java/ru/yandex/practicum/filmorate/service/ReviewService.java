package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewlikes.ReviewLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         @Qualifier("reviewLikesDbStorage") ReviewLikesStorage reviewLikesStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.reviewLikesStorage = reviewLikesStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review create(Review review) throws UserNotFoundException, FilmNotFoundException {
        User user = userStorage.getById(review.getUserId());
        Film film = filmStorage.getById(review.getFilmId());
        if (film == null) {
            throw new FilmNotFoundException("Film with id = " + review.getUserId() + " not found");
        } else if (user == null) {
            throw new UserNotFoundException("User with id = " + review.getUserId() + " not found");
        } else {
            return reviewStorage.create(review);
        }
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public Review getById(Long id) throws ReviewNotFoundException {
        if (reviewStorage.getById(id) == null) {
            throw new ReviewNotFoundException("Review with id = " + id + " not found");
        } else {
            return reviewStorage.getById(id);
        }
    }

    public List<Review> getReviewsForFilm(Long id, int count) throws FilmNotFoundException {
        if (filmStorage.getById(id) == null) {
            throw new FilmNotFoundException("Film with id = " + id + " not found");
        } else {
            return reviewStorage.getReviewsForFilm(id, count);
        }
    }

    public void addLikeToReview(Long reviewId, Long userId) throws ReviewNotFoundException, UserNotFoundException,
            LikeRecordAlreadyExistsException {
        if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else if (reviewLikesStorage.checkIfRecordExists(reviewId, userId)) {
            throw new LikeRecordAlreadyExistsException("Review with id = " + reviewId + " has already been rated by "
                    + "user with id = " + userId);
        } else {
            reviewLikesStorage.insertLikeToReview(reviewId, userId);
        }
    }

    public void addDislikeToReview(Long reviewId, Long userId) throws UserNotFoundException, ReviewNotFoundException,
            LikeRecordAlreadyExistsException {
        if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else if (reviewLikesStorage.checkIfRecordExists(reviewId, userId)) {
            throw new LikeRecordAlreadyExistsException("Review with id = " + reviewId + " has already been rated by "
                    + "user with id = " + userId);
        } else {
            reviewLikesStorage.insertDislikeToReview(reviewId, userId);
        }
    }

    public void removeLikeFromReview(Long reviewId, Long userId) throws UserNotFoundException,
            ReviewNotFoundException {
        if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else {
            reviewLikesStorage.removeLikeFromReview(reviewId, userId);
        }
    }

    public void removeDislikeFromReview(Long reviewId, Long userId) throws UserNotFoundException,
            ReviewNotFoundException {
        if (userStorage.getById(userId) == null) {
            throw new UserNotFoundException("User with id = " + userId + " not found");
        } else if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else {
            reviewLikesStorage.removeDislikeFromReview(reviewId, userId);
        }
    }

    public void deleteDyId(Long reviewId) throws ReviewNotFoundException {
        if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else {
            reviewStorage.deleteById(reviewId);
        }
    }
}
