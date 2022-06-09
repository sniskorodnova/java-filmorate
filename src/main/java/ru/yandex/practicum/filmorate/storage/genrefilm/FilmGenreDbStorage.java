package ru.yandex.practicum.filmorate.storage.genrefilm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TreeSet;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей film_genre в БД
 */
@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод для создания связи фильма и жанра в таблице
     */
    @Override
    public void create(Long filmId, int genreId) {
        String sqlQuery = "INSERT INTO film_genre (FILM_ID, GENRE_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    /**
     * Метод для получения фильма по его id
     */
    @Override
    public TreeSet<Integer> getByFilmId(Long filmId) {
        String sqlQuery = "SELECT GENRE_ID FROM film_genre WHERE FILM_ID = ?";
        List<Integer> genreList = jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
        return new TreeSet<>(genreList);
    }

    /**
     * Метод для удаления всех строк в таблице с переданным id фильма
     */
    @Override
    public void deleteByFilmId(Long filmId) {
        String sqlQuery = "DELETE FROM film_genre WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
