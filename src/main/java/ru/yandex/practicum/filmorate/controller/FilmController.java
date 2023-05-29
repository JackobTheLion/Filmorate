package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film putFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        return filmService.putFilm(film);
    }

    @GetMapping
    public List<Film> getFilms() {
        List<Film> films = filmService.getFilms();
        log.info("Currently {} films saved.", films.size());
        return films;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Adding like from id {} to film id {}", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Deleting like from id {} to film id {}", userId, filmId);
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count,
                                       @RequestParam(required = false, defaultValue = "0") Long genreId,
                                       @RequestParam(required = false, defaultValue = "0") @Positive Integer year) {
        if (genreId == 0 && year == 0) {
            log.info("Showing top {} films", count);
            return filmService.getTopFilms(count);
        } else {
            log.info("Looking top with count: {}, genreId: {}, year: {} ", count, genreId, year);
            return filmService.getTopFilms(count, genreId, year);
        }
    }

    @GetMapping("/search")
    public List<Film> findPopularFilms(@RequestParam(required = true, defaultValue = "10") String query,
                                       @RequestParam(required = false, defaultValue = "title") String by) {
        log.info("Search films. Text: {}, by: {}", query, by);
        return filmService.getSearch(query, by);
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Long id) {
        log.info("Looking for film ID {}", id);
        return filmService.findFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Request commmon films with userId = {} & friendId = {} ", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        log.info("Deleting film with id {}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findFilmsByDirector(@PathVariable Long directorId, @RequestParam(required = false) String sortBy) {
        return filmService.findFilmsByDirector(directorId, sortBy);
    }
}