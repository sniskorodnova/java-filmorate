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

import java.util.Comparator;
import java.util.List;

/**
 * Класс-сервис, отвечающий за логику работы с отзывами
 */
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

    /**
     * Метод для создания отзыва
     */
    public Review create(Review review) throws UserNotFoundException, FilmNotFoundException, ValidationException {
        if (review.getUserId() == null || review.getFilmId() == null || review.getIsPositive() == null) {
            throw new ValidationException("Incorrect request");
        } else {
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
    }

    /**
     * Метод для редактирования отзыва
     */
    public Review update(Review review) throws ReviewNotFoundException {
        if (reviewStorage.getById(review.getId()) == null) {
            throw new ReviewNotFoundException("Review with id = " + review.getId() + " not found");
        } else {
            return reviewStorage.update(review);
        }
    }

    /**
     * Метод для получения отзыва по его id
     */
    public Review getById(Long id) throws ReviewNotFoundException {
        if (reviewStorage.getById(id) == null) {
            throw new ReviewNotFoundException("Review with id = " + id + " not found");
        } else {
            return reviewStorage.getById(id);
        }
    }

    /**
     * Метод для получения отзывов для фильма по его id
     */
    public List<Review> getReviewsForFilm(Long id, int count) throws FilmNotFoundException {
        if (id == null) {
            List<Review> getReviews = reviewStorage.getAllReviews(count);
            getReviews.sort(Comparator.comparing(Review::getUseful).reversed()
                    .thenComparing(Review::getId));
            return getReviews;
        } else {
            if (filmStorage.getById(id) == null) {
                throw new FilmNotFoundException("Film with id = " + id + " not found");
            } else {
                List<Review> getReviews = reviewStorage.getReviewsForFilm(id, count);
                getReviews.sort(Comparator.comparing(Review::getUseful).reversed()
                                .thenComparing(Review::getId));
                return getReviews;
            }
        }
    }

    /**
     * Метод для добавления лайка отзыву
     */
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

    /**
     * Метод для добавления дизлайка отзыву
     */
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

    /**
     * Метод для удаления лайка у отзыва
     */
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

    /**
     * Метод для удаления дизлайка у отзыва
     */
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

    /**
     * Метод для удаления отзыва по его id
     */
    public void deleteById(Long reviewId) throws ReviewNotFoundException {
        if (reviewStorage.getById(reviewId) == null) {
            throw new ReviewNotFoundException("Review with id = " + reviewId + " not found");
        } else {
            reviewStorage.deleteById(reviewId);
        }
    }
}
