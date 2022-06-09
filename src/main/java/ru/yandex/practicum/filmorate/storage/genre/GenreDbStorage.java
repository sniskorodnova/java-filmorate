package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей genre в БД
 */
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для получения объекта жанра по его id
     */
    @Override
    public Genre getById (int id) {
        String sqlQuery = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } else {
            return null;
        }
    }

    /**
     * Метод получения списка всех объектов жанров
     */
    @Override
    public List<Genre> getAll () {
        String sqlQuery = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    /**
     * Метод для маппинга полей жанра из таблицы в объект
     */
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
