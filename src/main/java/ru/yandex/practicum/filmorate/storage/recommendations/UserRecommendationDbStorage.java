package ru.yandex.practicum.filmorate.storage.recommendations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserRecommendationDbStorage implements UserRecommendationStorage {

    JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRecommendationDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Long> getUserFilms(Long id) {
        List<Long> userFilm = new ArrayList<>(); // список фильмов, которые лайкнул пользователь

        String sqlQuery = "SELECT FILM_ID FROM user_film_likes where USER_ID = ?";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);

        // тут выгружаю все лайки пользователя
        while (sqlRowSet.next()) {
            userFilm.add(sqlRowSet.getLong("film_id"));
        }
        return userFilm;
    }

    @Override
    public List<Long> getListOfOtherUser(Long id) {
        List<Long> listOfUsers = new ArrayList<>(); // список пользователей, которые лайкали что-либо

        String sqlQueryUsers = "SELECT DISTINCT USER_ID FROM user_film_likes WHERE USER_ID != ?";

        SqlRowSet sqlRowUsers = jdbcTemplate.queryForRowSet(sqlQueryUsers, id);

        // тут выгружаю всех пользователей, которые поставили лайки фильмам
        while (sqlRowUsers.next()) {
            listOfUsers.add(sqlRowUsers.getLong("USER_ID"));
        }
        return listOfUsers;
    }

    @Override
    public List<Long> getFilmsOfOtherUser(List<Long> listOfUsers, int i) {
        List<Long> otherUserFilms = new ArrayList<>(); // лист с лайками других юзеров

        String sqlQueryOtherUser = "SELECT FILM_ID FROM user_film_likes where USER_ID = ?";

        SqlRowSet sqlRowSetOtherUser = jdbcTemplate.queryForRowSet(sqlQueryOtherUser, listOfUsers.get(i));

        while (sqlRowSetOtherUser.next()) {
            otherUserFilms.add(sqlRowSetOtherUser.getLong("film_id"));
        }

        return otherUserFilms;
    }
}
