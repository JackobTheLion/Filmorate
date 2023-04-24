package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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

    @Override
    public Genre addGenreToFilm(Film film, Genre genre) {
        try {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        } catch (DuplicateKeyException e) {
            log.info("Genre id {} already added to film id {}", genre.getId(), film.getId());
            throw e;
        }
        return findGenre(genre.getId());
    }

    @Override
    public List<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT * FROM film_genre fg JOIN genre g ON fg.genre_id = g.genre_id WHERE film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapGenre(rs), filmId);
    }

    public void removeGenreFromFilm(Film film) {
        String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    private Genre mapGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
