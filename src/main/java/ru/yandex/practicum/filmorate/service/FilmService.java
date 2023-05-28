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
import ru.yandex.practicum.filmorate.storage.director.DbDirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikesStorage likesStorage;
    private final DbDirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("dbStorage") FilmStorage filmStorage,
                       @Qualifier("dbStorage") GenreStorage genreStorage,
                       @Qualifier("dbStorage") MpaStorage mpaStorage,
                       @Qualifier("dbStorage") LikesStorage likesStorage,
                       @Qualifier("dbStorage") DbDirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.likesStorage = likesStorage;
        this.directorStorage = directorStorage;
    }

    public Film addFilm(Film film) {
        log.info("Trying to add film {}", film);
        filmStorage.addFilm(film);
        setMpaToFilm(film);
        updateFilmGenres(film);
        directorStorage.setDirectorsToFilm(film.getDirectors(), film.getId());
        return findFilm(film.getId());
    }

    public Film putFilm(Film film) {
        log.info("Trying to put film {}", film);
        filmStorage.putFilm(film);
        setMpaToFilm(film);
        updateFilmGenres(film);
        directorStorage.setDirectorsToFilm(film.getDirectors(), film.getId());
        return findFilm(film.getId());
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        for (Film film : films) {
            enrichFilm(film);
        }
        log.info("Number of films registered: {}", films.size());
        return films;
    }

    public Film findFilm(Long id) {
        log.info("Looking for film with id: {}", id);
        Film film = filmStorage.findFilm(id);
        enrichFilm(film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("FilmId and User Id must be more than zero");
            throw new IllegalArgumentException("FilmId and User Id must be more than zero");
        }
        log.info("Adding like from id {} to film id {}", userId, filmId);
        likesStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (filmId <= 0 || userId <= 0) {
            log.error("FilmId and UserId must be more than zero");
            throw new NotFoundException("FilmId and UserId must be more than zero");
        }
        log.info("Removing like from user id {} to film id {}", userId, filmId);
        likesStorage.removeLike(filmId, userId);
        log.info("Like from id {} to film {} removed", userId, filmId);
    }

    public List<Film> getTopFilms(Integer count) {
        if (count <= 0) {
            log.error("Count must be more than zero");
            throw new IllegalArgumentException("Count must be more than zero");
        }
        List<Film> popularFilms = filmStorage.getPopularFilms(count);
        for (Film film : popularFilms) {
            enrichFilm(film);
        }
        log.info("Returning top liked films, count {}", count);
        return popularFilms;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        var films = filmStorage.getCommonFilms(userId, friendId);
        for (Film film : films) {
            enrichFilm(film);
        }
        films = films.stream().sorted((c1, c2) -> Integer.compare(c2.getLikes().size(), c1.getLikes().size()))
                .collect(Collectors.toList());
        return films;
    }

    public void deleteFilm(Long id) {
        log.info("Deleting film with id {}", id);
        filmStorage.deleteFilm(id);

    }

    public List<Film> findFilmsByDirector(Long directorId, String sortBy) {
        if (directorStorage.getDirector(directorId) == null) {
            throw new NotFoundException("director with that id does not exist");
        }
        List<Film> films = new ArrayList<>();
        List<Long> filmsId = directorStorage.findFilmsByDirector(directorId, sortBy);
        for (Long id : filmsId) {
            films.add(findFilm(id));
        }
        return films;
    }

    private Film enrichFilm(Film film) {
        film.setGenres(genreStorage.getFilmGenres(film.getId()));
        film.setLikes(likesStorage.getLikes(film.getId()).stream()
                .map(f -> f.getUserId())
                .collect(Collectors.toList()));
        film.setDirectors(directorStorage.getDirectorsByFilm(film.getId()));
        return film;
    }

    public List<Film> getSearch(String query, String by) {
        query = "'%" + query.toLowerCase().replace("_", "\\_").replace("%", "\\%") + "%'";
        boolean hasTitle, hasDirector;
        hasTitle = by.contains("title");
        hasDirector = by.contains("director");

        List<Film> searchList = List.of();
        if (hasDirector && hasTitle) {
            log.info("Returning search films. Title, director with text = {}", query);
            searchList = filmStorage.getSearch("LOWER(f.name) LIKE " + query);
            //TODO Добавить режиссёра в текст поиска!
        } else if (hasTitle) {
            log.info("Returning search films. Title with text = {}", query);
            searchList = filmStorage.getSearch("LOWER(f.name) LIKE " + query);
        } else if (hasDirector) {
            log.info("Returning search films. Director with text = {}", query);
            //TODO Добавить режиссёра в текст поиска!
            log.error("DIRECTORS NOT EXIST");
        } else {
            log.error("Parameter \"by\" not found by = {} ", by);
            throw new NotFoundException("Parameter \"by\" is incorrect");
        }

        searchList.forEach(film -> film.setGenres(genreStorage.getFilmGenres(film.getId())));
        // Добавление жанров к выводу

        //TODO Добавить режиссёра как жанры!

        return searchList;
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
                    String genreName = genreStorage.addGenreToFilm(film, genre).getName();
                    genre.setName(genreName);
                } catch (DuplicateKeyException e) {
                    duplicateGenres.add(genre);
                }
            }
            film.getGenres().removeAll(duplicateGenres);
        }
        return film;
    }
}
