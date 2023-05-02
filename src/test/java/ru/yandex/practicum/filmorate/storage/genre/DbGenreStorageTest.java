package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbGenreStorageTest {

    private final DbGenreStorage genreStorage;
    private final DbFilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "film_genre");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        film = null;
    }

    public void initFilms() {
        film = Film.builder()
                .name("Film name")
                .description("Film description")
                .duration(120)
                .releaseDate(LocalDate.of(2019, 10, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmStorage.addFilm(film);
    }

    @Test
    public void getAllGenresNormal() {
        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(new Genre(1L, "Комедия"));
        expectedGenres.add(new Genre(2L, "Драма"));
        expectedGenres.add(new Genre(3L, "Мультфильм"));
        expectedGenres.add(new Genre(4L, "Триллер"));
        expectedGenres.add(new Genre(5L, "Документальный"));
        expectedGenres.add(new Genre(6L, "Боевик"));

        List<Genre> savedGenre = genreStorage.getAllGenres();
        assertEquals(expectedGenres, savedGenre);
    }

    @Test
    public void findGenreNormal() {
        Optional<Genre> genreOptional = Optional.of(genreStorage.findGenre(1L));
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(g ->
                        assertThat(g).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Комедия")
                );
    }

    @Test
    public void findGenreWrongId() {
        Long wrongId = 999999L;
        Throwable exception = assertThrows(GenreNotFoundException.class, () -> genreStorage.findGenre(wrongId));
        assertEquals(String.format("Genre with id %s not found", wrongId), exception.getMessage());
    }

    @Test
    public void addGenreToFilmNormal() {
        initFilms();
        Genre genre = Genre.builder().id(1L).build();
        genreStorage.addGenreToFilm(film, genre);

        Genre expectedGenre = new Genre(1L, "Комедия");
        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(expectedGenre);

        List<Genre> savedGenres = genreStorage.getFilmGenres(film.getId());
        assertEquals(expectedGenres, savedGenres);
    }

    @Test
    public void addGenreToFilmWrongGenreId() {
        initFilms();
        Long wrongId = 999999L;
        Genre genre = Genre.builder().id(wrongId).build();
        Throwable exception = assertThrows(NotFoundException.class, () -> genreStorage.addGenreToFilm(film, genre));
        assertEquals(String.format("Film id %s or genre id %s not found", film.getId(), genre.getId()),
                exception.getMessage());
    }

    @Test
    public void addGenreToFilmWrongFilmId() {
        initFilms();
        Long wrongId = 999999L;
        film.setId(wrongId);
        Genre genre = Genre.builder().id(1L).build();
        Throwable exception = assertThrows(NotFoundException.class, () ->
                genreStorage.addGenreToFilm(film, genre));
        assertEquals(String.format("Film id %s or genre id %s not found", film.getId(), genre.getId()),
                exception.getMessage());
    }

    @Test
    public void addGenreDuplicate() {
        initFilms();
        Genre genre1 = Genre.builder().id(1L).build();
        Genre genre2 = Genre.builder().id(1L).build();
        genreStorage.addGenreToFilm(film, genre1);

        assertThrows(DuplicateKeyException.class, () -> genreStorage.addGenreToFilm(film, genre2));
    }
}
