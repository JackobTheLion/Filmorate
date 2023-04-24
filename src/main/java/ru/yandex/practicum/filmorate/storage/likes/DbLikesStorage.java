package ru.yandex.practicum.filmorate.storage.likes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

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
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?,?)";
        if (jdbcTemplate.update(sql, filmId, userId) != 1) {
            log.error("User with id {} or film with id {} not found", userId, filmId);
            throw new NotFoundException(String.format("User with id %s or film with id %s not found", userId, filmId));
        }
        log.info("Like from id {} to film {} added", userId, filmId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        log.info("Removing like from user id {} to film id {}", userId, filmId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.update(sql, filmId, userId) != 1) {
            log.error("User with id {} or film with id {} not found", userId, filmId);
            throw new NotFoundException(String.format("User with id %s or film with id %s not found", userId, filmId));
        }
        log.info("Like from id {} to film {} removed", userId, filmId);
    }
}
