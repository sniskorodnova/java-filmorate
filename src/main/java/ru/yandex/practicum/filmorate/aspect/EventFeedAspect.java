package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;


import java.time.Instant;

@Component
@Aspect
public class EventFeedAspect {
    private final FeedStorage feedStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventFeedAspect(FeedStorage feedStorage, JdbcTemplate jdbcTemplate) {
        this.feedStorage = feedStorage;
        this.jdbcTemplate = jdbcTemplate;
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
        Long entityId = getFriendshipIdByUserId(userId);
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
        Long entityId = getReviewIdByUserId(userId);
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

    private Long getFriendshipIdByUserId(Long userId) {
        String sqlQuery = "SELECT FRIENDSHIP_ID FROM FRIENDSHIP WHERE USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (row.next()) {
            return row.getLong("FRIENDSHIP_ID");
        } else {
            return null;
        }
    }

    private Long getReviewIdByUserId(Long userId) {
        String sqlQuery = "SELECT REVIEW_ID FROM REVIEW WHERE USER_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if (row.next()) {
            return row.getLong("REVIEW_ID");
        } else {
            return null;
        }
    }
}