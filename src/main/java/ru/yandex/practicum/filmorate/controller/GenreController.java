package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@Slf4j
@RequestMapping("/genre")
@RestController
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController (@Qualifier("dbStorage") GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAllRatings() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Genre findRating(@PathVariable Long genreId) {
        return genreService.findGenre(genreId);
    }
}
