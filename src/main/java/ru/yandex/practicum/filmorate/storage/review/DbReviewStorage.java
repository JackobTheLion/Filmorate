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

    @Override
    public Review addReview(Review review) {

        String sql = "INSERT INTO REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID) " +
                "VALUES (?,?,?,?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
                stmt.setString(1, review.getContent());
                stmt.setBoolean(2, review.getIsPositive());
                stmt.setLong(3, review.getUserId());
                stmt.setLong(4, review.getFilmId());
                return stmt;
            }, keyHolder);
        } catch (DataIntegrityViolationException e) {
            log.debug("User with id {} or film with id {} was not found", review.getUserId(), review.getFilmId());
            throw new NotFoundException(String.format("User with id %s or film with id %s was not found", review.getUserId(), review.getFilmId()));
        }

        review.setReviewId(keyHolder.getKey().longValue());
        log.debug("Review added: {}", review);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE REVIEW SET CONTENT = ?, IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";

        var affectedRows = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        if (affectedRows == 0) {
            log.debug("Review with id {} not found", review.getReviewId());
            throw new NotFoundException(String.format("Review with id %s was not found", review.getReviewId()));
        }

        log.debug("Review with id {} updated", review.getReviewId());
        return this.getReview(review.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        String sql = "DELETE FROM REVIEW WHERE REVIEW_ID = ?";
        var affectedRows = jdbcTemplate.update(sql, id);
        if (affectedRows == 0) {
            log.debug("Review with id {} was not found", id);
            throw new NotFoundException(String.format("Review with id %s was not found", id));
        }
        log.debug("Review id {} deleted", id);
    }

    @Override
    public Review getReview(Long id) {
        String sql = "SELECT REVIEW.REVIEW_ID, REVIEW.CONTENT, REVIEW.IS_POSITIVE, REVIEW.USER_ID, REVIEW.FILM_ID, " +
                "CASE WHEN REVIEW_LIKE.rating IS NULL THEN 0 ELSE REVIEW_LIKE.rating END AS useful " +
                "FROM REVIEW " +
                "LEFT JOIN ( " +
                "SELECT REVIEW_ID, sum(CASE WHEN IS_LIKED THEN 1 ELSE -1 END) AS rating " +
                "FROM REVIEW_LIKE " +
                "GROUP BY REVIEW_ID) AS REVIEW_LIKE " +
                "ON REVIEW.REVIEW_ID = REVIEW_LIKE.REVIEW_ID " +
                "WHERE REVIEW.REVIEW_ID = ?";

        var queryResult = jdbcTemplate.query(sql, (x, y) -> mapRowToReview(x), id)
                .stream()
                .collect(Collectors.toList());

        if (queryResult.size() == 0) {
            log.debug("Review with id {} was not found", id);
            throw new NotFoundException(String.format("Review with id %s was not found", id));
        } else {
            log.debug("Review found {}.", queryResult.get(0));
            return queryResult.get(0);
        }
    }

    @Override
    public List<Review> getFilmReviews(Long filmId, Integer count) {
        String sql = "SELECT REVIEW.REVIEW_ID, REVIEW.CONTENT, REVIEW.IS_POSITIVE, REVIEW.USER_ID, REVIEW.FILM_ID, " +
                "CASE WHEN REVIEW_LIKE.rating IS NULL THEN 0 ELSE REVIEW_LIKE.rating END AS useful " +
                "FROM REVIEW " +
                "LEFT JOIN ( " +
                "SELECT REVIEW_ID, sum(CASE WHEN IS_LIKED  THEN 1 ELSE -1 END) AS rating " +
                "FROM REVIEW_LIKE " +
                "GROUP BY REVIEW_ID) AS REVIEW_LIKE " +
                "ON REVIEW.REVIEW_ID = REVIEW_LIKE.REVIEW_ID " +
                "WHERE FILM_ID = ? " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        var queryResult = jdbcTemplate.query(sql, (x, y) -> mapRowToReview(x), filmId, count)
                .stream()
                .collect(Collectors.toList());

        log.debug("Found {} reviews of film id {}", queryResult.size(), filmId);
        return queryResult;
    }

    @Override
    public List<Review> getReviews(Integer count) {
        String sql = "SELECT REVIEW.REVIEW_ID, REVIEW.CONTENT, REVIEW.IS_POSITIVE, REVIEW.USER_ID, REVIEW.FILM_ID, " +
                "CASE WHEN REVIEW_LIKE.rating IS NULL THEN 0 ELSE REVIEW_LIKE.rating END AS useful " +
                "FROM REVIEW " +
                "LEFT JOIN ( " +
                "SELECT REVIEW_ID, sum(CASE WHEN IS_LIKED  THEN 1 ELSE -1 END) AS rating " +
                "FROM REVIEW_LIKE " +
                "GROUP BY REVIEW_ID) AS REVIEW_LIKE " +
                "ON REVIEW.REVIEW_ID = REVIEW_LIKE.REVIEW_ID " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        var queryResult = jdbcTemplate.query(sql, (x, y) -> mapRowToReview(x), count)
                .stream()
                .collect(Collectors.toList());

        log.debug("Found {} reviews", queryResult.size());
        return queryResult;
    }

    public void addReviewLiking(Long reviewId, Long userId, Boolean isLiked) {
        String sql = "INSERT INTO REVIEW_LIKE " +
                "(USER_ID, REVIEW_ID, IS_LIKED) " +
                "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, userId, reviewId, isLiked);
            log.debug("Adding like from user {} to review {}", userId, reviewId);
        } catch (DataIntegrityViolationException e) {
            log.debug("User with id {} or review with id {} was not found", userId, reviewId);
            throw new NotFoundException(String.format("User with id %s or review with id %s was not found", userId, reviewId));
        }
    }

    @Override
    public void deleteReviewLiking(Long reviewId, Long userId) {
        String sql = "DELETE FROM REVIEW_LIKE " +
                "WHERE REVIEW_ID = ? and USER_ID = ?";
        var affectedRows = jdbcTemplate.update(sql, reviewId, userId);
        if (affectedRows == 0) {
            log.debug("User with id {} or review with id {} was not found", userId, reviewId);
            throw new NotFoundException(String.format("User with id %s or review with id %s was not found", userId, reviewId));
        }
        log.debug("Like from user id {} to review id {} deleted", userId, reviewId);
    }

    private Review mapRowToReview(ResultSet rs) throws SQLException {
        return Review
                .builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}