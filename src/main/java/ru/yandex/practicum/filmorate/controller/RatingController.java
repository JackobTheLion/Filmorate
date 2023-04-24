package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RequestMapping("/mpa")
@RestController
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(@Qualifier("dbStorage") RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Mpa> getAllRatings() {
        return ratingService.getAllRatings();
    }

    @GetMapping("/{ratingId}")
    public Mpa findRating(@PathVariable Long ratingId) {
        return ratingService.findRating(ratingId);
    }
}
