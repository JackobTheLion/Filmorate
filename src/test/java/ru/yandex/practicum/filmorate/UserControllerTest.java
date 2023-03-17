package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = new User("email@email.ru", "login",
                LocalDate.of(1990, 12, 26));
        user.setName("userName");
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
    public void addUserLoginWithSpace() {
        User user1 = new User("email@email.ru", "Lo gin",
                LocalDate.of(1990, 12, 26));
        user1.setName("userName");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user1);
        });
        String expectedMessage = "Login must not contain spaces";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> savedUsers = userController.getUsers();
        assertEquals(0, savedUsers.size());
    }

    @Test
    public void addUserEmptyLogin() {
        User user1 = new User("email@email.ru", " ",
                LocalDate.of(1990, 12, 26));
        user1.setName("userName");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user1);
        });
        String expectedMessage = "Login cannot be empty or blank";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> savedUsers = userController.getUsers();
        assertEquals(0, savedUsers.size());
    }

    @Test
    public void addUserBlankLogin() {
        User user1 = new User("email@email.ru", "",
                LocalDate.of(1990, 12, 26));
        user1.setName("userName");

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(user1);
        });
        String expectedMessage = "Login cannot be empty or blank";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> savedUsers = userController.getUsers();
        assertEquals(0, savedUsers.size());
    }

    @Test
    public void addUserEmailExist() {
        userController.addUser(user);

        User user1 = new User(user.getEmail(), "Login",
                LocalDate.of(1990, 12, 26));
        user1.setName("userName");

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
        User user1 = new User("email@email.ru", "Login",
                LocalDate.of(1990, 12, 26));
        user1.setName(null);

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithEmptyName() {
        User user1 = new User("email@email.ru", "Login",
                LocalDate.of(1990, 12, 26));
        user1.setName("");

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithBlankName() {
        User user1 = new User("email@email.ru", "Login",
                LocalDate.of(1990, 12, 26));
        user1.setName(" ");

        userController.addUser(user1);
        assertEquals(user1.getName(), user1.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void putUserNormal() {
        userController.addUser(user);

        User updatedUser = new User("email@yandex.ru", "OtherLogin",
                LocalDate.of(2000, 12, 26));
        updatedUser.setName("Other Name");
        updatedUser.setId(user.getId());

        userController.putUser(updatedUser);

        List<User> users = userController.getUsers();
        assertEquals(1, users.size());
        assertEquals(updatedUser, users.get(0));
    }

    @Test
    public void putUserWrongId() {
        userController.addUser(user);

        User updatedUser = new User("email@yandex.ru", "OtherLogin",
                LocalDate.of(2000, 12, 26));
        updatedUser.setName("Other Name");
        updatedUser.setId(user.getId() + 100);

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
        User updatedUser = new User("email@yandex.ru", "OtherLogin",
                LocalDate.of(2000, 12, 26));
        updatedUser.setId(user.getId());
        updatedUser.setName(null);

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
        assertEquals(updatedUser, savedUsers.get(0));
    }

    @Test
    public void putUserWithEmptyName() {
        userController.addUser(user);
        User updatedUser = new User("email@yandex.ru", "OtherLogin",
                LocalDate.of(2000, 12, 26));
        updatedUser.setId(user.getId());
        updatedUser.setName("");

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void putUserWithBlankName() {
        userController.addUser(user);
        User updatedUser = new User("email@yandex.ru", "OtherLogin",
                LocalDate.of(2000, 12, 26));
        updatedUser.setId(user.getId());
        updatedUser.setName(" ");

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
