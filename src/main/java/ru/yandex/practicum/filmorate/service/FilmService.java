package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@Slf4j
@Qualifier("dbStorage")
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("dbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        log.error("Trying to add film {}", film);
        return filmStorage.addFilm(film);
    }

    public Film putFilm(Film film) {
        log.error("Trying to put film {}", film);
        return filmStorage.putFilm(film);
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        log.info("Number of films registered: {}", films.size());
        return films;
    }

    public Film findFilm(Long id) {
        log.info("Looking for film with id: {}", id);
        return filmStorage.findFilm(id);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Adding like from id {} to film id {}", userId, filmId);
        filmStorage.addLike(filmId, userId);

    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Removing like from user id {} to film id {}", userId, filmId);
        filmStorage.removeLike(filmId, userId);
        log.info("Like from id {} to film {} removed", userId, filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Returning top liked films, count {}", count);
        return filmStorage.getPopularFilms(count);
    }
}
