package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class RecommendationService {
    public Set<Long> getRecommendation(List<Long> listOfUsers,
                                       List<Long> userFilm,
                                       JdbcTemplate jdbcTemplate,
                                       Set<Long> recommendedFilms) {

        for (int i = 0; i < listOfUsers.size(); i++) {
            List<Long> otherUserFilms = new ArrayList<>(); // лист с лайками других юзеров
            List<Long> userList = new ArrayList<>(List.copyOf(userFilm)); // копия листа с фильмами юзера

            String sqlQueryOtherUser = "SELECT FILM_ID FROM user_film_likes where USER_ID = ?";

            SqlRowSet sqlRowSetOtherUser = jdbcTemplate.queryForRowSet(sqlQueryOtherUser, listOfUsers.get(i));

            while (sqlRowSetOtherUser.next()) {
                otherUserFilms.add(sqlRowSetOtherUser.getLong("film_id"));
            }

            userList.retainAll(otherUserFilms); // проверяю пересечение по лайкам с другим юзером

            /**
             * Здесь я решил реализовать проверку на то, что количество совпадений по лайкам
             * с другим пользователем больше 50%, тогда рекомендация проходит
             */
            if (userList.size() > userFilm.size() / 2) {
                otherUserFilms.removeAll(userFilm);

                recommendedFilms.addAll(otherUserFilms);
            }
        }
        return recommendedFilms;
    }
}
