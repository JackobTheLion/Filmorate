package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
<<<<<<< Updated upstream
import java.time.LocalDate;
import java.util.*;
=======
import java.util.List;
>>>>>>> Stashed changes

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;

    @PostMapping
    public Film addFilm (@NotNull @Valid @RequestBody Film film) {
        log.info("POST request received: {}", film);
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date {} is before min release date {}.", film.getReleaseDate(), MIN_RELEASE_DATE);
            throw new ValidationException("Film release date cannot be earlier than min release date "
                    + MIN_RELEASE_DATE);
        }
        if (films.containsKey(film.getId())) {
            log.error("Film id {} already exists: {}", film.getId(), films.get(film.getId()));
            throw new ValidationException("Film with id " + film.getId() + " already exists");
        }
        id++;
        film.setId(id);
        films.put(id, film);
        log.info("Film added: {}", film);
        return film;
    }

    @PutMapping
    public Film putFilm(@NotNull @Valid @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than min release date "
                    + MIN_RELEASE_DATE);
        }
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        log.info("Film updated: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms () {
        log.info("Currently {} films saved.", films.size());
        return new ArrayList<>(films.values());
    }
}