package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Qualifier("dbStorage")
@Slf4j
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
                stmt.setLong(4, film.getDuration());
                return stmt;
            }, keyHolder);
            film.setId(keyHolder.getKey().longValue());
            return film;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Film putFilm(Film film) {
        String sql = "UPDATE films SET " +
                "name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?" +
                "WHERE film_id = ?";
        try {
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getId());
            return film;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films";
        try {
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
            log.info("Number of films registered: {}", films.size());
            return films;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Film findFilm(Long id) {
        log.info("Looking for film: {}", id);
        String sql = "SELECT * FROM films WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapFilm(rs), id);
            log.info("Film found: {}", film);
            return film;
        } catch (DataAccessException e) {
            log.error("Film with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new FilmNotFoundException(String.format("Film with id %s not found", id));
        }
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .rating(rs.getString("rating"))
                .build();
    }
}
