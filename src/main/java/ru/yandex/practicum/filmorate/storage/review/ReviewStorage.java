package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long id);

    Review getReview(Long id);

    List<Review> getFilmReviews(Long filmId, Integer count);

    void addReviewLiking(Long reviewId, Long userId, Boolean isLiked);

    void deleteReviewLiking(Long reviewId, Long userId);

    List<Review> getReviews(Integer count);
}