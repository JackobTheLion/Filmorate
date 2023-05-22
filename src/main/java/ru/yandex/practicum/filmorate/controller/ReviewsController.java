package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

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

    @PostMapping
    public Review addReview(@NotNull @RequestBody Review review) {
        return reviewStub;
    }

    @PutMapping
    public Review putReview(@NotNull @RequestBody Review review) {
        return reviewStub;
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Integer id) {
        // Remove
    }

    @GetMapping("{id}")
    public Review getReview(@PathVariable Integer id) {
        return reviewStub;
    }

    //GET /reviews?filmId={filmId}&count={count}
    @GetMapping()
    public List<Review> getFilmReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        return List.of(reviewStub);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(Integer id, Integer userId) {
        // Do smth
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(Integer id, Integer userId) {
        // Do smth
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(Integer id, Integer userId) {
        // Do smth
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(Integer id, Integer userId) {
        // Do smth
    }
}
