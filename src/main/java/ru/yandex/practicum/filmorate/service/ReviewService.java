package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @PostMapping
    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    @PutMapping
    public Review putReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(Long id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReview(Long id) {
        return reviewStorage.getReview(id);
    }

    public List<Review> getFilmReviews(Long filmId, Integer count) {
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public List<Review> getReviews(Integer count) {
        return reviewStorage.getReviews(count);
    }

    public void addLike(Long id, Long userId) {
        reviewStorage.addReviewLiking(id, userId, true);
    }

    public void addDislike(Long id, Long userId) {
        reviewStorage.addReviewLiking(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(Long id, Long userId) {
        reviewStorage.deleteReviewLiking(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(Long id, Long userId) {
        reviewStorage.deleteReviewLiking(id, userId);
    }
}
