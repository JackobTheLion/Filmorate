package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DirectorDbStorage implements DirectorDaoStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAll() {
        final String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director createDirector(Director director) {
        validation(director);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        final String sql = "DELETE FROM director WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Director updateDirector(Director director) {
        validation(director);
        String sql = "UPDATE director SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public Director getDirector(Long id) {
        final String sql = "SELECT * FROM director WHERE director_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id)
                .stream()
                .findAny().orElse(null);
    }

    @Override
    public List<Long> findFilmsByDirector(Long directorId, String sortBy) {
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
    public Set<Director> getDirectorsByFilm(Long film_id) {
        String sql = "SELECT d.director_id, d.name FROM director AS d, film_directors AS fd " +
                "WHERE d.director_id=fd.director_id AND fd.film_id=?";
        List<Director> list = jdbcTemplate.query(sql, (rs, rowNum) -> Director.builder()
                .id(rs.getLong(1))
                .name(rs.getString(2))
                .build(), film_id);
        return Set.copyOf(list);
    }

    @Override
    public void setDirectorsToFilm(Set<Director> directors, Long film_id) {
        String del = "DELETE FROM film_directors WHERE film_id=?";
        String ins = "INSERT INTO film_directors (film_id, director_id) VALUES (?,?)";

        jdbcTemplate.update(del, film_id);
        if (directors == null || directors.size() == 0) {
            return;
        }
        List<Director> list = List.copyOf(directors);
        jdbcTemplate.batchUpdate(ins, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, film_id);
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

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
