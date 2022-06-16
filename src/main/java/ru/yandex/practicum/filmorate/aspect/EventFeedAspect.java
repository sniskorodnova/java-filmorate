package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.create(ru.yandex.practicum.filmorate.model.Review))")
    public void executeEventReviewAdd() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.update(ru.yandex.practicum.filmorate.model.Review)))")
    public void executeEventReviewUpdate() {
    }

    @Pointcut("execution(* ru.yandex.practicum.filmorate.service.ReviewService.deleteById(Long))")
    public void executeEventReviewDelete() {
    }

    /**
     * Метод для обработки события добавления в друзья
     */
    @AfterReturning("executeEventFriendshipAdd()")
    public void processingEventFriendshipAdd(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        Long userId = (Long) joinPoint.getArgs()[0];
        Long entityId = feedStorage.getFriendshipIdByUserId(userId, (Long) joinPoint.getArgs()[1]);
        if (entityId != null) {
            //запись в хранилище
            feedStorage.createEvent(
                    Feed.builder()
                            .timestamp(getTimeNow())
                            .userId(userId)
                            .eventType("FRIEND")
                            .operation("ADD")
                            .entityId(entityId)
                            .build()
            );
        }
    }

    /**
     * Метод для обработки события удаления из друзей
     */
    @Before("executeEventFriendshipDelete()")
    public void processingEventFriendshipDelete(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        Long userId = (Long) joinPoint.getArgs()[0];
        Long entityId = feedStorage.getFriendshipIdByUserId(userId, (Long) joinPoint.getArgs()[1]);
        if (entityId != null) {
            //запись в хранилище
            feedStorage.createEvent(
                    Feed.builder()
                            .timestamp(getTimeNow())
                            .userId(userId)
                            .eventType("FRIEND")
                            .operation("REMOVE")
                            .entityId(entityId)
                            .build()
            );
        }
    }

    /**
     * Метод для обработки события добавление лайка
     */
    @AfterReturning("executeEventLikeAdd()")
    public void processingEventLikeAdd(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        Long userId = (Long) joinPoint.getArgs()[1];
        //запись в хранилище
        feedStorage.createEvent(
                Feed.builder()
                        .timestamp(getTimeNow())
                        .userId(userId)
                        .eventType("LIKE")
                        .operation("ADD")
                        .entityId(userId)
                        .build()
        );
    }

    /**
     * Метод для обработки события удаление лайка
     */
    @AfterReturning("executeEventLikeDelete()")
    public void processingEventLikeDelete(JoinPoint joinPoint) {
        Long userId = (Long) joinPoint.getArgs()[1];
        //запись в хранилище
        feedStorage.createEvent(
                Feed.builder()
                        .timestamp(getTimeNow())
                        .userId(userId)
                        .eventType("LIKE")
                        .operation("REMOVE")
                        .entityId(userId)
                        .build()
        );
    }

    /**
     * Метод для обработки события добавления отзыва или обновление отзыва
     */
    @AfterReturning("executeEventReviewAdd() || executeEventReviewUpdate()")
    public void processingEventReviewAddOrUpdate(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        Long userId = ((Review) joinPoint.getArgs()[0]).getUserId();
        String operation = (joinPoint.getSignature().getName().equals("addToFriends")) ? "ADD" : "UPDATE";
        Long entityId = feedStorage.getReviewIdByUserId(userId, ((Review) joinPoint.getArgs()[0]).getFilmId());
        if (entityId != null) {
            //запись в хранилище
            feedStorage.createEvent(
                    Feed.builder()
                            .timestamp(getTimeNow())
                            .userId(userId)
                            .eventType("REVIEW")
                            .operation(operation)
                            .entityId(entityId)
                            .build()
            );
        }
    }

    /**
     * Метод для обработки события удаление отзыва
     */
    @Before("executeEventReviewDelete()")
    public void processingEventReviewDelete(JoinPoint joinPoint) {
        //инициализация полей для объекта класса Feed
        long timestamp = getTimeNow();
        Long userId = (Long) joinPoint.getArgs()[0];
        Long entityId = feedStorage.getReviewIdByUserId(userId, (Long) joinPoint.getArgs()[0]);
        if (entityId != null) {
            //запись в хранилище
            feedStorage.createEvent(
                    Feed.builder()
                            .timestamp(timestamp)
                            .userId(userId)
                            .eventType("REVIEW")
                            .operation("REMOVE")
                            .entityId(entityId)
                            .build()
            );
        }
    }

    private long getTimeNow() {
        return Instant.now().getEpochSecond();
    }
}