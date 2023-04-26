package ru.yandex.practicum.filmorate.storage.rating;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbRatingStorageTest {

    private final DbRatingStorage ratingStorage;
    private final JdbcTemplate jdbcTemplate;
    private Film film;


    @BeforeEach
    public void beforeEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "films");
        jdbcTemplate.update("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
    }

    public void initFilms() {
        film = Film.builder()
                .name("Film name")
                .description("Film description")
                .duration(120)
                .releaseDate(LocalDate.of(2019, 10, 1))
                .mpa(Mpa.builder().id(1).build())
                .build();
    }

    @Test
    public void getRatingNormal() {
        Mpa mpa = ratingStorage.findRating(1L);
        assertEquals(Mpa.builder().id(1L).name("G").build(), mpa);
    }

    @Test
    public void getRatingWrongId() {
        Long wrongId = 9999999L;
        Throwable exception = assertThrows(RatingNotFoundException.class, () -> ratingStorage.findRating(wrongId));
        assertEquals(exception.getMessage(), String.format("Rating with id %s not found", wrongId));
    }

    @Test
    public void getAllRatingsNormal() {
        List<Mpa> expectedRatings = new ArrayList<>();
        expectedRatings.add(new Mpa(1, "G"));
        expectedRatings.add(new Mpa(2, "PG"));
        expectedRatings.add(new Mpa(3, "PG-13"));
        expectedRatings.add(new Mpa(4, "R"));
        expectedRatings.add(new Mpa(5, "NC-17"));

        List<Mpa> ratings = ratingStorage.getAllRatings();

        assertEquals(expectedRatings, ratings);
    }
}
