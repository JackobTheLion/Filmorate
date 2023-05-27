package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DirectorStorage implements DbDirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAllDirectors() {
        log.info("Query all Directors");
        final String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapDirector(rs));
    }

    @Override
    public Director createDirector(Director director) {
        log.info("Create Director: {}", director.getName());
        String sql = "INSERT INTO director (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());

        log.debug("Director added to DB with id {}", director.getId());

        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        log.info("Delete Director {}", id);
        final String sql = "DELETE FROM director WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Update Director: {}", director);
        validation(director);
        String sql = "UPDATE director SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public Director getDirector(Long id) {
        log.info("Get Director {}", id);
        final String sql = "SELECT * FROM director WHERE director_id = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapDirector(rs), id);
        } catch (EmptyResultDataAccessException e) {
            log.error("Director with id {} not found", id);
            throw new NotFoundException(String.format("Director wth id %s not found", id));
        }
        return director;
    }

    @Override
    public List<Long> findFilmsByDirector(Long directorId, String sortBy) {
        log.info("Query findFilmsByDirector director:{} sortBy:{}", directorId, sortBy);
        String sqlByLikes = "SELECT fd.film_id, COUNT(fl.user_id) AS p " +
                "FROM film_directors AS fd " +
                "LEFT OUTER JOIN likes AS fl ON fd.film_id = fl.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY fd.film_id " +
                "ORDER BY p ";
        String sqlByYear = "SELECT f.film_id " +
                "FROM films AS f " +
                "INNER JOIN film_directors AS fd ON f.film_id = fd.film_id AND fd.director_id = ? " +
                "ORDER BY f.release_date";
        List<Film> films = new ArrayList<>();
        if (sortBy.equals("year")) {
            return jdbcTemplate.query(sqlByYear,
                    (rs, rowNum) -> rs.getLong("film_id"), directorId);
        } else {
            return jdbcTemplate.query(sqlByLikes,
                    (rs, rowNum) -> rs.getLong("film_id"), directorId);
        }
    }

    @Override
    public Set<Director> getDirectorsByFilm(Long filmId) {
        log.info("Query getDirectorsByFilm film:{}", filmId);
        String sql = "SELECT d.director_id, d.name FROM director AS d, film_directors AS fd " +
                "WHERE d.director_id=fd.director_id AND fd.film_id=?";
        List<Director> list = jdbcTemplate.query(sql, (rs, rowNum) -> Director.builder()
                .id(rs.getLong(1))
                .name(rs.getString(2))
                .build(), filmId);
        return Set.copyOf(list);
    }

    @Override
    public void setDirectorsToFilm(Set<Director> directors, Long filmId) {
        String del = "DELETE FROM film_directors WHERE film_id=?";
        String ins = "INSERT INTO film_directors (film_id, director_id) VALUES (?,?)";

        jdbcTemplate.update(del, filmId);
        if (directors == null || directors.size() == 0) {
            return;
        }
        List<Director> list = List.copyOf(directors);
        jdbcTemplate.batchUpdate(ins, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, list.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                }
        );
    }

    public void validation(@Valid @RequestBody Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            log.error("director name is empty or contain blank");
            throw new ValidationException("name of director can not be empty");
        }
    }

    private Director mapDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
