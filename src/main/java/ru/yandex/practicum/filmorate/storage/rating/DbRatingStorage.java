package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DbRatingStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAllRatings() {
        String sql = "SELECT * FROM ratings";
        try {
            List<Rating> rating = jdbcTemplate.query(sql, (rs, rowNum) -> mapRating(rs));
            log.info("Number of ratings: {}", rating.size());
            return rating;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Rating findRating(Long id) {
        log.info("Looking for ratings: {}", id);
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        try {
            Rating rating = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRating(rs), id);
            log.info("Rating found: {}", rating);
            return rating;
        } catch (DataAccessException e) {
            log.error("Rating with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new RatingNotFoundException(String.format("Rating with id %s not found", id));
        }
    }

    private Rating mapRating(ResultSet rs) throws SQLException {
        return Rating.builder()
                .id(rs.getLong("rating_id"))
                .mpa(rs.getString("mpa"))
                .build();
    }
}
