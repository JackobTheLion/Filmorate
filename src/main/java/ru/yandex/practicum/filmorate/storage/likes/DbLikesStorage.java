package ru.yandex.practicum.filmorate.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DbLikesStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbLikesStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        try {
            String sql = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Like from id {} to film {} added", userId, filmId);
        } catch (DuplicateKeyException e) {
            log.error("Like from user id {} to film id {} already exists", userId, filmId);
            throw new ValidationException(String.format("Like from user id %s to film id %s already exists",
                    userId, filmId));
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}
