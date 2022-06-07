package ru.yandex.practicum.filmorate.storage.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchInDb implements Search {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<Film> searchFilmByParam(String query, KindOfSearchFilm by) {
        if (by != KindOfSearchFilm.TITLE) {
            return new ArrayList<>();
        }

        String sql = "SELECT f.FILM_ID,\n" +
                "       f.NAME,\n" +
                "       f.DESCRIPTION,\n" +
                "       f.RELEASE_DATE,\n" +
                "       f.DURATION,\n" +
                "       f.RATING_MPAA_ID,\n" +
                "       COUNT(u.USER_ID) AS likes_count\n" +
                "FROM \"film\" f\n" +
                "         LEFT OUTER JOIN \"user_film_likes\" u ON f.FILM_ID = u.FILM_ID\n" +
                "WHERE LOWER(f.NAME) LIKE '%'+?+'%'\n" +
                "GROUP BY f.FILM_ID\n" +
                "ORDER BY likes_count DESC";

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);

        return jdbcTemplate.query(sql,
                filmDbStorage::mapRowToFilm, query.toLowerCase());
    }
}
