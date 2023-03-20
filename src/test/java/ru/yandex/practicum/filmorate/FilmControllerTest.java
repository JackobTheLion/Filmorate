package ru.yandex.practicum.filmorate;

import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.controller.FilmController.MIN_RELEASE_DATE;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .build();
    }

    @Test
    public void addFilmNormal() {
        filmController.addFilm(film);
        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film);

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
    public void addFilmIdAreadyExests() {
        filmController.addFilm(film);
        Film film1 = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(film.getId())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });

        String expectedMessage = "Film with id 1 already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void putFilmNormal() {
        filmController.addFilm(film);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(film.getId())
                .build();

        filmController.putFilm(updatedFilm);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(updatedFilm, films.get(0));
    }

    @Test
    public void putFilmReleaseBeforeMinDate() {
        filmController.addFilm(film);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(MIN_RELEASE_DATE.minusDays(1))
                .duration(133)
                .id(film.getId())
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film release date cannot be earlier than min release date " + MIN_RELEASE_DATE;
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void putFilmWrongId() {
        filmController.addFilm(film);
        Film updatedFilm = Film.builder()
                .name("Крепкий орешек")
                .description("Крутой боевик с Брюсом Уиллисом")
                .releaseDate(LocalDate.of(1988, 07, 22))
                .duration(133)
                .id(film.getId() + 100)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film with id " + updatedFilm.getId() + " does not exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void getFilms() {
        filmController.addFilm(film);
        List<Film> expectedFilms  = new ArrayList<>();
        expectedFilms.add(film);
        List<Film> savedFilms = filmController.getFilms();
        assertEquals(expectedFilms, savedFilms);
    }

    @Test
    public void getFilmsWhenEmpty() {
        List<Film> savedFilms = filmController.getFilms();
        assertTrue(savedFilms.isEmpty());
    }
}