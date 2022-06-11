package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей rating_mpaa в БД
 */
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для получения объекта рейтинга по его id
     */
    @Override
    public Mpa getById (int id) {
        String sqlQuery = "SELECT * FROM RATING_MPAA WHERE RATING_MPAA_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToMpa, id);
        } else {
            return null;
        }
    }

    /**
     * Метод для получения списка всех объектов рейтинга
     */
    @Override
    public List<Mpa> getAll () {
        String sqlQuery = "SELECT * FROM RATING_MPAA";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpa);
    }

    /**
     * Метод для маппинга полей рейтинга mpa из таблицы в объект
     */
    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("RATING_MPAA_ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
