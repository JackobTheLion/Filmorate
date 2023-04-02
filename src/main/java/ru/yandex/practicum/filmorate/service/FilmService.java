package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public FilmService(@Autowired FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(Film film, User user) {
        log.info("Adding like from id {} to film {}", user.getId(), film);
        if(!film.getLikes().add(user.getId())) {
            log.info("Like from id {} to film {} already exist", user.getId(), film);
        }
        log.info("Like from id {} to film {} added", user.getId(), film);
        return film;
    }

    public Film removeLike(Film film, User user) {
        log.info("Removing like from id {} to film {}", user.getId(), film);
        if(!film.getLikes().remove(user.getId())) {
            log.info("Like from id {} to film {} does not exist", user.getId(), film);
        }
        log.info("Like from id {} to film {} removed", user.getId(), film);
        return film;
    }

    public List<Film> getTopFilms(int count) {
        log.info("Returning top liked films");
        if(count == 0) {
            log.info("Count equals 0, changing count to 10");
            count = 10;
        }
        return filmStorage.getFilms().stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
