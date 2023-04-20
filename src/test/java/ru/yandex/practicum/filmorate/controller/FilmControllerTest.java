package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage.MIN_RELEASE_DATE;

public class FilmControllerTest {
    FilmController filmController;
    FilmService filmService;
    FilmStorage filmStorage;
    UserStorage userStorage;
    Film film1;
    Film film2;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);

        filmController = new FilmController(filmService);

        film1 = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .build();

        film2 = Film.builder()
                .name("Крепкий орешек 2")
                .description("Крутой боевик с Брюсом Уиллисом. Вторая часть")
                .releaseDate(LocalDate.of(1990, 07, 2))
                .duration(124)
                .build();

        user1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("userName")
                .build();

        user2 = User.builder()
                .email("other@email.ru")
                .login("other")
                .birthday(LocalDate.of(1980, 12, 26))
                .name("otherName")
                .build();
    }

    public void init() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);
    }

    @Test
    public void addFilmNormal() {
        filmController.addFilm(film1);
        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film1);

        List<Film> savedFilms = filmController.getFilms();
        assertEquals(expectedFilms, savedFilms);
    }

    @Test
    public void addFilmReleaseBeforeMinDate() {
        Film film1 = Film.builder()
                        .name("Крепкий орешек")
                        .description("Крутой боевик с Брюсом Уиллисом")
                        .releaseDate(MIN_RELEASE_DATE.minusDays(1))
                        .duration(133)
                        .build();
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });

        String expectedMessage = "Film release date cannot be earlier than min release date "
                + MIN_RELEASE_DATE;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size());
    }

    @Test
    public void addFilmIdAlreadyExists() {
        filmController.addFilm(film1);
        Film film1 = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(this.film1.getId())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });

        String expectedMessage = "Film with id 1 already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(this.film1, films.get(0));
    }

    @Test
    public void putFilmNormal() {
        filmController.addFilm(film1);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(film1.getId())
                .build();

        filmController.putFilm(updatedFilm);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(updatedFilm, films.get(0));
    }

    @Test
    public void putFilmReleaseBeforeMinDate() {
        filmController.addFilm(film1);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(MIN_RELEASE_DATE.minusDays(1))
                .duration(133)
                .id(film1.getId())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film release date cannot be earlier than min release date " + MIN_RELEASE_DATE;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film1, films.get(0));
    }

    @Test
    public void putFilmWrongId() {
        filmController.addFilm(film1);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(film1.getId() + 100)
                .build();

        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film with id " + updatedFilm.getId() + " does not exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film1, films.get(0));
    }

    @Test
    public void getFilms() {
        filmController.addFilm(film1);
        List<Film> expectedFilms  = new ArrayList<>();
        expectedFilms.add(film1);
        List<Film> savedFilms = filmController.getFilms();
        assertEquals(expectedFilms, savedFilms);
    }

    @Test
    public void getFilmsWhenEmpty() {
        List<Film> savedFilms = filmController.getFilms();
        assertTrue(savedFilms.isEmpty());
    }

    @Test
    public void addLikeNormal() {
        init();
        filmController.addLike(film1.getId(), user1.getId());

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());

        Set<Long> savedLikes = film1.getLikes();

        assertEquals(expectedLikes, savedLikes);
    }

    @Test
    public void addLikeWrongUserOrFilmId() {
        init();

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            filmController.addLike(film1.getId(), -1L);
        });

        String expectedMessage = "FilmId and User Id must be more than zero";
        String actualMessage = exception1.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            filmController.addLike(-1L, user1.getId());
        });

        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage, actualMessage2);
    }

    @Test
    public void removeLikeNormal() {
        init();
        filmController.addLike(film1.getId(), user1.getId());
        filmController.removeLike(film1.getId(), user1.getId());

        assertTrue(film1.getLikes().isEmpty());
    }

    @Test
    public void removeLikeWrongUserOrFilmId() {
        init();
        filmController.addLike(film1.getId(), user1.getId());

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            filmController.removeLike(film1.getId(), -1L);
        });

        String expectedMessage = "FilmId and UserId must be more than zero";
        String actualMessage = exception1.getMessage();
        assertEquals(expectedMessage, actualMessage);
        assertEquals(expectedLikes, filmStorage.findFilm(film1.getId()).getLikes());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            filmController.removeLike(-1L, user1.getId());
        });

        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage, actualMessage2);
        assertEquals(expectedLikes, filmStorage.findFilm(film1.getId()).getLikes());
    }

    @Test
    public void getPopularNormal() {
        init();
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> expectedPopular = new ArrayList<>();
        expectedPopular.add(film1);
        expectedPopular.add(film2);

        List<Film> savedPopular = filmController.findPopularFilms(10);

        assertEquals(expectedPopular, savedPopular);
    }

    @Test
    public void getPopularWrongCount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            filmController.findPopularFilms(-1);
        });

        String expectedMessage = "Count must be more than zero";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void findFilmNormal() {
        init();
        Film savedFilm = filmController.findFilm(film1.getId());
        assertEquals(film1, savedFilm);
    }

    @Test
    public void findFilmWrongId() {
        init();
        Long wrongId = film1.getId() + 1000;

        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmController.findFilm(wrongId);
        });

        String expectedMessage = String.format("Film with id %s not found", wrongId);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}