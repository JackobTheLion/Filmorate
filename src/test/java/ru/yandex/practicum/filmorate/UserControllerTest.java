package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new InMemoryUserStorage());
        user = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("userName")
                .build();
    }

    @Test
    public void addUserNormal() {
        userController.addUser(user);
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user);
        List<User> savedUsers = userController.getUsers();
        assertEquals(expectedUsers, savedUsers);
    }

    @Test
    public void addUserEmailExist() {
        userController.addUser(user);

        User user1 = User.builder()
                .email(user.getEmail())
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("userName")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user1);
        });
        String expectedMessage = "User with such email already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithNullName() {
        User user1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name(null)
                .build();

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithEmptyName() {
        User user1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("")
                .build();

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithBlankName() {
        User user1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name(" ")
                .build();

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void putUserNormal() {
        userController.addUser(user);

        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("otherUserName")
                .id(user.getId())
                .build();

        userController.putUser(updatedUser);

        List<User> users = userController.getUsers();
        assertEquals(1, users.size());
        assertEquals(updatedUser, users.get(0));
    }

    @Test
    public void putUserWrongId() {
        userController.addUser(user);

        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("otherUserName")
                .id(user.getId() + 100)
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.putUser(updatedUser);;
        });

        String expectedMessage = "No user with id " + updatedUser.getId();
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> users = userController.getUsers();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    @Test
    public void putUserWithNullName() {
        userController.addUser(user);
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name(null)
                .id(user.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
        assertEquals(updatedUser, savedUsers.get(0));
    }

    @Test
    public void putUserWithEmptyName() {
        userController.addUser(user);
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("")
                .id(user.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void putUserWithBlankName() {
        userController.addUser(user);
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name(" ")
                .id(user.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
        assertEquals(updatedUser, savedUsers.get(0));
    }

    @Test
    public void getUsersNormal() {
        userController.addUser(user);
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user);

        List<User> savedUsers = userController.getUsers();
        assertEquals(expectedUsers, savedUsers);
    }

    @Test
    public void getUsersWhenEmpty() {
        List<User> savedUsers = userController.getUsers();
        assertTrue(savedUsers.isEmpty());
    }
}
