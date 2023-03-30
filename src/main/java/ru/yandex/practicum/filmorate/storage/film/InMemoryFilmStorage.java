package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int id = 0;
    @Override
    public Film addFilm(Film film) {
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

    @Override
    public Film putFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Film release date cannot be earlier than min release date");
            throw new ValidationException("Film release date cannot be earlier than min release date "
                    + MIN_RELEASE_DATE);
        }
        if (!films.containsKey(film.getId())) {
            log.error("Film with id " + film.getId() + "does not exist");
            throw new ValidationException("Film with id " + film.getId() + " does not exist");
        }
        films.put(film.getId(), film);
        log.info("Film updated: {}", film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}