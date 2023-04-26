package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DbMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        List<Mpa> mpa = jdbcTemplate.query(sql, (rs, rowNum) -> mapRating(rs));
        log.info("Number of mpa: {}", mpa.size());
        return mpa;
    }

    @Override
    public Mpa findMpa(Long id) {
        log.info("Looking for mpa: {}", id);
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        try {
            Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRating(rs), id);
            log.info("Mpa found: {}", mpa);
            return mpa;
        } catch (EmptyResultDataAccessException e) {
            log.error("Mpa with id {} not found", id);
            throw new MpaNotFoundException(String.format("Mpa with id %s not found", id));
        }
    }

    private Mpa mapRating(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("mpa"))
                .build();
    }
}
