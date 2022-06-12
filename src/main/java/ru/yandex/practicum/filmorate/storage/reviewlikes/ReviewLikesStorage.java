package ru.yandex.practicum.filmorate.storage.reviewlikes;

/**
 * Интерфейс, описывающий логику работы с хранилищем для лайков к отзывам
 */
public interface ReviewLikesStorage {
    void insertLikeToReview(Long reviewId, Long userId);

    void insertDislikeToReview(Long reviewId, Long userId);

    void removeLikeFromReview(Long reviewId, Long userId);

    void removeDislikeFromReview(Long reviewId, Long userId);

    boolean checkIfRecordExists(Long reviewId, Long userId);
}
