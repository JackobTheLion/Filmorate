package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

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
    public List<Mpa> getAllRatings() {
        String sql = "SELECT * FROM mpa";
        try {
            List<Mpa> rating = jdbcTemplate.query(sql, (rs, rowNum) -> mapRating(rs));
            log.info("Number of mpa: {}", rating.size());
            return rating;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Mpa findFilmRating(Long filmId) {
        String sql = "SELECT mpa FROM films f JOIN mpa m ON f.rating = m.mpa_id WHERE film_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRating(rs), filmId);
            log.info("Rating found: {}", mpa);
            return mpa;
        } catch (DataAccessException e) {
            log.error("Rating for film id {} not found", filmId);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new RatingNotFoundException(String.format("Rating with id %s not found", filmId));
        }
    }

    @Override
    public Mpa findRating(Long id) {
        log.info("Looking for mpa: {}", id);
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRating(rs), id);
            log.info("Rating found: {}", mpa);
            return mpa;
        } catch (DataAccessException e) {
            log.error("Rating with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new RatingNotFoundException(String.format("Rating with id %s not found", id));
        }
    }

    private Mpa mapRating(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa"))
                .build();
    }
}
