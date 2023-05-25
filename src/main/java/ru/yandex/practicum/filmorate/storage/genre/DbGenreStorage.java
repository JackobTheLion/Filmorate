package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> mapGenre(rs));
        log.info("Number of genres: {}", genres.size());
        return genres;
    }

    @Override
    public Genre findGenre(Long id) {
        log.info("Looking for genre: {}", id);
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            Genre genre = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapGenre(rs), id);
            log.info("Genre found: {}", genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            log.error("Genre with id {} not found", id);
            throw new GenreNotFoundException(String.format("Genre with id %s not found", id));
        }
    }

    @Override
    public Genre addGenreToFilm(Film film, Genre genre) {
        try {
            log.info("Adding genre id {} to film id {}", genre.getId(), film.getId());
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)";
            jdbcTemplate.update(sql, film.getId(), genre.getId());
            log.info("Genre id {} added to film id {}", genre.getId(), film.getId());
            return findGenre(genre.getId());
        } catch (DuplicateKeyException e) {
            log.error("Genre id {} already added to film id {}", genre.getId(), film.getId());
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("Film id {} or genre id {} not found", film.getId(), genre.getId());
            throw new NotFoundException(String.format("Film id %s or genre id %s not found", film.getId(), genre.getId()));
        }
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
    public List<Film> loadFilmsGenre(List<Film> films) {
        log.debug("Запрос к БД на загрузку жанров для нескольких фильмов");
        List<Long> ids = films.stream().map(Film::getId).collect(Collectors.toList());
        Map<Integer, Film> filmMap = new LinkedHashMap<>();
        for (Film f: films){
            filmMap.put((int) f.getId(),f);
        }
        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);
        NamedParameterJdbcTemplate namedJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        String sqlQuery = "SELECT *" +
                "FROM FILMS_GENRE F " +
                "INNER JOIN GENRE G on G.GENRE_ID = F.GENRE_ID " +
                "WHERE FILM_ID IN (:ids)";

        namedJdbcTemplate.query(sqlQuery, parameters, (rs, rowNum) ->
                filmMap.get(rs.getInt("FILM_ID"))
                        .getGenres()
                        .add(mapGenre(rs)));

        return new ArrayList<>(filmMap.values());

    }
}