package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.controller.FilmController.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.controller.FilmController.MIN_RELEASE_DATE;

public class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController();
        film = new Film("Крепкий орешек", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22), 133);
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
    public void addFilmBlankName() {
        Film film1 = new Film(" ", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22), 133);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });
        String expectedMessage = "Film name cannot be empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size());
    }

    @Test
    public void addFilmEmptyName() {
        Film film1 = new Film("", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22), 133);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });
        String expectedMessage = "Film name cannot be empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size());
    }

    @Test
    public void addFilmTooLonDescription() {
        String description = "a".repeat(MAX_DESCRIPTION_LENGTH + 1);
        Film film1 = new Film("Крепкий Орешек", description,
                LocalDate.of(1988, 07, 22), 133);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });

        String expectedMessage = "Film description length exceeds max length ("
                + MAX_DESCRIPTION_LENGTH + ")";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size());
    }

    @Test
    public void addFilmReleaseBeforeMinDate() {
        Film film1 = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                MIN_RELEASE_DATE.minusDays(1), 133);
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
    public void addFilmFilmDurationNegative() {
        Film film1 = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22),  -1);
        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film1);
        });

        String expectedMessage = "Film duration should be more than 0";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size());
    }

    @Test
    public void addFilmIdAreadyExests() {
        filmController.addFilm(film);
        Film film1 = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22),  133);
        film1.setId(film.getId());

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
        Film updatedFilm = new Film("Крестный отец", "фильм про мафию",
                LocalDate.of(1977, 3, 14),  175);
        updatedFilm.setId(film.getId());

        filmController.putFilm(updatedFilm);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(updatedFilm, films.get(0));
    }

    @Test
    public void putFilmBlankName() {
        filmController.addFilm(film);
        Film updatedFilm = new Film(" ", "фильм про мафию",
                LocalDate.of(1977, 3, 14),  175);
        updatedFilm.setId(film.getId());

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film name cannot be empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }
    @Test
    public void putFilmEmptyName() {
        filmController.addFilm(film);
        Film updatedFilm = new Film("", "фильм про мафию",
                LocalDate.of(1977, 3, 14),  175);
        updatedFilm.setId(film.getId());

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film name cannot be empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void putFilmTooLonDescription() {
        filmController.addFilm(film);
        String description = "a".repeat(MAX_DESCRIPTION_LENGTH + 1);
        Film updatedFilm = new Film("Крепкий Орешек", description,
                LocalDate.of(1988, 07, 22), 133);
        updatedFilm.setId(film.getId());

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film description length exceeds max length ("
                + MAX_DESCRIPTION_LENGTH + ")";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void putFilmReleaseBeforeMinDate() {
        filmController.addFilm(film);
        Film updatedFilm = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                MIN_RELEASE_DATE.minusDays(1), 133);
        updatedFilm.setId(film.getId());

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
    public void putFilmFilmDurationNegative() {
        filmController.addFilm(film);
        Film updatedFilm = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22),  -1);
        updatedFilm.setId(film.getId());

        Exception exception = assertThrows(ValidationException.class, () -> {
            filmController.putFilm(updatedFilm);
        });

        String expectedMessage = "Film duration should be more than 0";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<Film> films = filmController.getFilms();
        assertEquals(1, films.size());
        assertEquals(film, films.get(0));
    }

    @Test
    public void putFilmWrongId() {
        filmController.addFilm(film);
        Film updatedFilm = new Film("Крепкий Орешек", "Крутой боевик с Брюсом Уиллисом",
                LocalDate.of(1988, 07, 22),  133);
        updatedFilm.setId(film.getId() + 100);

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