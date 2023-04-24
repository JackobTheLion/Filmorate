package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("dbStorage")
@Slf4j
public class DbUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO filmorate_users (email, login, name, birthday) VALUES (?,?,?,?)";
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("filmorate_users")
                    .usingGeneratedKeyColumns("user_id");
            user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
            return user;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User putUser(User user) {
        String sql = "UPDATE filmorate_users SET " +
                "email = ?, " +
                "login = ?, " +
                "name = ?, " +
                "birthday = ?" +
                "WHERE user_id = ?";
        try {
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Date.valueOf(user.getBirthday()),
                    user.getId());
            return user;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM filmorate_users";
        try {
            List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs));
            log.info("Number of users registered: {}", users.size());
            return users;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public User findUser(Long id) {
        log.info("Looking for user: {}", id);
        String sql = "SELECT * FROM filmorate_users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapUser(rs), id);
            log.info("User found: {}", user);
            return user;
        } catch (DataAccessException e) {
            log.error("User with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new UserNotFoundException(String.format("User with id %s not found", id));
        }
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
