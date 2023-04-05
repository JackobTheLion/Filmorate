package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    UserController userController;
    UserStorage userStorage;
    UserService userService;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        user1 = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("userName")
                .build();

        user2 = User.builder()
                .email("other@email.ru")
                .login("other")
                .birthday(LocalDate.of(1980, 12, 26))
                .name("otherName")
                .build();
    }

    public void init() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
    }

    @Test
    public void addUserNormal() {
        init();
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        List<User> savedUsers = userController.getUsers();
        assertEquals(expectedUsers, savedUsers);
    }

    @Test
    public void addUserEmailExist() {
        init();

        User otherUser = User.builder()
                .email(user1.getEmail())
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("userName")
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.addUser(otherUser);
        });
        String expectedMessage = "User with such email already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> savedUsers = userController.getUsers();
        assertEquals(2, savedUsers.size());
    }

    @Test
    public void addUserWithNullName() {
        User otherUser = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name(null)
                .build();

        userController.addUser(otherUser);
        assertEquals(otherUser.getName(), otherUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithEmptyName() {
        User otherUser = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name("")
                .build();

        userController.addUser(otherUser);
        assertEquals(otherUser.getName(), otherUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void addUserWithBlankName() {
        User otherUser = User.builder()
                .email("email@email.ru")
                .login("login")
                .birthday(LocalDate.of(1990, 12, 26))
                .name(" ")
                .build();

        userController.addUser(otherUser);
        assertEquals(otherUser.getName(), otherUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(1, savedUsers.size());
    }

    @Test
    public void putUserNormal() {
        init();

        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("otherUserName")
                .id(user1.getId())
                .build();

        userController.putUser(updatedUser);

        List<User> users = userController.getUsers();
        assertEquals(2, users.size());
        assertEquals(updatedUser, users.get(0));
    }

    @Test
    public void putUserWrongId() {
        init();

        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("otherUserName")
                .id(user1.getId() + 100)
                .build();

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userController.putUser(updatedUser);
        });

        String expectedMessage = "No user with id " + updatedUser.getId();
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);

        List<User> users = userController.getUsers();
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
    }

    @Test
    public void putUserWithNullName() {
        init();
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name(null)
                .id(user1.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(2, savedUsers.size());
        assertEquals(updatedUser, savedUsers.get(0));
    }

    @Test
    public void putUserWithEmptyName() {
        init();
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("")
                .id(user1.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(2, savedUsers.size());
    }

    @Test
    public void putUserWithBlankName() {
        init();
        User updatedUser = User.builder()
                .email("email@yandex.ru")
                .login("otherLogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name(" ")
                .id(user1.getId())
                .build();

        userController.putUser(updatedUser);
        assertEquals(updatedUser.getName(), updatedUser.getLogin());

        List<User> savedUsers = userController.getUsers();
        assertEquals(2, savedUsers.size());
        assertEquals(updatedUser, savedUsers.get(0));
    }

    @Test
    public void getUsersNormal() {
        init();
        List<User> expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);

        List<User> savedUsers = userController.getUsers();
        assertEquals(expectedUsers, savedUsers);
    }

    @Test
    public void getUsersWhenEmpty() {
        List<User> savedUsers = userController.getUsers();
        assertTrue(savedUsers.isEmpty());
    }

    @Test
    public void findUserNormal() {
        init();
        assertEquals(user1, userController.findUser(user1.getId()));
    }

    @Test
    public void findUserWrongId() {
        init();
        Long wrongId = user1.getId() + 1000;

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userController.findUser(wrongId);
        });

        String expectedMessage = String.format("User with id %s not found", wrongId);
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void addFriendNormal() {
        init();
        userController.addFriend(user1.getId(), user2.getId());

        Set<Long> user1ExpectedFriends = new HashSet<>();
        user1ExpectedFriends.add(user2.getId());

        Set<Long> user2ExpectedFriends = new HashSet<>();
        user2ExpectedFriends.add(user1.getId());

        assertEquals(user1ExpectedFriends, user1.getFriends());
        assertEquals(user2ExpectedFriends, user2.getFriends());
    }

    @Test
    public void addFriendWrongId() {
        init();
        Long wrongUserId = user1.getId() + 1000;

        Exception exception1 = assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(wrongUserId, user2.getId());
        });

        String expectedMessage1 = String.format("User with id %s not found", wrongUserId);
        String actualMessage1 = exception1.getMessage();
        assertEquals(expectedMessage1, actualMessage1);

        Long wrongFriendId = user2.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(user1.getId(), wrongFriendId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongFriendId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);

        Exception exception3 = assertThrows(ValidationException.class, () -> {
            userController.addFriend(user1.getId(), user1.getId());
        });

        String expectedMessage3 = "UserID and FriendID should be different";
        String actualMessage3 = exception3.getMessage();
        assertEquals(expectedMessage3, actualMessage3);
    }

    @Test
    public void deleteFriendNormal() {
        init();
        userController.addFriend(user1.getId(), user2.getId());

        userController.deleteFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().isEmpty());
        assertTrue(user2.getFriends().isEmpty());
    }

    @Test
    public void deleteFriendWrongId() {
        init();
        userController.addFriend(user1.getId(), user2.getId());
        Long wrongUserId = user1.getId() + 1000;

        Exception exception1 = assertThrows(UserNotFoundException.class, () -> {
            userController.deleteFriend(wrongUserId, user2.getId());
        });

        String expectedMessage1 = String.format("User with id %s not found", wrongUserId);
        String actualMessage1 = exception1.getMessage();
        assertEquals(expectedMessage1, actualMessage1);

        Long wrongFriendId = user2.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userController.addFriend(user1.getId(), wrongFriendId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongFriendId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);

        Exception exception3 = assertThrows(ValidationException.class, () -> {
            userController.addFriend(user1.getId(), user1.getId());
        });

        String expectedMessage3 = "UserID and FriendID should be different";
        String actualMessage3 = exception3.getMessage();
        assertEquals(expectedMessage3, actualMessage3);
    }

    @Test
    public void getFriendsNormal() {
        init();
        userController.addFriend(user1.getId(), user2.getId());

        List<User> expectedUser1Friends = new ArrayList<>();
        expectedUser1Friends.add(user2);
        List<User> savedUser1Friends = userController.getFriends(user1.getId());

        assertEquals(expectedUser1Friends, savedUser1Friends);

        List<User> expectedUser2Friends = new ArrayList<>();
        expectedUser2Friends.add(user1);
        List<User> savedUser2Friends = userController.getFriends(user2.getId());

        assertEquals(expectedUser2Friends, savedUser2Friends);
    }

    @Test
    public void getFriendsWrongId() {
        init();
        userController.addFriend(user1.getId(), user2.getId());

        Long wrongId = user1.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userController.getFriends(wrongId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);
    }

    @Test
    public void getCommonFriendsNormal() {
        init();
        User user3 = User.builder()
                .email("pochta@gmail.ru")
                .login("loooooogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("The Name")
                .build();
        userStorage.addUser(user3);

        userController.addFriend(user1.getId(), user2.getId());
        userController.addFriend(user1.getId(), user3.getId());

        userController.addFriend(user2.getId(), user3.getId());

        List<User> expectedCommonFriends = new ArrayList<>();
        expectedCommonFriends.add(user3);

        List<User> savedCommonFriend = userController.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(expectedCommonFriends, savedCommonFriend);
    }

    @Test
    public void getCommonFriendsEmpty() {
        init();

        List<User> savedCommonFriend = userController.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(savedCommonFriend.isEmpty());
    }
}
