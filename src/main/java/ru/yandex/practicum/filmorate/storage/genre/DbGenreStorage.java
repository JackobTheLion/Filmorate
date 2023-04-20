package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("dbStorage")
public class DbGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre";
        try {
            List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapGenre(rs));
            log.info("Number of genres: {}", genres.size());
            return genres;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Genre findGenre(Long id) {
        log.info("Looking for genre: {}", id);
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapGenre(rs), id);
            log.info("Genre found: {}", genre);
            return genre;
        } catch (DataAccessException e) {
            log.error("Genre with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new GenreNotFoundException(String.format("Genre with id %s not found", id));
        }
    }

    private Genre mapGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .genre(rs.getString("genre"))
                .build();
    }
}
