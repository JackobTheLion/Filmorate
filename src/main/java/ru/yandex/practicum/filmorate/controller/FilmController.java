package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;

    public FilmController(@Autowired FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
}