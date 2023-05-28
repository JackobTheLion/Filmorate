package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbUserStorageTest {

    private final DbUserStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    private User user;

    @BeforeEach
    public void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "filmorate_users");
        jdbcTemplate.update("ALTER TABLE filmorate_users ALTER COLUMN user_id RESTART WITH 1");
    }

    public void initUser() {
        user = User.builder()
                .email("newEmail@ya.ru")
                .login("theLogin")
                .name("userName")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user);
    }

    @Test
    public void findUserNormal() {
        initUser();
        Optional<User> userOptional = Optional.of(userStorage.findUser(user.getId()));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", user.getId())
                                .hasFieldOrPropertyWithValue("email", user.getEmail())
                                .hasFieldOrPropertyWithValue("login", user.getLogin())
                                .hasFieldOrPropertyWithValue("name", user.getName())
                                .hasFieldOrPropertyWithValue("birthday", user.getBirthday())
                );
    }

    @Test
    public void findUserWrongId() {
        Long wrongId = 99999L;
        Throwable exception = assertThrows(UserNotFoundException.class, () -> userStorage.findUser(wrongId));
        assertThat(exception.getMessage().equals(String.format("User with id %s not found", wrongId)));
    }

    @Test
    public void addUserNormal() {
        User user = User.builder()
                .email("newEmail1@ya.ru")
                .login("theLogin1")
                .name("userName")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user);

        Optional<User> userOptional = Optional.of(userStorage.findUser(user.getId()));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", user.getEmail())
                                .hasFieldOrPropertyWithValue("login", user.getLogin())
                                .hasFieldOrPropertyWithValue("name", user.getName())
                                .hasFieldOrPropertyWithValue("birthday", user.getBirthday())
                );
    }

    @Test
    public void putUserNormal() {
        initUser();
        User updatedUser = User.builder()
                .id(user.getId())
                .email("newEmail@mail.ru")
                .login("otherLogin")
                .name("updatedName")
                .birthday(user.getBirthday().plusYears(10))
                .build();

        Optional<User> userOptional = Optional.of(userStorage.putUser(updatedUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("email", updatedUser.getEmail())
                                .hasFieldOrPropertyWithValue("login", updatedUser.getLogin())
                                .hasFieldOrPropertyWithValue("name", updatedUser.getName())
                                .hasFieldOrPropertyWithValue("birthday", updatedUser.getBirthday())
                );
    }

    @Test
    public void putUserWrongId() {
        initUser();
        Long wrongId = user.getId() + 99999;
        User updatedUser = User.builder()
                .id(wrongId)
                .email("newEmail@ya.ru")
                .login("theLogin")
                .name("updated")
                .birthday(LocalDate.of(1999, 1, 1))
                .build();
        Throwable exception = assertThrows(UserNotFoundException.class, () -> userStorage.putUser(updatedUser));
        assertThat(exception.getMessage().equals(String.format("User with id %s not found", wrongId)));
    }

    @Test
    public void getUsersNormal() {
        initUser();
        List<User> users = userStorage.getUsers();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    public void getUsersEmpty() {
        List<User> users = userStorage.getUsers();
        assertTrue(users.isEmpty());
    }
    
}
