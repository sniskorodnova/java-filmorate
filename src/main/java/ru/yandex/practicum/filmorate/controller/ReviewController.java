package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

/**
 * Класс-контроллер для работы с отзывами
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * Метод для создания отзыва
     */
    @PostMapping
    public Review create(@RequestBody Review review) throws UserNotFoundException, FilmNotFoundException {
        log.debug("Входящий запрос на создание отзыва");
        log.debug(review.toString());
        return reviewService.create(review);
    }

    /**
     * Метод для редактирования отзыва. Новый объект отзыва передается в теле запроса
     */
    @PutMapping
    public Review update(@RequestBody Review review) throws ValidationException, FilmNotFoundException,
            ReviewNotFoundException {
        log.debug("Входящий запрос на редактирование отзыва");
        log.debug(review.toString());
        return reviewService.update(review);
    }

    /**
     * Метод для получения отзыва по его id. Если отзыв не найден, то возвращается 400 ошибка
     */
    @GetMapping("/{reviewId}")
    public Review getById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        log.debug("Входящий запрос на получение отзыва по id = {}", reviewId);
        return reviewService.getById(reviewId);
    }

    /**
     * Метод для удаления отзыва по его id. Если отзыв не найден, то возвращается 400 ошибка
     */
    @DeleteMapping("/{reviewId}")
    public void deleteById(@PathVariable Long reviewId) throws ReviewNotFoundException {
        log.debug("Входящий запрос на удаление отзыва с id = {}", reviewId);
        reviewService.deleteById(reviewId);
    }

    /**
     * Метод для получения всех отзывов для фильма по его id. Если фильм не найден, то возвращается 400 ошибка
     */
    @GetMapping()
    public List<Review> getReviewsForFilm(@RequestParam Long film, @RequestParam(defaultValue = "10") int count)
            throws FilmNotFoundException {
        log.debug("Входящий запрос на получение первых {} отзывов на фильм с id = {}", count, film);
        return reviewService.getReviewsForFilm(film, count);
    }

    /**
     * Метод для проставления лайка отзыву. В урле передается id отзыва и id пользователя, который поставил лайк
     */
    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException, LikeRecordAlreadyExistsException {
        log.debug("Входящий запрос на добавления лайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.addLikeToReview(reviewId, userId);
    }

    /**
     * Метод для проставления дизлайка отзыву. В урле передается id отзыва и id пользователя, который поставил дизлайк
     */
    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislikeToReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException, LikeRecordAlreadyExistsException {
        log.debug("Входящий запрос на добавления дизлайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.addDislikeToReview(reviewId, userId);
    }

    /**
     * Метод для удаления лайка у отзыва. В урле передается id отзыва и id пользователя, который удаляет лайк
     */
    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException {
        log.debug("Входящий запрос на удаление лайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.removeLikeFromReview(reviewId, userId);
    }

    /**
     * Метод для удаления дизлайка у отзыва. В урле передается id отзыва и id пользователя, который удаляет дизлайк
     */
    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable Long reviewId, @PathVariable Long userId)
            throws UserNotFoundException, ReviewNotFoundException {
        log.debug("Входящий запрос на удаление дизлайка для отзыва с id = {} пользователем с id = {}",
                reviewId, userId);
        reviewService.removeDislikeFromReview(reviewId, userId);
    }

    /**
     * Обработка ошибки, если фильм не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFound(final FilmNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки, если запись в таблице с лайками уже существует
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLikeExists(final LikeRecordAlreadyExistsException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки, если отзыв не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReviewNotFound(final ReviewNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * Обработка ошибки, если пользователь не найден
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
