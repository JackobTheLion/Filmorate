package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
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
                .usingGeneratedKeyColumns("id");
        Map<String, Object> values = new HashMap<>();
        values.put("NAME", director.getName());
        director.setId(simpleJdbcInsert.executeAndReturnKey(values).longValue());
        return director;
    }

    @Override
    public void deleteDirector(Long id) {
        final String sql = "DELETE FROM director WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Director updateDirector(Director director) {
        validation(director);
        String sql = "UPDATE director SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public Director getDirector(Long id) {
        final String sql = "SELECT * FROM director WHERE id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id)
                .stream()
                .findAny().orElse(null);
    }

    @Override
    public List<Long> findFilmsByDirector(Long directorId, String sortBy) {
        String sqlByLikes = "SELECT fd.film_id, COUNT(fl.user_id) AS p " +
                "FROM film_directors AS fd " +
                "LEFT OUTER JOIN user_likes_film AS fl ON fd.film_id = fl.film_id " +
                "WHERE director_id = ? " +
                "GROUP BY fd.film_id " +
                "ORDER BY p ";
        String sqlByYear = "SELECT f.id AS film_id " +
                "FROM film AS f " +
                "INNER JOIN film_directors AS fd ON f.id = fd.film_id AND fd.director_id = ? " +
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

    public void validation(@Valid @RequestBody Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            log.error("director name is empty or contain blank");
            throw new ValidationException("name of director can not be empty");
        }
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
