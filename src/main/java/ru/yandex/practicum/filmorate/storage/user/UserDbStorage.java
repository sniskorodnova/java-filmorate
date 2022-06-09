package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс, имплементирующий интерфейс для работы с таблицей user в БД
 */
@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("friendshipDbStorage") FriendshipStorage friendshipStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipStorage = friendshipStorage;
    }

    /**
     * Метод для создания нового пользователя
     */
    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        long key = keyHolder.getKey().longValue();
        return getById(key);
    }

    /**
     * Метод для редактирования пользователя
     */
    @Override
    public User update(User user) {
        String sqlQuery = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    /**
     * Метод для получения пользователя по его id
     */
    @Override
    public User getById(Long id) {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                "FROM users WHERE (NOT is_delete) AND (USER_ID = ?)";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (row.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        } else {
            return null;
        }
    }

    /**
     * Метод для получения всех пользователей
     */
    @Override
    public List<User> getAll() {
        String sqlQuery = "SELECT USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    /**
     * Метод для удаления всех пользователей
     */
    @Override
    public void deleteAll() {
        String sqlQuery = "DELETE FROM users";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public void delete(Long id) {
        String sqlQuery = "UPDATE users SET is_delete = TRUE WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    /**
     * Метод для маппинга полей пользователя из таблрицы в объект
     */
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        Set<Long> idList = new HashSet<>();
        for (User user : friendshipStorage.getFriendsForUser(resultSet.getLong("USER_ID"))) {
            idList.add(user.getId());
        }
        return User.builder()
                .id(resultSet.getLong("USER_ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .friends(idList)
                .build();
    }
}
