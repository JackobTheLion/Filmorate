package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/reviews")
@RestController
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@NotNull @Valid @RequestBody Review review) {
        log.info("Adding review {}", review);
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review putReview(@NotNull @Valid @RequestBody Review review) {
        log.info("Updating review {}", review);
        return reviewService.putReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Deleting review id {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping("{id}")
    public Review getReview(@PathVariable Long id) {
        log.info("Looking for review id {}", id);
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<Review> getFilmReviews(@RequestParam(required = false) Long filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Looking for {} reviews of film id {}", count, filmId);
        if (filmId == null) {
            return reviewService.getReviews(count);
        } else {
            return reviewService.getFilmReviews(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Adding like from user id {} to review id {}", userId, id);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Adding dislike from user id {} to review id {}", userId, id);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Deleting like from user id {} to review id {}", userId, id);
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Deleting dislike from user id {} to review id {}", userId, id);
        reviewService.deleteDislike(id, userId);
    }
}