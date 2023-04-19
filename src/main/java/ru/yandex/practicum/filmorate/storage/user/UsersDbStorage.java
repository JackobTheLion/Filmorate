package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Qualifier("userDbStorage")
@Slf4j
public class UsersDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public UsersDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        return null;
    }

    @Override
    public User putUser(User user) {
        return null;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM filmorate_users";
        List<User> users = jdbcTemplate.query(sql,(rs, rowNum) -> makeUser(rs));
        log.info("Number of users registered: {}", users.size());
        return null;
    }

    @Override
    public User findUser(Long id) {
        return null;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthdate = rs.getDate("birthdate").toLocalDate();

        return new User(id, email, login, name, birthdate);
    }
}
