package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTest {

    private final FilmService filmService;
    private final JdbcTemplate jdbcTemplate;
    private Film film;

    @BeforeEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
    }

    public void initFilms() {
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1L).build());
        film = Film.builder()
                .name("Film name")
                .description("Film description")
                .duration(120)
                .releaseDate(LocalDate.of(2019, 10, 1))
                .mpa(Mpa.builder().id(1).build())
                .genres(genres)
                .build();
    }

    @Test
    public void addFilmNormal() {
        initFilms();
        Genre expectedGenre = Genre.builder().id(1L).name("Комедия").build();
        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(expectedGenre);
        Optional<Film> filmOptional = Optional.of(filmService.addFilm(film));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", film.getName())
                                .hasFieldOrPropertyWithValue("description", film.getDescription())
                                .hasFieldOrPropertyWithValue("duration", film.getDuration())
                                .hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate())
                                .hasFieldOrPropertyWithValue("mpa", new Mpa(1L, "G"))
                                .hasFieldOrPropertyWithValue("genres", expectedGenres)
                );
    }

    @Test
    public void addFilmWithDuplicatedGenre() {
        initFilms();
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1L).build());
        genres.add(Genre.builder().id(1L).build());
        genres.add(Genre.builder().id(2L).build());
        film.setGenres(genres);
        filmService.addFilm(film);

        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(Genre.builder().id(1L).name("Комедия").build());
        expectedGenres.add(Genre.builder().id(2L).name("Драма").build());

        Optional<Film> filmOptional = Optional.of(filmService.findFilm(film.getId()));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", film.getId())
                                .hasFieldOrPropertyWithValue("name", film.getName())
                                .hasFieldOrPropertyWithValue("description", film.getDescription())
                                .hasFieldOrPropertyWithValue("duration", film.getDuration())
                                .hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate())
                                .hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate())
                                .hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"))
                                .hasFieldOrPropertyWithValue("genres", expectedGenres)
                );
    }

    @Test
    public void putFilmWithDuplicatedGenre() {
        initFilms();
        filmService.addFilm(film);
        List<Genre> updatedGenres = new ArrayList<>();
        updatedGenres.add(Genre.builder().id(1L).build());
        updatedGenres.add(Genre.builder().id(1L).build());
        updatedGenres.add(Genre.builder().id(2L).build());
        Film updatedFilm = Film.builder()
                .id(film.getId())
                .name("Updated name")
                .description("Updated description")
                .duration(999)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .mpa(Mpa.builder().id(1).build())
                .genres(updatedGenres)
                .build();
        filmService.putFilm(updatedFilm);

        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(Genre.builder().id(1L).name("Комедия").build());
        expectedGenres.add(Genre.builder().id(2L).name("Драма").build());

        Optional<Film> filmOptional = Optional.of(filmService.findFilm(film.getId()));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("id", updatedFilm.getId())
                                .hasFieldOrPropertyWithValue("name", updatedFilm.getName())
                                .hasFieldOrPropertyWithValue("description", updatedFilm.getDescription())
                                .hasFieldOrPropertyWithValue("duration", updatedFilm.getDuration())
                                .hasFieldOrPropertyWithValue("releaseDate", updatedFilm.getReleaseDate())
                                .hasFieldOrPropertyWithValue("releaseDate", updatedFilm.getReleaseDate())
                                .hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"))
                                .hasFieldOrPropertyWithValue("genres", expectedGenres)
                );
    }
}
