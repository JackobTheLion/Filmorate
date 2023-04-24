package ru.yandex.practicum.filmorate.storage.friends;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("dbStorage")
public class DbFriendsStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFriendsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "SELECT COUNT(1) AS count FROM friends WHERE user1_id = ? AND user2_id = ?";
        SqlRowSet rsDirect = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        SqlRowSet rsReverse = jdbcTemplate.queryForRowSet(sql, friendId, userId);
        if (rsDirect.next() && rsDirect.getInt("count") > 0) {
            log.info("Friendship request already exist.");
        } else if (rsReverse.next() && rsReverse.getInt("count") > 0) {
            String sqlUpdate = "UPDATE friends SET confirmed = true WHERE user1_id = ? AND user2_id = ?";
            jdbcTemplate.update(sqlUpdate, friendId, userId);
            log.info("Reverse friendship request already exist. Friendship confirmed.");
        } else {
            String sqlInsert = "INSERT INTO friends (user1_id, user2_id) VALUES (?,?)";
            jdbcTemplate.update(sqlInsert, userId, friendId);
            log.info("Friendship request created.");
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "SELECT * FROM friends WHERE user1_id = ? AND user2_id = ?";
        SqlRowSet rsDirect = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        SqlRowSet rsReverse = jdbcTemplate.queryForRowSet(sql, friendId, userId);

        if (rsDirect.next() && rsDirect.getBoolean("confirmed")) {
            String sqlUpdate = "UPDATE friends SET confirmed = false WHERE user1_id = ? AND user2_id = ?";
            jdbcTemplate.update(sqlUpdate, userId, friendId);
        } else if (rsReverse.next() && rsReverse.getBoolean("confirmed")) {
            String sqlUpdate = "UPDATE friends SET confirmed = false WHERE user1_id = ? AND user2_id = ?";
            jdbcTemplate.update(sqlUpdate, friendId, userId);
        } else {
            String sqlInsert = "DELETE from friends WHERE (user1_id = ? AND user2_id = ?) " +
                    "OR (user1_id = ? AND user2_id = ?)";
            jdbcTemplate.update(sqlInsert, userId, friendId, friendId, userId);
        }
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT * FROM filmorate_users WHERE " +
                "user_id IN (SELECT user2_id FROM friends WHERE user1_id = ? AND confirmed = true) " +
                "OR (user_id IN (SELECT user1_id FROM friends WHERE user2_id = ? AND confirmed = true))";
        List<User> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), userId, userId);
        log.info("Number of top films: {}", films.size());
        return films;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long friendId) {
        String sql = "SELECT * FROM filmorate_users AS u WHERE ((u.user_id IN " +
                "(SELECT user2_id FROM friends WHERE user1_id = ? AND confirmed = true)) " +
                "OR (u.user_id IN (SELECT user1_id FROM friends WHERE user2_id = ? AND confirmed = true))) " +
                "AND ((u.user_id IN (SELECT user2_id FROM friends WHERE user1_id = ? AND confirmed = true)) " +
                "OR (u.user_id IN (SELECT user1_id FROM friends WHERE user2_id = ? AND confirmed = true)))";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs), userId, userId, friendId, friendId);
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
