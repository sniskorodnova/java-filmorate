package ru.yandex.practicum.filmorate.storage.userfilmlikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей friendship в БД
 */
@Component
public class UserFilmLikesDbStorage implements UserFilmLikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFilmLikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для сохранения лайка для фильма от пользователя
     */
    @Override
    public void saveLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO user_film_likes (USER_ID, FILM_ID) values (?, ?)";

        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    /**
     * Метод для удаления лайка для фильма от пользователя
     */
    @Override
    public void removeLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM user_film_likes WHERE USER_ID = ? AND FILM_ID = ?";

        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    /**
     * Метод для получения count первых фильмов по количеству лайков
     */
    @Override
    public List<Film> getCount(int count) {
        String sqlQuery = "SELECT f.FILM_ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, "
                + "f.RATING_MPAA_ID, COUNT(u.USER_ID) AS likes_count FROM film f "
                + "LEFT OUTER JOIN user_film_likes u ON f.FILM_ID = u.FILM_ID "
                + "WHERE NOT f.is_delete "
                + "GROUP BY f.FILM_ID ORDER BY likes_count DESC LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    /**
     * Метод для маппинга полей фильма из таблицы в объект
     */
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int mpaId = resultSet.getInt("RATING_MPAA_ID");
        String sqlFindName = "SELECT RATING_MPAA_ID, NAME FROM rating_mpaa WHERE RATING_MPAA_ID = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlFindName, this::mapRowToMpa, mpaId);

        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(mpa)
                .build();
    }

    /**
     * Метод для маппинга полей рейтинга фильма из таблицы в объект
     */
    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("RATING_MPAA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
