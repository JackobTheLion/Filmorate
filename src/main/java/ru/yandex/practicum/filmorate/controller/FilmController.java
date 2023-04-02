package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(@Autowired FilmStorage filmStorage, @Autowired FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm (@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        return filmStorage.putFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        log.info("Currently {} films saved.", films.size());
        return films;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Adding like from id {} to film id {}", userId, filmId);
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Deleting like from id {} to film id {}", userId, filmId);
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular?count={count}")
    public List<Film> findPopularFilms(@RequestParam Integer count) {
        log.info("Showing top {} films", count);
        return filmService.getTopFilms(count);
    }
}