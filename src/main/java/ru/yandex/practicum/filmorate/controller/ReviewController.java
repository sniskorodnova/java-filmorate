package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) throws UserNotFoundException, FilmNotFoundException {
        log.debug("Входящий запрос на создание отзыва");
        log.debug(review.toString());
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) throws ValidationException, FilmNotFoundException {
        log.debug("Входящий запрос на редактирование отзыва");
        log.debug(review.toString());
        return reviewService.update(review);
    }

    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        log.debug("Входящий запрос на получение отзыва по id = {}", reviewId);
        return reviewService.getById(reviewId);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        log.debug("Входящий запрос на удаление отзыва с id = {}", reviewId);
        reviewService.deleteDyId(reviewId);
    }

    @GetMapping()
    public List<Review> getReviewsForFilm(@RequestParam Long film, @RequestParam(defaultValue = "10") int count)
            throws FilmNotFoundException {
        log.debug("Входящий запрос на получение первых {} отзывов на фильм с id = {}", count, film);
        return reviewService.getReviewsForFilm(film, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException, LikeRecordAlreadyExistsException {
        log.debug("Входящий запрос на добавления лайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException, LikeRecordAlreadyExistsException {
        log.debug("Входящий запрос на добавления дизлайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.addDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException {
        log.debug("Входящий запрос на удаление лайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.removeLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException {
        log.debug("Входящий запрос на удаление дизлайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.removeDislikeFromReview(reviewId, userId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLikeExists(final LikeRecordAlreadyExistsException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReviewNotFound(final ReviewNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
