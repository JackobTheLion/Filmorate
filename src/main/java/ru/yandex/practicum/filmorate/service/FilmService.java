package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;

    @Autowired
    public FilmService(@Qualifier("dbStorage") FilmStorage filmStorage,
                       @Qualifier("dbStorage") GenreStorage genreStorage,
                       @Qualifier("dbStorage") MpaStorage mpaStorage,
                       @Qualifier("dbStorage") LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.likesStorage = likesStorage;
    }

    public Film addFilm(Film film) {
        log.error("Trying to add film {}", film);
        setMpaToFilm(film);
        filmStorage.addFilm(film);
        return updateFilmGenres(film);
    }

    public Film putFilm(Film film) {
        log.error("Trying to put film {}", film);
        setMpaToFilm(film);
        filmStorage.putFilm(film);
        return updateFilmGenres(film);
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        for (Film film : films) {
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }
        log.info("Number of films registered: {}", films.size());
        return films;
    }

    public Film findFilm(Long id) {
        log.info("Looking for film with id: {}", id);
        Film film = filmStorage.findFilm(id);
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            log.info("FilmId and User Id must be more than zero");
            throw new IllegalArgumentException("FilmId and User Id must be more than zero");
        }
        log.info("Adding like from id {} to film id {}", userId, filmId);
        likesStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            log.info("FilmId and UserId must be more than zero");
            throw new NotFoundException("FilmId and UserId must be more than zero");
        }
        log.info("Removing like from user id {} to film id {}", userId, filmId);
        likesStorage.removeLike(filmId, userId);
        log.info("Like from id {} to film {} removed", userId, filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            log.info("Count must be more than zero");
            throw new IllegalArgumentException("Count must be more than zero");
        }
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        for (Film film : popularFilms) {
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
        }
        log.info("Returning top liked films, count {}", count);
        return popularFilms;
    }

    private void setMpaToFilm(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            Mpa mpa = mpaStorage.findMpa(film.getMpa().getId());
            film.getMpa().setName(mpa.getName());
            log.info("Mpa {} added to film id {}", mpa, film.getId());
        }
    }

    private Film updateFilmGenres(Film film) {
        genreStorage.removeGenreFromFilm(film);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Genre> duplicateGenres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                try {
                    genre.setName(genreStorage.addGenreToFilm(film, genre).getName());
                } catch (DuplicateKeyException e) {
                    duplicateGenres.add(genre);
                }
            }
            film.getGenres().removeAll(duplicateGenres);
        }
        return film;
    }
}
