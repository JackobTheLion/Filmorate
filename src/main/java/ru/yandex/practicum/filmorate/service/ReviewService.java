package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
@Slf4j
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @PostMapping
    public Review addReview(Review review) {
        log.info("Class: {}. Method: addReview. Obj: {}", ReviewService.class, review);
        return reviewStorage.addReview(review);
    }

    @PutMapping
    public Review putReview(Review review) {
        log.info("Class: {}. Method: putReview. Obj: {}", ReviewService.class, review);
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        log.info("Class: {}. Method: deleteReview. Id: {}", ReviewService.class, id);
        reviewStorage.deleteReview(id);
    }

    public Review getReview(Long id) {
        log.info("Class: {}. Method: getReview. Id: {}", ReviewService.class, id);
        return reviewStorage.getReview(id);
    }

    public List<Review> getFilmReviews(Long filmId, Integer count) {
        log.info("Class: {}. Method: getFilmReviews. FilmId: {}. Count: {}", ReviewService.class, filmId, count);
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public List<Review> getReviews(Integer count) {
        log.info("Class: {}. Method: getReviews. Count: {}", ReviewService.class, count);
        return reviewStorage.getReviews(count);
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Class: {}. Method: addLike. ReviewId: {}. UserId: {}", ReviewService.class, reviewId, userId);
        reviewStorage.addReviewLiking(reviewId, userId, true);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Class: {}. Method: addDislike. ReviewId: {}. UserId: {}", ReviewService.class, reviewId, userId);
        reviewStorage.addReviewLiking(reviewId, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(Long reviewId, Long userId) {
        log.info("Class: {}. Method: deleteLike. ReviewId: {}. UserId: {}", ReviewService.class, reviewId, userId);
        reviewStorage.deleteReviewLiking(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(Long reviewId, Long userId) {
        log.info("Class: {}. Method: deleteDislike. ReviewId: {}. UserId: {}", ReviewService.class, reviewId, userId);
        reviewStorage.deleteReviewLiking(reviewId, userId);
    }
}
