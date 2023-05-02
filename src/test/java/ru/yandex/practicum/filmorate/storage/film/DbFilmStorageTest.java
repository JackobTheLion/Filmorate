package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.likes.DbLikesStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFilmStorageTest {

    private final DbFilmStorage filmStorage;
    private final DbUserStorage userStorage;
    private final DbLikesStorage likesStorage;
    private final JdbcTemplate jdbcTemplate;
    private Film film1;
    private Film film2;
    private User user1;
    private User user2;

    @BeforeEach
    public void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "likes");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "film_genre");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "filmorate_users");
        jdbcTemplate.update("ALTER TABLE filmorate_users ALTER COLUMN user_id RESTART WITH 1");
    }

    public void initFilms() {
        film1 = Film.builder()
                .name("Film name")
                .description("Film description")
                .duration(120)
                .releaseDate(LocalDate.of(2019, 10, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmStorage.addFilm(film1);

        film2 = Film.builder()
                .name("Other name")
                .description("Other film description")
                .duration(220)
                .releaseDate(LocalDate.of(1990, 10, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmStorage.addFilm(film2);
    }

    public void initUser() {
        user1 = User.builder()
                .email("user1@ya.ru")
                .login("user1")
                .name("user1")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user1);

        user2 = User.builder()
                .email("user2@ya.ru")
                .login("user2")
                .name("user2")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user2);
    }

    public void initLikes() {
        initFilms();
        initUser();
        likesStorage.addLike(film1.getId(), user1.getId());
        likesStorage.addLike(film2.getId(), user1.getId());
        likesStorage.addLike(film2.getId(), user2.getId());
        // Film1 1 like from user1
        // Film2 2 likes from user1 and user2
    }

    @Test
    public void addFilmNormal() {
        Film film = Film.builder()
                .name("Film name")
                .description("Film description")
                .duration(120)
                .releaseDate(LocalDate.of(2019, 10, 1))
                .mpa(Mpa.builder()
                        .id(1)
                        .build())
                .build();
        filmStorage.addFilm(film);

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilm(film.getId()));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", film.getName())
                                .hasFieldOrPropertyWithValue("description", film.getDescription())
                                .hasFieldOrPropertyWithValue("duration", film.getDuration())
                                .hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate())
                );
    }

    @Test
    public void findFilmNormal() {
        initFilms();
        Optional<Film> filmOptional = Optional.of(filmStorage.findFilm(film1.getId()));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f ->
                        assertThat(f).hasFieldOrPropertyWithValue("name", film1.getName())
                                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate())
                );
    }

    @Test
    public void findFilmWrongId() {
        initFilms();
        Long wrongId = film1.getId() + 9999;
        Throwable exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.findFilm(wrongId));
        assertThat(exception.getMessage().equals(String.format("Film wth id %s not found", wrongId)));
    }

    @Test
    public void getFilmsNormal() {
        initFilms();
        List<Film> films = filmStorage.getFilms();
        assertEquals(2, films.size());
        assertThat(films.get(0)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());

        assertThat(films.get(1)).hasFieldOrPropertyWithValue("name", film2.getName())
                .hasFieldOrPropertyWithValue("description", film2.getDescription())
                .hasFieldOrPropertyWithValue("duration", film2.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate());
    }

    @Test
    public void getFilmsEmpty() {
        List<Film> films = filmStorage.getFilms();
        assertTrue(films.isEmpty());
    }

    @Test
    public void getPopularFilmsNormal() {
        initLikes();
        List<Film> popularFilms = filmStorage.getPopularFilms(100);
        assertTrue(popularFilms.size() <= 100);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film2.getName())
                .hasFieldOrPropertyWithValue("description", film2.getDescription())
                .hasFieldOrPropertyWithValue("duration", film2.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate());

        assertThat(popularFilms.get(1)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());

        popularFilms = filmStorage.getPopularFilms(1);
        assertTrue(popularFilms.size() <= 1);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film2.getName())
                .hasFieldOrPropertyWithValue("description", film2.getDescription())
                .hasFieldOrPropertyWithValue("duration", film2.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate());
    }

    @Test
    public void getPopularFilmsEmpty() {
        assertTrue(filmStorage.getPopularFilms(999999).isEmpty());
    }

    @Test
    public void getPopularFilmsNoLikes() {
        initUser();
        initFilms();
        List<Film> popularFilms = filmStorage.getPopularFilms(100);
        assertTrue(popularFilms.size() <= 100);

        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());

        assertThat(popularFilms.get(1)).hasFieldOrPropertyWithValue("name", film2.getName())
                .hasFieldOrPropertyWithValue("description", film2.getDescription())
                .hasFieldOrPropertyWithValue("duration", film2.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film2.getReleaseDate());

        popularFilms = filmStorage.getPopularFilms(1);
        assertTrue(popularFilms.size() <= 1);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());
    }

    @Test
    public void putFilmNormal() {
        initFilms();
        Film updatedFilm = Film.builder()
                .id(film1.getId())
                .name("Updated name")
                .description("Updated description")
                .duration(999)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
        filmStorage.putFilm(updatedFilm);

        Optional<Film> filmOptional = Optional.of(filmStorage.findFilm(film1.getId()));
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
                );
    }

    @Test
    public void putFilmWrongId() {
        initFilms();
        List<Genre> updatedGenres = new ArrayList<>();
        updatedGenres.add(Genre.builder().id(1L).name("Комедия").build());
        Long wrongId = film1.getId() + 99999;
        Film updatedFilm = Film.builder()
                .id(wrongId)
                .name("Updated name")
                .description("Updated description")
                .duration(999)
                .releaseDate(LocalDate.of(2023, 1, 1))
                .mpa(Mpa.builder().id(1).build())
                .genres(updatedGenres)
                .build();

        Throwable exception = assertThrows(FilmNotFoundException.class, () -> filmStorage.putFilm(updatedFilm));
        assertEquals(exception.getMessage(), String.format("Film with id %s not found", wrongId));
    }
}
