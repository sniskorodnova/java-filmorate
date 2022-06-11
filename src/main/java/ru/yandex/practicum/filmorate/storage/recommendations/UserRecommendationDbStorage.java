package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.service.RecommendationService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserRecommendationDbStorage implements UserRecommendationStorage {

    JdbcTemplate jdbcTemplate;
    RecommendationService recommendationService;

    @Autowired
    public UserRecommendationDbStorage(JdbcTemplate jdbcTemplate,
                                       RecommendationService recommendationService) {
        this.jdbcTemplate = jdbcTemplate;
        this.recommendationService = recommendationService;
    }

    @Override
    public List<Long> getRecommendation(Long id) {
        Set<Long> recommendedFilms = new HashSet<>(); // список фильмов для рекомендации
        List<Long> userFilm = new ArrayList<>(); // список фильмов, которые лайкнул пользователь
        List<Long> listOfUsers = new ArrayList<>(); // список пользователей, которые лайкали что-либо

        String sqlQuery = "SELECT FILM_ID FROM user_film_likes where USER_ID = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);

        // тут выгружаю все лайки пользователя
        while (sqlRowSet.next()) {
            userFilm.add(sqlRowSet.getLong("film_id"));
        }

        String sqlQueryUsers = "SELECT DISTINCT USER_ID FROM user_film_likes WHERE USER_ID != ?";

        SqlRowSet sqlRowUsers = jdbcTemplate.queryForRowSet(sqlQueryUsers, id);

        // тут выгружаю всех пользователей, которые поставили лайки фильмам
        while (sqlRowUsers.next()) {
            listOfUsers.add(sqlRowUsers.getLong("USER_ID"));
        }
        return List.copyOf(recommendationService
                .getRecommendation(listOfUsers,
                userFilm,
                jdbcTemplate,
                recommendedFilms));
    }
}
