package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/reviews")
@RestController
public class ReviewsController {

    private Review reviewStub = new Review(
            1,
            "My first review",
            true,
            1,
            1,
            10
    );

    private final ReviewService reviewService;

    @Autowired
    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@NotNull @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review putReview(@NotNull @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("{id}")
    public Review getReview(@PathVariable Integer id) {
        return reviewService.getReview(id);
    }

    @GetMapping()
    public List<Review> getFilmReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.getFilmReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(Integer id, Integer userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(Integer id, Integer userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(Integer id, Integer userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(Integer id, Integer userId) {
        reviewService.deleteDislike(id, userId);
    }
}
