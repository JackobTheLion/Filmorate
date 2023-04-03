package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Autowired FilmStorage filmStorage,
                       @Autowired UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addLike(Long filmId, Long userId) {
        log.info("Adding like from id {} to film id {}", userId, filmId);
        User user = userStorage.findUser(userId);
        if(user == null) {
            log.error("User with ID {} not found", userId);
            throw new UserNotFoundException(String.format("User with ID %s not found", userId));
        }
        Film film = filmStorage.findFilm(filmId);
        if(film == null) {
            log.error("Film with ID {} not found", filmId);
            throw new FilmNotFoundException(String.format("Film with ID %s not found", userId));
        }
        film.getLikes().add(userId);
        log.info("Like from id {} to film {} added", userId, filmId);
        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        log.info("Removing like from user id {} to film id {}", userId, filmId);
        User user = userStorage.findUser(userId);
        if(user == null) {
            log.error("User with ID {} not found", userId);
            throw new UserNotFoundException(String.format("User with ID %s not found", userId));
        }
        Film film = filmStorage.findFilm(filmId);
        if(film == null) {
            log.error("Film with ID {} not found", filmId);
            throw new FilmNotFoundException(String.format("Film with ID %s not found", userId));
        }
        if(!film.getLikes().remove(userId)) {
            log.error("Like from id {} to film id {} does not exist", userId, filmId);
            throw new LikeNotFoundException(String.format("Like from id %s to film id %s does not exist", userId, filmId));
        }
        log.info("Like from id {} to film {} removed", userId, filmId);
        return film;
    }

    public List<Film> getTopFilms(Integer count) {
        log.info("Returning top liked films, count {}", count);
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> (film1.getLikes().size() - film2.getLikes().size()) * -1)
                .limit(count)
                .collect(Collectors.toList());
    }
}
