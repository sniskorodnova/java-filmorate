package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.time.Instant;

@Component
@Aspect
public class EventFeedAspect {
    private final FeedStorage feedStorage;

    @Autowired
    public EventFeedAspect(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.UserService.addToFriends(Long, Long))")
    public void executeEventFriendshipAdd() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.UserService.deleteFromFriends(Long, Long))")
    public void executeEventFriendshipDelete() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.FilmService.likeFilm(Long, Long))")
    public void executeEventLikeAdd() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.FilmService.deleteLike(Long, Long))")
    public void executeEventLikeDelete() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.create(Object))")
    public void executeEventReviewAdd() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.update(Object))")
    public void executeEventReviewUpdate() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.deleteById(Long))")
    public void executeEventReviewDelete() {
    }

    /**
     * Метод для обработки события дружба
     */
    @After("executeEventFriendshipAdd() || executeEventFriendshipDelete()")
    public void processingEventFriendship(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        long timestamp = getTimeNow();
        Long userId = (Long) joinPoint.getArgs()[0];
        String eventType = "FRIEND";
        String operation = (joinPoint.getSignature().getName().equals("addToFriends")) ? "ADD" : "DELETE";
        Long entityId = feedStorage.getFriendshipIdByUserId(userId, (Long) joinPoint.getArgs()[1]);
        //запись в хранилище
        feedStorage.createEvent(
                Feed.builder()
                        .timestamp(timestamp)
                        .userId(userId)
                        .eventType(eventType)
                        .operation(operation)
                        .entityId(entityId)
                        .build()
        );
    }

    /**
     * Метод для обработки события Лайка
     */
    @After("executeEventLikeAdd() || executeEventLikeDelete()")
    public void processingEventLike(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        long timestamp = getTimeNow();
        Long userId = (Long) joinPoint.getArgs()[1];
        String eventType = "LIKE";
        String operation = (joinPoint.getSignature().getName().equals("likeFilm")) ? "ADD" : "DELETE";
        //запись в хранилище
        feedStorage.createEvent(
                Feed.builder()
                        .timestamp(timestamp)
                        .userId(userId)
                        .eventType(eventType)
                        .operation(operation)
                        .entityId(userId)
                        .build()
        );
    }

    /**
     * Метод для обработки события Отзыва
     */
    @After("executeEventReviewAdd() || executeEventReviewUpdate() || executeEventReviewDelete()")
    public void processingEventReview(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        long timestamp = getTimeNow();
        String eventType = "REVIEW";
        Long userId;
        String operation;
        //определения метода
        if (joinPoint.getSignature().getName().equals("deleteById")) {
            userId = (Long) joinPoint.getArgs()[0];
            operation = "REMOVE";
        } else {
            userId = ((Review) joinPoint.getArgs()[0]).getUserId();
            operation = (joinPoint.getSignature().getName().equals("addToFriends")) ? "ADD" : "UPDATE";
        }
        Long entityId = feedStorage.getReviewIdByUserId(userId, ((Review) joinPoint.getArgs()[0]).getFilmId());
        //запись в хранилище
        feedStorage.createEvent(
                Feed.builder()
                        .timestamp(timestamp)
                        .userId(userId)
                        .eventType(eventType)
                        .operation(operation)
                        .entityId(entityId)
                        .build()
        );
    }

    private long getTimeNow() {
        return Instant.now().getEpochSecond();
    }
}