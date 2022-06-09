package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей films в БД
 */
@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод, возвращающий список всех фильмов в таблице
     */
    @Override
    public List<Film> getAll() {
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPAA_ID FROM film";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    /**
     * Метод для создания нового фильма в таблице
     */
    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO film (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPAA_ID) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        long key = keyHolder.getKey().longValue();
        return getById(key);
    }

    /**
     * Метод для редактирования фильма в таблице
     */
    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE film SET "
                + "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_MPAA_ID = ? "
                + "WHERE FILM_ID = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return getById(film.getId());
    }

    /**
     * Метод для получения фильма по его id из таблицы
     */
    @Override
    public Film getById (Long id) {
        String sqlQuery = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPAA_ID " +
                "FROM film WHERE (NOT is_delete) AND (FILM_ID = ?)";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        } else {
            return null;
        }
    }

    /**
     * Метод для удаления всех фильмов из таблицы
     */
    @Override
    public void deleteAll() {
        String sqlQuery = "DELETE FROM film";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "UPDATE film SET is_delete = TRUE WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    /**
     * Метод для маппинга полей фильма из таблицы в объект
     */
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int mpaId = resultSet.getInt("RATING_MPAA_ID");
        String sqlFindName = "SELECT RATING_MPAA_ID, NAME FROM rating_mpaa WHERE RATING_MPAA_ID = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sqlFindName, this::mapRowToMpa, mpaId);

        Set<Long> idLikes = new HashSet<>();
        String sqlFindLikes = "SELECT USER_ID FROM user_film_likes WHERE FILM_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlFindLikes, resultSet.getLong("FILM_ID"));
        while(row.next()) {
            idLikes.add(row.getLong("USER_ID"));
        }

        return Film.builder()
                .id(resultSet.getLong("FILM_ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .mpa(mpa)
                .likesFromUsers(idLikes)
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
