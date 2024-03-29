package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("dbStorage")
@Slf4j
public class DbFilmStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("Adding film {}", film);
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?,?,?,?,?)";
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
        return film;
    }

    @Override
    public Film putFilm(Film film) {
        log.debug("Updating film {}", film);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";
        if (jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                Date.valueOf(film.getReleaseDate()), film.getDuration(),
                film.getMpa().getId(), film.getId()) <= 0) {
            log.error("Film with id {} not found", film.getId());
            throw new FilmNotFoundException(String.format("Film with id %s not found", film.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id ORDER BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
        log.info("Number of films registered: {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilms(Integer limit) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, m.mpa_id, m.mpa FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON l.film_id = f.film_id GROUP BY f.film_id, m.mpa_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), limit);
        log.info("Number of top films: {}", films.size());
        return films;
    }

    @Override
    public List<Film> getSearch(String sqlText) {
        String sql = "SELECT f.*, m.* " +
                "FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes l ON l.film_id = f.film_id " +
                "LEFT JOIN  film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN director d ON fd.director_id = d.director_id " +
                "WHERE " + sqlText +                     // Безопасная инъекция для упрощения жизни всем
                // В сервис классе выполняется условие по параметрам которые ввёл пользователь,
                // а в условии разработчик сам пишет какой sql код нужен в данный момент.
                " GROUP BY f.film_id, m.mpa_id ORDER BY COUNT(l.user_id) DESC";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
        log.info("Number of search films: {}", films.size());
        return films;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = "SELECT f.*, m.* " +
                "FROM likes " +
                "JOIN likes l ON l.film_id = likes.film_id " +
                "JOIN films f on f.film_id = l.film_id " +
                "JOIN mpa m on f.mpa_id = m.mpa_id " +
                "WHERE l.user_id = ? AND likes.user_id = ?";

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), userId, friendId);
        log.info("List of common films: {}", films.size());
        return films;
    }

    @Override
    public List<Film> getPopularFilms(Integer limit, Long genreId, Integer year) {
        String sql;
        List<Film> films;
        if (genreId != 0 && year != 0) {
            sql = "SELECT f.*, m.* " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN likes l ON l.film_id = f.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ? AND g.genre_id = ? " +
                    "GROUP BY f.film_id, m.mpa_id ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), year, genreId, limit);
        } else if (genreId == 0) {
            sql = "SELECT f.*, m.* " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN likes l ON l.film_id = f.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ?" +
                    "GROUP BY f.film_id, m.mpa_id ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), year, limit);
        } else {
            sql = "SELECT f.*, m.* " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN likes l ON l.film_id = f.film_id " +
                    "WHERE g.genre_id = ? " +
                    "GROUP BY f.film_id, m.mpa_id ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";
            films = jdbcTemplate.query(sql, (rs, rowNum) -> mapFilm(rs), genreId, limit);
        }

        log.info("Number of most populars films: {}", films.size());
        return films;
    }

    @Override
    public Film findFilm(Long id) {
        log.info("Looking for film: {}", id);
        String sql = "SELECT * FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id = ?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapFilm(rs), id);
            log.info("Film found: {}", film);
        } catch (EmptyResultDataAccessException e) {
            log.error("Film with id {} not found", id);
            throw new FilmNotFoundException(String.format("Film wth id %s not found", id));
        }
        return film;
    }

    @Override
    public List<Film> findAllFilmsByIds(List<Long> ids) {
        var sqlQuery = "SELECT * FROM films f LEFT JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id IN (:ids)";
        var idsParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.query(sqlQuery, idsParams, (rs, rowNum) -> mapFilm(rs));
    }

    @Override
    public void deleteFilm(Long id) {
        String sql = "DELETE from films WHERE film_id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result == 1) {
            log.info("Film with id {} deleted", id);
        } else {
            log.info("Film with id {} not found", id);
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
                .mpa(Mpa.builder()
                        .id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa"))
                        .build())
                .build();
    }
}
