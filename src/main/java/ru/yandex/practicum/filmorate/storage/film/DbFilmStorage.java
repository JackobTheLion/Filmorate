package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("dbStorage")
@Slf4j
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;
    private final LikesStorage likesStorage;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("dbStorage") GenreStorage genreStorage,
                         @Qualifier("dbStorage") RatingStorage ratingStorage,
                         @Qualifier("dbStorage") LikesStorage likesStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.likesStorage = likesStorage;
    }

    @Override
    public Film addFilm(Film film) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
            setMpaToFilm(film);
            return updateFilmGenres(film);
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
                "duration = ?, " +
                "rating = ? " +
                "WHERE film_id = ?";
        try {
            jdbcTemplate.update(sql,
                    film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());

            setMpaToFilm(film);
            return updateFilmGenres(film);
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films f LEFT JOIN mpa m ON f.rating = m.mpa_id";
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
    public List<Film> getPopularFilms(Integer limit) {
        String sql = "SELECT * FROM films f LEFT JOIN mpa m ON f.rating = m.mpa_id WHERE f.film_id IN (" +
                "SELECT film_id FROM likes GROUP BY film_id ORDER BY count(distinct user_id) DESC) LIMIT ?;";
        try {
            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), limit);
            log.info("Number of top films: {}", films.size());
            return films;
        } catch (DataAccessException e) {
            log.error("DataAccessException message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Film findFilm(Long id) {
        log.info("Looking for film: {}", id);
        String sql = "SELECT * FROM films f JOIN mpa m ON f.rating = m.mpa_id WHERE film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapFilm(rs), id);
            log.info("Film found: {}", film);
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
            return film;
        } catch (DataAccessException e) {
            log.error("Film with id {} not found", id);
            log.error("DataAccessException message: {}", e.getMessage());
            throw new FilmNotFoundException(String.format("Film with id %s not found", id));
        }
    }

    public void addLike(Long filmId, Long userId) {
        likesStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        likesStorage.removeLike(filmId, userId);
    }

    private void setMpaToFilm(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            Mpa mpa = ratingStorage.findRating(film.getMpa().getId());
            film.getMpa().setName(mpa.getName());
        }
    }

    private Film updateFilmGenres(Film film) {
        genreStorage.removeGenreFromFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> duplicateGenres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                try {
                    genre.setName(genreStorage.addGenreToFilm(film, genre).getName());
                } catch (DuplicateKeyException e) {
                    duplicateGenres.add(genre);
                }
            }
            film.getGenres().removeAll(duplicateGenres);
        }
        return film;
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getLong("duration"))
                .mpa(Mpa.builder()
                        .id(rs.getInt("rating"))
                        .name(rs.getString("mpa"))
                        .build())
                .genres(genreStorage.getFilmGenres(rs.getLong("film_id")))
                .build();
    }
}
