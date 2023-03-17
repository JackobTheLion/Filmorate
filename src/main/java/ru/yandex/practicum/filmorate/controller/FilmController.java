package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>(); // Map<ID, Film>
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @PostMapping
    public Film addFilm (@NotNull @RequestBody Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            log.warn("Film name cannot be empty");
            throw new ValidationException("Film name cannot be empty");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Film description '{}' length exceeds max length: {}", film.getDescription(), MAX_DESCRIPTION_LENGTH);
            throw new ValidationException("Film description length exceeds max length (" + MAX_DESCRIPTION_LENGTH + ")");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Film release date {} is before min release date {}.", film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Film release date cannot be earlier than min release date "
                    + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Film duration should be more than 0");
            throw new ValidationException("Film duration should be more than 0");
        }
        if (films.containsKey(film.getId())) {
            log.warn("Film id {} already exists: {}", film.getId(), films.get(film.getId()));
            throw new ValidationException("Film with id " + film.getId() + " already exists");
        }
        id++;
        film.setId(id);
        films.put(id, film);
        log.info("Film added: {}", film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            log.warn("Film name cannot be empty");
            throw new ValidationException("Film name cannot be empty");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.warn("Film description length exceeds max length: {}", film.getDescription());
            throw new ValidationException("Film description length exceeds max length ("
                    + MAX_DESCRIPTION_LENGTH + ")");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than min release date "
                    + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Film duration should be more than 0");
            throw new ValidationException("Film duration should be more than 0");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        log.info("Film updated: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms () {
        log.info("Currently {} films saved.", films.size());
        return new ArrayList<Film>(films.values());
    }
}