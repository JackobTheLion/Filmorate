package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DbDirectorStorageTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private DirectorController directorController;
    @Autowired
    private UserController userController;

    private Film createFilm() {
        Film film = Film.builder()
                .name("name")
                .description("new Film")
                .duration(130)
                .releaseDate(LocalDate.of(2002, 5, 20))
                .mpa(Mpa.builder().id(1L).name("G").build())
                .build();
        return film;
    }

    private User createUser() {
        User user = User.builder()
                .email("email@co")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(2000, 4, 15))
                .build();
        return user;
    }

    private Director createDirector() {

        Director director = Director.builder()
                .name("name")
                .build();
        return director;
    }

    @Test
    void createTest() {
        Director director = createDirector();
        Director director1 = directorController.create(director);
        director.setId(director1.getId());
        assertEquals(director, director1);
    }

    @Test
    void updateTest() {
        Director director = createDirector();
        Director director1 = directorController.create(director);
        director1.setName("name2");
        Director director2 = directorController.updateDirector(director1);
        assertEquals(director1, director2);
    }

    @Test
    void deleteTest() {
        Director director = createDirector();
        Director director1 = directorController.create(director);
        directorController.deleteDirector(director1.getId());
        assertEquals(0, directorController.getAll().size());
    }

    @Test
    void getDirectorTest() {
        Director director = createDirector();
        Director director1 = directorController.create(director);
        Director director2 = directorController.getDirector(director1.getId());
        assertEquals(director1, director2);
    }

    @Test
    void getAllDirectorsTest() {
        Director director = createDirector();
        Director director1 = directorController.create(director);
        List<Director> directors = directorController.getAll();
        assertEquals(director1, directors.get(0));
        Director director2 = directorController.create(director);
        directors = directorController.getAll();
        assertEquals(2, directors.size());
    }

    @Test
    void getFilmsByDirectorsTest() {
        Director director = createDirector();
        Director director2 = createDirector();
        Film film = createFilm();
        Film film2 = createFilm();
        User user1 = userController.addUser(createUser());
        User user2 = userController.addUser(createUser());

        Director director1 = directorController.create(director);
        film.setDirectors(new HashSet<>(directorController.getAll()));
        Film film1 = filmController.addFilm(film);
        filmController.addLike(film1.getId(), user1.getId());

        film2.setName("name2");
        film2.setDescription("new Film2");
        film2.setReleaseDate(LocalDate.of(2001, 5, 20));
        director2.setName("name2");
        director2 = directorController.create(director2);
        film2.setDirectors(new HashSet<>(directorController.getAll()));
        film2 = filmController.addFilm(film2);
        filmController.addLike(film2.getId(), user1.getId());
        filmController.addLike(film2.getId(), user2.getId());

        List<Film> filmsByDirector = filmController.findFilmsByDirector(director1.getId(), "year");
        assertEquals(2, filmsByDirector.size());
        assertEquals(filmController.findFilm(film2.getId()), filmsByDirector.get(0));

        filmsByDirector = filmController.findFilmsByDirector(director2.getId(), "year");
        assertEquals(1, filmsByDirector.size());
        assertEquals(filmController.findFilm(film2.getId()), filmsByDirector.get(0));

        filmsByDirector = filmController.findFilmsByDirector(director1.getId(), "likes");
        assertEquals(2, filmsByDirector.size());
        assertEquals(filmController.findFilm(film1.getId()), filmsByDirector.get(0));
    }
}
