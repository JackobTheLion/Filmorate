package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmServiceTest {
    FilmStorage filmStorage;
    UserStorage userStorage;
    FilmService filmService;
    RatingStorage ratingStorage;
    Film film1;
    Film film2;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
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
    public void addLikeNormal() {
        init();

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        Set<Long> savedLikes = filmStorage.findFilm(film1.getId()).getLikes();

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());
        expectedLikes.add(user2.getId());

        assertEquals(expectedLikes, savedLikes);
    }

    @Test
    public void addLikeUserFilmDoesNotExist() {
        init();

        Long wrongID = film1.getId() + 1000;

        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmService.addLike(wrongID, user1.getId());
        });

        String expectedMessage = String.format("Film with id %s not found", wrongID);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        assertTrue(film1.getLikes().isEmpty());
    }

    @Test
    public void addLikeUserDoesNotExist() {
        init();

        Long wrongID = user1.getId() + 1000;

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            filmService.addLike(film1.getId(), wrongID);
        });

        String expectedMessage = String.format("User with id %s not found", wrongID);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        assertTrue(film1.getLikes().isEmpty());
    }

    @Test
    public void removeLikeNormal() {
        init();

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());
        expectedLikes.add(user2.getId());

        Set<Long> savedLikes = filmStorage.findFilm(film1.getId()).getLikes();

        assertEquals(expectedLikes, savedLikes);
    }

    @Test
    public void removeLikeUserDoesNotExist() {
        init();
        filmService.addLike(film1.getId(), user1.getId());

        Long wrongID = user1.getId() + 1000;

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            filmService.removeLike(film1.getId(), wrongID);
        });

        String expectedMessage = String.format("User with id %s not found", wrongID);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());

        assertEquals(expectedLikes, film1.getLikes());
    }

    @Test
    public void removeLikeFilmDoesNotExist() {
        init();
        filmService.addLike(film1.getId(), user1.getId());

        Long wrongID = film1.getId() + 1000;

        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmService.removeLike(wrongID, user1.getId());
        });

        String expectedMessage = String.format("Film with id %s not found", wrongID);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());

        assertEquals(expectedLikes, film1.getLikes());
    }

    @Test
    public void removeLikeDoesNotExist() {
        init();
        filmService.addLike(film1.getId(), user1.getId());

        Exception exception = assertThrows(LikeNotFoundException.class, () -> {
            filmService.removeLike(film1.getId(), user2.getId());
        });

        String expectedMessage = String.format("Like from id %s to film id %s does not exist", user2.getId(), film1.getId());
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        Set<Long> expectedLikes = new HashSet<>();
        expectedLikes.add(user1.getId());

        assertEquals(expectedLikes, film1.getLikes());
    }

    @Test
    public void getTopFilmsNormal() {
        init();

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user1.getId());

        List<Film> expectedTopFilms = new ArrayList<>();
        expectedTopFilms.add(film1);
        expectedTopFilms.add(film2);

        List<Film> savedTopFilms = filmService.getTopFilms(2);

        assertEquals(expectedTopFilms, savedTopFilms);
        assertEquals(2, savedTopFilms.size());
    }

    @Test
    public void getTopFilmsEmpty() {
        List<Film> savedTopFilms = filmService.getTopFilms(10);

        assertEquals(0, savedTopFilms.size());
    }
}
