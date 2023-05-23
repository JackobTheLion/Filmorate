package ru.yandex.practicum.filmorate.storage.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

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
        /*String sql = "INSERT INTO review (CONTENT, ISPOSITIVE, USERID, FILMID, USEFUL) VALUES (?,?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"reviewid"});
            stmt.setString(1, review.getContent());
            stmt.setString(2, review.getIsPositive().toString());
            stmt.setString(3, review.getUserId().toString());
            stmt.setString(4, review.getFilmId().toString());
            stmt.setString(5, review.getUseful() + "");
            return stmt;
        });*/

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("reviewid");

        try {
            review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).longValue());
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Переданы неверные foreign keys");
        }

        // Если id не быд присвоен
        if (review.getReviewId() == null) {
            throw new ValidationException("Ревью не был добавлен в бд. Содержит невалидные данные");
        }

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE review SET CONTENT = ?, ISPOSITIVE = ?, USERID = ?, FILMID = ?, USEFUL = ? " +
                "WHERE REVIEWID = ?";

        var updatedCount = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful()
        );

        if (updatedCount == 0) {
            throw new ReviewNotFoundException(String.format("Review with id %s not found", review.getReviewId()));
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
