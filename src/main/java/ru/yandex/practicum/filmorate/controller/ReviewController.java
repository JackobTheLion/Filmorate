package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.servlet.http.HttpServletRequest;
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
    public Review addReview(HttpServletRequest request, @NotNull @Valid @RequestBody Review review) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review putReview(HttpServletRequest request, @NotNull @Valid @RequestBody Review review) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return reviewService.putReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(HttpServletRequest request, @PathVariable Long id) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        reviewService.deleteReview(id);
    }

    @GetMapping("{id}")
    public Review getReview(HttpServletRequest request, @PathVariable Long id) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<Review> getFilmReviews(HttpServletRequest request, @RequestParam(required = false) Long filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (filmId == null) {
            return reviewService.getReviews(count);
        } else {
            return reviewService.getFilmReviews(filmId, count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(HttpServletRequest request, @PathVariable Long id, @PathVariable Long userId) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(HttpServletRequest request, @PathVariable Long id, @PathVariable Long userId) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(HttpServletRequest request, @PathVariable Long id, @PathVariable Long userId) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(HttpServletRequest request, @PathVariable Long id, @PathVariable Long userId) {
        log.info("Received a request to the endpoint: '{} {}', Request parameters: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        reviewService.deleteDislike(id, userId);
    }
}