package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.EventType.REVIEW;
import static ru.yandex.practicum.filmorate.model.Operation.*;

@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewService(@Qualifier("dbStorage") ReviewStorage reviewStorage,
                         @Qualifier("dbStorage") EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.eventStorage = eventStorage;
    }

    @PostMapping
    public Review addReview(Review review) {
        log.info("Adding review {}", review);
        reviewStorage.addReview(review);
        log.info("Review added {}", review);
        eventStorage.addEvent(review.getUserId(), REVIEW, ADD, review.getReviewId());
        return review;
    }

    @PutMapping
    public Review putReview(Review review) {
        log.info("Updating review {}", review);
        Review r = reviewStorage.updateReview(review);
        eventStorage.addEvent(r.getUserId(), REVIEW, UPDATE, r.getFilmId());
        return r;
    }

    public void deleteReview(Long id) {
        log.info("Deleting review id {}", id);
        Review review = getReview(id);
        reviewStorage.deleteReview(id);
        eventStorage.addEvent(review.getUserId(), REVIEW, REMOVE, review.getReviewId());
    }

    public Review getReview(Long id) {
        log.info("Looking for review id {}", id);
        Review review = reviewStorage.getReview(id);
        log.info("Found review: {}", review);
        return review;
    }

    public List<Review> getFilmReviews(Long filmId, Integer count) {
        log.info("Looking for {} reviews of film id {}", count, filmId);
        List<Review> reviews = reviewStorage.getFilmReviews(filmId, count);
        log.info("Found {} reviews", reviews.size());
        return reviews;
    }

    public List<Review> getReviews(Integer count) {
        log.info("Looking for reviews of film id {}", count);
        List<Review> reviews = reviewStorage.getReviews(count);
        log.info("Found {} reviews", reviews.size());
        return reviews;
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Adding like from user id {} to review id {}", userId, reviewId);
        reviewStorage.addReviewLiking(reviewId, userId, true);
        log.info("Like added");
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Adding dislike from user id {} to review id {}", userId, reviewId);
        reviewStorage.addReviewLiking(reviewId, userId, false);
        log.info("Dislike added");
    }

    public void deleteLike(Long reviewId, Long userId) {
        log.info("Deleting like from user id {} to review id {}", userId, reviewId);
        reviewStorage.deleteReviewLiking(reviewId, userId);
        log.info("Like deleted");
    }

    public void deleteDislike(Long reviewId, Long userId) {
        log.info("Deleting dislike from user id {} to review id {}", userId, reviewId);
        reviewStorage.deleteReviewLiking(reviewId, userId);
        log.info("Deleting deleted");
    }
}
