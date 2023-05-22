package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Qualifier("dbStorage")
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbReviewStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        String sql = "INSERT INTO review (CONTENT, ISPOSITIVE, USERID, FILMID, USEFUL) VALUES (?,?,?,?,?)";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getUserID(),
                review.getFilmId(),
                review.getUseful()
        );

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE review SET CONTENT = ?, ISPOSITIVE = ?, USERID = ?, FILMID = ?, USEFUL = ? " +
                "WHERE REVIEWID = ?";

        var updatedCount = jdbcTemplate.update(sql,
                review.getContent(),
                review.isPositive(),
                review.getUserID(),
                review.getFilmId(),
                review.getUseful()
        );
        if (updatedCount == 0) {
            throw new FilmNotFoundException(String.format("Film with id %s not found", review.getReviewID()));
        }

        return review;
    }

    @Override
    public void deleteReview(Integer id) {

    }

    @Override
    public Review getReview(Integer id) {
        return null;
    }

    @Override
    public List<Review> getFilmReviews(Integer filmId, Integer count) {
        return null;
    }

    @Override
    public void addLike(Integer id, Integer userId) {

    }

    @Override
    public void addDislike(Integer id, Integer userId) {

    }

    @Override
    public void deleteLike(Integer id, Integer userId) {

    }

    @Override
    public void deleteDislike(Integer id, Integer userId) {

    }
}
