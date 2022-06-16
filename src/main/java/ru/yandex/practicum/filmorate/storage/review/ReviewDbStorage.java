package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей review в БД
 */
@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для создания нового отзыва в таблице
     */
    @Override
    public Review create(Review review) {
        String sqlQuery = "INSERT INTO review (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        long key = keyHolder.getKey().longValue();
        return getById(key);
    }

    /**
     * Метод для получения отзыва по его id из таблицы
     */
    @Override
    public Review getById(Long id) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, "
                + "(SELECT SUM(DECODE(IS_LIKE, true, 1, -1)) FROM review_likes rl "
                + "WHERE rl.REVIEW_ID = r.REVIEW_ID AND rl.IS_DELETE = false) USEFUL FROM review r "
                + "WHERE r.REVIEW_ID = ? AND (NOT r.IS_DELETE)";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id);
        } else {
            return null;
        }
    }

    /**
     * Метод для редактирования отзыва в таблице
     */
    @Override
    public Review update(Review review) {
        String sqlQuery = "UPDATE review SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getId());
        return getById(review.getId());
    }

    /**
     * Метод для получения списка всех отзывов для фильма по его id из таблицы
     */
    @Override
    public List<Review> getReviewsForFilm(Long id, int count) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, "
                + "(SELECT SUM(DECODE(IS_LIKE, true, 1, -1)) FROM review_likes rl "
                + "WHERE rl.REVIEW_ID = r.REVIEW_ID) useful FROM review r WHERE r.FILM_ID = ? "
                + "AND (NOT IS_DELETE) LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, id, count);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT REVIEW_ID, CONTENT, IS_POSITIVE, USER_ID, FILM_ID, "
                + "(SELECT SUM(DECODE(IS_LIKE, true, 1, -1)) FROM review_likes rl "
                + "WHERE rl.REVIEW_ID = r.REVIEW_ID) useful FROM review r WHERE "
                + "(NOT IS_DELETE) LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    /**
     * Метод для удаления отзыва по его id из таблицы. Удаление реализовано через проставление флага is_delete
     */
    @Override
    public void deleteById(Long reviewId) {
        String sqlQuery = "UPDATE review SET IS_DELETE = true WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, reviewId);
    }

    /**
     * Метод для маппинга полей отзыва из таблицы в объект
     */
    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .id(resultSet.getLong("REVIEW_ID"))
                .content(resultSet.getString("CONTENT"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(resultSet.getLong("USEFUL"))
                .build();
    }
}
