package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
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
    public DbFilmStorage(JdbcTemplate jdbcTemplate, @Qualifier("dbStorage") GenreStorage genreStorage,
                         @Qualifier("dbStorage") RatingStorage ratingStorage,
                         @Qualifier("dbStorage") LikesStorage likesStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
        this.likesStorage = likesStorage;
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("Adding film {}", film);
        String sql = "INSERT INTO films (name, description, release_date, duration, rating) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        log.debug("Added to DB with id {}", keyHolder.getKey().longValue());
        return updateFilmGenres(film);
    }

    @Override
    public Film putFilm(Film film) {
        log.debug("Updating film {}", film);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating = ? " +
                "WHERE film_id = ?";
        if (jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getMpa().getId(), film.getId()) <= 0) {
            log.error("Film with id {} not found", film.getId());
            throw new FilmNotFoundException(String.format("Film with id %s not found", film.getId()));
        }
        setMpaToFilm(film);
        return updateFilmGenres(film);
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films f LEFT JOIN mpa m ON f.rating = m.mpa_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
        log.info("Number of films registered: {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        String sql = "SELECT * FROM films f LEFT JOIN mpa m ON f.rating = m.mpa_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id GROUP BY f.film_id ORDER BY count(l.user_id) DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), limit);
        log.info("Number of top films: {}", films.size());
        return films;
    }

    @Override
    public Film findFilm(Long id) {
        log.info("Looking for film: {}", id);
        String sql = "SELECT * FROM films f JOIN mpa m ON f.rating = m.mpa_id WHERE film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapFilm(rs), id);
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
            log.info("Film found: {}", film);
        } catch (EmptyResultDataAccessException e) {
            log.error("Film with id {} not found", id);
            throw new FilmNotFoundException(String.format("Film wth id %s not found", id));
        }
        return film;
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
        return Film.builder().id(rs.getLong("film_id")).name(rs.getString("name")).description(rs.getString("description")).releaseDate(rs.getDate("release_date").toLocalDate()).duration(rs.getLong("duration")).mpa(Mpa.builder().id(rs.getInt("mpa_id")).name(rs.getString("mpa")).build()).genres(genreStorage.getFilmGenres(rs.getLong("film_id"))).build();
    }
}
