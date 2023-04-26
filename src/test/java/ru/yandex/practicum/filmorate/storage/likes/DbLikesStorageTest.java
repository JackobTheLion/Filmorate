package ru.yandex.practicum.filmorate.storage.likes;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbLikesStorageTest {

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "filmorate_users");
        jdbcTemplate.update("ALTER TABLE filmorate_users ALTER COLUMN user_id RESTART WITH 1");
    }

    public void initFilmsAndUsers() {
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
        likesStorage.addLike(film1.getId(), user1.getId());
        likesStorage.addLike(film2.getId(), user1.getId());
        likesStorage.addLike(film2.getId(), user2.getId());
        // Film1 1 like from user1
        // Film2 2 likes from user1 and user2
    }

    @Test
    public void addLikeNormal() {
        initFilmsAndUsers();
        likesStorage.addLike(film1.getId(), user1.getId());

        List<Film> popularFilms = filmStorage.getPopularFilms(1);
        assertTrue(popularFilms.size() <= 1);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());
    }

    @Test
    public void addLikeWrongFilmId() {
        initFilmsAndUsers();
        Long wrongFilmId = film1.getId() + 9999;

        Throwable exception = assertThrows(NotFoundException.class,
                () -> likesStorage.addLike(wrongFilmId, user1.getId()));
        assertEquals(String.format("User with id %s or film with id %s not found", user1.getId(), wrongFilmId)
                , exception.getMessage());
    }

    @Test
    public void addLikeWrongUserId() {
        initFilmsAndUsers();
        Long wrongUserId = user1.getId() + 9999;

        Throwable exception = assertThrows(NotFoundException.class,
                () -> likesStorage.addLike(film1.getId(), wrongUserId));
        assertEquals(String.format("User with id %s or film with id %s not found", wrongUserId, film1.getId())
                , exception.getMessage());
    }

    @Test
    public void removeLikeNormal() {
        initFilmsAndUsers();
        initLikes();

        likesStorage.removeLike(film2.getId(), user1.getId());
        likesStorage.removeLike(film2.getId(), user2.getId());

        List<Film> popularFilms = filmStorage.getPopularFilms(1);
        assertTrue(popularFilms.size() <= 1);
        assertThat(popularFilms.get(0)).hasFieldOrPropertyWithValue("name", film1.getName())
                .hasFieldOrPropertyWithValue("description", film1.getDescription())
                .hasFieldOrPropertyWithValue("duration", film1.getDuration())
                .hasFieldOrPropertyWithValue("releaseDate", film1.getReleaseDate());
    }

    @Test
    public void removeLikeWrongUserId() {
        initFilmsAndUsers();
        initLikes();
        Long wrongUserId = user1.getId() + 999999;

        Throwable exception = assertThrows(NotFoundException.class,
                () -> likesStorage.removeLike(film1.getId(), wrongUserId));
        assertEquals(String.format("User with id %s or film with id %s not found", wrongUserId, film1.getId())
                , exception.getMessage());
    }

    @Test
    public void removeLikeWrongFilmId() {
        initFilmsAndUsers();
        initLikes();
        Long wrongFilmId = film1.getId() + 999999;

        Throwable exception = assertThrows(NotFoundException.class,
                () -> likesStorage.removeLike(wrongFilmId, user1.getId()));
        assertEquals(String.format("User with id %s or film with id %s not found", user1.getId(), wrongFilmId)
                , exception.getMessage());
    }
}
