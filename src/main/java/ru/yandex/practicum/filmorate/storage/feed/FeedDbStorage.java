package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для создания нового события
     */
    @Override
    public void createEvent(Feed feed) {
        String sqlQuery = "INSERT INTO FEED (USER_ID, EVENT_TYPE, OPERATION, TIMESTAMP, ENTITY_ID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"EVENT_ID"});
            stmt.setLong(1, feed.getUserId());
            stmt.setString(2, feed.getEventType());
            stmt.setString(3, feed.getOperation());
            stmt.setLong(4, feed.getTimestamp());
            stmt.setLong(5, feed.getEntityId());
            return stmt;
        }, keyHolder);
    }

    /**
     * Метод для получения список объектов класса Event по id пользователя
     */
    @Override
    public List<Feed> findEventByUserId(Long userId) {
        String sqlQuery = "SELECT * FROM FEED WHERE USER_ID= ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, userId);
    }

    @Override
    public Long getFriendshipIdByUserId(Long userId, Long friendId) {
        String sqlQuery = "SELECT FRIENDSHIP_ID FROM FRIENDSHIP WHERE USER_ID = ? and FRIEND_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        if (row.next()) {
            return row.getLong("FRIENDSHIP_ID");
        } else {
            return null;
        }
    }

    @Override
    public Long getReviewIdByUserId(Long userId, Long filmId) {
        String sqlQuery = "SELECT REVIEW_ID FROM REVIEW WHERE USER_ID = ? and FILM_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, filmId);
        if (row.next()) {
            return row.getLong("REVIEW_ID");
        } else {
            return null;
        }
    }

    /**
     * Метод для маппинга полей жанра из таблицы в объект
     */
    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(resultSet.getLong("TIMESTAMP"))
                .userId(resultSet.getLong("USER_ID"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("OPERATION"))
                .eventId(resultSet.getLong("EVENT_ID"))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .build();
    }
}