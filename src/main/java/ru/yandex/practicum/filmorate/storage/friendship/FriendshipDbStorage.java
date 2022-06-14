package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.aspect.EventFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей friendship в БД
 */
@Slf4j
@Component
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Метод добавления в друзья
     */
    @Override
    public void addToFriends(Long userId, Long friendId) {
        if (!checkIfFriendsAtAll(userId, friendId)) {
            if (checkIfFriendsAtAll(friendId, userId)) {
                if (!checkIfConfirmedFriends(friendId, userId)) {
                    String sqlQuery = "UPDATE friendship SET CONFIRMED = true WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQuery, friendId, userId);
                }
            } else {
                String sqlQuery = "INSERT INTO friendship (USER_ID, FRIEND_ID, CONFIRMED)  VALUES (?, ?, false)";
                jdbcTemplate.update(sqlQuery, userId, friendId);
            }
        }
    }

    /**
     * Метод для проверки являются ли пользователи обоюдными друзьями
     */
    private boolean checkIfConfirmedFriends(Long userId, Long friendId) {
        String sqlQuery = "SELECT * FROM friendship WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        row.next();
        return row.getBoolean("CONFIRMED");
    }

    /**
     * Метод для проверки являются ли пользователи неподтвержденными друзьями
     */
    private boolean checkIfFriendsAtAll(Long userId, Long friendId) {
        String sqlQuery = "SELECT * FROM friendship WHERE USER_ID = ? AND FRIEND_ID = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        int rowCount = 0;
        while (row.next()) {
            rowCount++;
        }
        return rowCount != 0;
    }

    /**
     * Метод для получения друзей пользователя
     */
    @Override
    public List<User> getFriendsForUser(Long userId) {
        String sqlQuery = "SELECT * FROM (SELECT USER_ID AS user_id FROM friendship "
                + "WHERE FRIEND_ID = ? AND CONFIRMED = true "
                + "UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM friendship "
                + "WHERE USER_ID = ?) AS users_id INNER JOIN users u "
                + "ON u.USER_ID = users_id.user_id AND (NOT u.is_delete)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId);
    }

    /**
     * Метод для маппинга полей пользователя из таблрицы в объект
     */
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    /**
     * Метод для получения общих друзей двух пользователей
     */
    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {

        String sqlQuery = "SELECT * FROM ((SELECT USER_ID AS user_id FROM friendship "
                + "WHERE (FRIEND_ID = ?) "
                + "UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM friendship "
                + "WHERE USER_ID = ?) INTERSECT (SELECT USER_ID AS user_id "
                + "FROM friendship WHERE FRIEND_ID = ? UNION ALL "
                + "SELECT FRIEND_ID AS user_id FROM friendship "
                + "WHERE USER_ID = ?)) AS common_friends "
                + "INNER JOIN users u ON u.user_id = common_friends.user_id AND (NOT u.is_delete)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId, otherUserId, otherUserId);
    }

    /**
     * Метод для удаления пользователя из друзей
     */
    @Override
    public void deleteFromFriends(Long userId, Long friendId) {
        if (checkIfFriendsAtAll(friendId, userId)) {
            if (checkIfConfirmedFriends(friendId, userId)) {
                String sqlQuery = "UPDATE friendship SET CONFIRMED = false WHERE USER_ID = ? AND FRIEND_ID = ?";
                jdbcTemplate.update(sqlQuery, friendId, userId);
            }
        } else {
            if (checkIfFriendsAtAll(userId, friendId)) {
                if (checkIfConfirmedFriends(userId, friendId)) {
                    String sqlQueryDelete = "DELETE FROM friendship WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQueryDelete, userId, friendId);
                    String sqlQueryInsert = "INSERT INTO friendship (USER_ID, FRIEND_ID, CONFIRMED)  "
                            + "VALUES (?, ?, false)";
                    jdbcTemplate.update(sqlQueryInsert, friendId, userId);
                } else {
                    String sqlQuery = "DELETE FROM friendship WHERE USER_ID = ? AND FRIEND_ID = ?";
                    jdbcTemplate.update(sqlQuery, userId, friendId);
                }
            }
        }
    }
}
