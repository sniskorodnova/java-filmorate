package ru.yandex.practicum.filmorate.storage.reviewlikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class ReviewLikesDbStorage implements ReviewLikesStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ReviewLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertLikeToReview(Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO review_likes (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, true)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void removeLikeFromReview (Long reviewId, Long userId) {
        String sqlQuery = "UPDATE review_likes SET IS_DELETE = true WHERE REVIEW_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void insertDislikeToReview (Long reviewId, Long userId) {
        String sqlQuery = "INSERT INTO review_likes (REVIEW_ID, USER_ID, IS_LIKE) VALUES (?, ?, false)";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void removeDislikeFromReview (Long reviewId, Long userId) {
        String sqlQuery = "UPDATE review_likes SET IS_DELETE = true WHERE REVIEW_ID = ? AND USER_ID = ? ";
        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public boolean checkIfRecordExists(Long reviewId, Long userId) {
        String sqlQuery = "SELECT * FROM review_likes WHERE REVIEW_ID = ? AND USER_ID = ? AND IS_DELETE = false";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, reviewId, userId);
        int rowCount = 0;
        while (row.next()) {
            rowCount++;
        }
        System.out.println("rowCount = " + rowCount);
        return rowCount != 0;
    }
}
