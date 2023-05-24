package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("dbStorage")
@RequiredArgsConstructor
public class DbReviewStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    // Базовый запрос на извлечение Review из бд
    private final String getReviewsQuery =
            "SELECT REVIEW.REVIEW_ID, REVIEW.CONTENT, REVIEW.IS_POSITIVE, REVIEW.USER_ID, REVIEW.FILM_ID, CASE WHEN REVIEW_LIKE.rating IS NULL THEN 0 ELSE REVIEW_LIKE.rating END AS useful " +
                    "FROM REVIEW " +
                    "LEFT JOIN ( " +
                    "SELECT REVIEW_ID, sum(CASE WHEN IS_LIKED  THEN 1 ELSE -1 END) AS rating " +
                    "FROM REVIEW_LIKE " +
                    "GROUP BY REVIEW_ID) AS REVIEW_LIKE " +
                    "ON REVIEW.REVIEW_ID = REVIEW_LIKE.REVIEW_ID";

    @Override
    public Review addReview(Review review) {

        String sqlQuery = "insert into REVIEW (REVIEW_ID, CONTENT, IS_POSITIVE , USER_ID, FILM_ID) " +
                "values ( (SELECT(COALESCE(MAX(REVIEW_ID),0) + 1) FROM REVIEW) ,?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.getIsPositive());
                stmt.setLong(3, review.getUserId());
                stmt.setLong(4, review.getFilmId());
                return stmt;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Переданы неверные foreign keys для Review");
        }

        review.setReviewId(keyHolder.getKey().longValue());

        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sqlQuery = "UPDATE REVIEW SET CONTENT = ?, IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";

        var affectedRows = jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        if (affectedRows == 0) {
            throw new NotFoundException("Review не был обновлен");
        }

        return this.getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        String sqlQuery = "delete from REVIEW where REVIEW_ID = ?";

        var affectedRows = jdbcTemplate.update(sqlQuery, id);

        if (affectedRows == 0) {
            throw new NotFoundException("Review не был удален");
        }
    }

    @Override
    public Review getReview(Long id) {

        String sqlQuery = getReviewsQuery + " " +
                "WHERE REVIEW.REVIEW_ID = " + id;

        var queryResult = jdbcTemplate.query(sqlQuery, (x, y) -> mapRowToReview(x))
                .stream()
                .collect(Collectors.toList());

        if (queryResult.size() == 0) {
            throw new NotFoundException("Review не найден.");
        } else {
            return queryResult.get(0);
        }
    }

    @Override
    public List<Review> getFilmReviews(Long filmId, Integer count) {
        String sqlQuery = getReviewsQuery + " " +
                "WHERE FILM_ID = " + filmId + " " +
                "ORDER BY USEFUL DESC " +
                "LIMIT " + count;

        var queryResult = jdbcTemplate.query(sqlQuery, (x, y) -> mapRowToReview(x))
                .stream()
                .collect(Collectors.toList());

        return queryResult;
    }

    @Override
    public List<Review> getReviews(Integer count) {
        String sqlQuery = getReviewsQuery + " " +
                "ORDER BY USEFUL DESC " +
                "LIMIT " + count;

        var queryResult = jdbcTemplate.query(sqlQuery, (x, y) -> mapRowToReview(x))
                .stream()
                .collect(Collectors.toList());

        return queryResult;
    }

    public void addReviewLiking(Long reviewId, Long userId, Boolean isLiked) {

        String sqlQuery = "insert into REVIEW_LIKE " +
                "(user_id, review_id, is_liked) " +
                "values (?, ?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, reviewId, isLiked);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Переданый неверные значения идентификаторов review или user");
        }
    }

    @Override
    public void deleteReviewLiking(Long reviewId, Long userId) {
        String sqlQuery = "delete from REVIEW_LIKE " +
                "where review_id = ? and user_id = ?";

        var affectedRows = jdbcTemplate.update(sqlQuery, reviewId, userId);

        if (affectedRows == 0) {
            throw new NotFoundException("REVIEW_LIKE не был найден.");
        }
    }

    private Review mapRowToReview(ResultSet rs) throws SQLException {

        return Review
                .builder()
                .reviewId(rs.getLong("review.REVIEW_ID"))
                .content(rs.getString("review.content"))
                .isPositive(rs.getBoolean("review.IS_POSITIVE"))
                .userId(rs.getLong("review.USER_ID"))
                .filmId(rs.getLong("review.FILM_ID"))
                .useful(rs.getInt("useful"))
                .build();
    }
}