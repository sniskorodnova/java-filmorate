package ru.yandex.practicum.filmorate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
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

    @Pointcut("@annotation(EventFriendship)")
    public void executeEventFriendship() {
    }

    @Pointcut("@annotation(EventLike)")
    public void executeEventLike() {
    }

    @Pointcut("@annotation(EventReview)")
    public void executeEventReview() {
    }

    /**
     * Метод для обработки события дружба
     */
    @After("executeEventFriendship()")
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
    @After("executeEventLike()")
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
    @After("executeEventReview()")
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
