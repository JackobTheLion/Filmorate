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

    public void deleteReview(Integer id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReview(Integer id) {
        return reviewStorage.getReview(id);
    }

    public List<Review> getFilmReviews(Integer filmId, Integer count) {
        return reviewStorage.getFilmReviews(filmId, count);
    }

    public void addLike(Integer id, Integer userId) {
        reviewStorage.addLike(id, userId);
    }

    public void addDislike(Integer id, Integer userId) {
        reviewStorage.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(Integer id, Integer userId) {
        reviewStorage.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(Integer id, Integer userId) {
        reviewStorage.deleteDislike(id, userId);
    }
}
