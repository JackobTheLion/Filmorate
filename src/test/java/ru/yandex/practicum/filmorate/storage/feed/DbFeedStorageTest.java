package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFeedStorageTest {

    private final FilmService filmService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    private Film film1;
    private User user1;
    private User user2;

    public void cleanDatabase() {
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
        filmService.addFilm(film1);
    }

    public void initUser() {
        user1 = User.builder()
                .email("user1@ya.ru")
                .login("user1")
                .name("user1")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userService.addUser(user1);

        user2 = User.builder()
                .email("user2@ya.ru")
                .login("user2")
                .name("user2")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userService.addUser(user2);
    }

    @Test
    public void addEventAndGetFeedNormal() {
        initFilms();
        initUser();

        Event expectedEvent = Event.builder()
                .userId(user1.getId())
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .entityId(user2.getId())
                .build();

        userService.addFriend(user1.getId(), user2.getId());

        List<Event> savedEvents = userService.getFeedForUser(user1.getId());
        assertThat(savedEvents.get(0)).hasFieldOrPropertyWithValue("userId", expectedEvent.getUserId())
                .hasFieldOrPropertyWithValue("eventType", expectedEvent.getEventType())
                .hasFieldOrPropertyWithValue("operation", expectedEvent.getOperation())
                .hasFieldOrPropertyWithValue("entityId", expectedEvent.getEntityId());
    }

    @Test
    public void getFeedForUserWrongUserId() {
        Long wrongId = 99999L;
        Throwable exception = assertThrows(UserNotFoundException.class, () -> userService.getFeedForUser(wrongId));
        assertThat(exception.getMessage().equals(String.format("User with id %s not found", wrongId)));
    }
}