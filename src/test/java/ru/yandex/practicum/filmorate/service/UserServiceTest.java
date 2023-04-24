package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.DbFriendsStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    UserStorage userStorage;
    UserService userService;
    Film film1;
    Film film2;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    public void beforeEach() {
        userStorage = new InMemoryUserStorage();
        FriendsStorage friendsStorage = new DbFriendsStorage(new JdbcTemplate());
        userService = new UserService(userStorage, friendsStorage);

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

        user3 = User.builder()
                .email("pochta@gmail.ru")
                .login("loooooogin")
                .birthday(LocalDate.of(2000, 12, 26))
                .name("The Name")
                .build();
    }

    public void init() {
        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
    }

    @Test
    public void addFriendNormal() {
        init();
        userService.addFriend(user1.getId(), user2.getId());

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
            userService.addFriend(wrongUserId, user2.getId());
        });

        String expectedMessage1 = String.format("User with id %s not found", wrongUserId);
        String actualMessage1 = exception1.getMessage();
        assertEquals(expectedMessage1, actualMessage1);

        Long wrongFriendId = user2.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.addFriend(user1.getId(), wrongFriendId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongFriendId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);
    }

    @Test
    public void deleteFriendNormal() {
        init();
        userService.addFriend(user1.getId(), user2.getId());

        userService.deleteFriend(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().isEmpty());
        assertTrue(user2.getFriends().isEmpty());

    }

    @Test
    public void deleteFriendWrongId() {
        init();
        userService.addFriend(user1.getId(), user2.getId());
        Long wrongUserId = user1.getId() + 1000;

        Exception exception1 = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteFriend(wrongUserId, user2.getId());
        });

        String expectedMessage1 = String.format("User with id %s not found", wrongUserId);
        String actualMessage1 = exception1.getMessage();
        assertEquals(expectedMessage1, actualMessage1);

        Long wrongFriendId = user2.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.addFriend(user1.getId(), wrongFriendId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongFriendId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);
    }

    @Test
    public void getFriendsNormal() {
        init();
        userService.addFriend(user1.getId(), user2.getId());

        List<User> expectedUser1Friends = new ArrayList<>();
        expectedUser1Friends.add(user2);
        List<User> savedUser1Friends = userService.getFriends(user1.getId());

        assertEquals(expectedUser1Friends, savedUser1Friends);

        List<User> expectedUser2Friends = new ArrayList<>();
        expectedUser2Friends.add(user1);
        List<User> savedUser2Friends = userService.getFriends(user2.getId());

        assertEquals(expectedUser2Friends, savedUser2Friends);
    }

    @Test
    public void getFriendsWrongId() {
        init();
        userService.addFriend(user1.getId(), user2.getId());

        Long wrongId = user1.getId() + 1000;

        Exception exception2 = assertThrows(UserNotFoundException.class, () -> {
            userService.getFriends(wrongId);
        });

        String expectedMessage2 = String.format("User with id %s not found", wrongId);
        String actualMessage2 = exception2.getMessage();
        assertEquals(expectedMessage2, actualMessage2);
    }

    @Test
    public void getCommonFriendsNormal() {
        init();

        userService.addFriend(user1.getId(), user2.getId());
        userService.addFriend(user1.getId(), user3.getId());

        userService.addFriend(user2.getId(), user3.getId());

        List<User> expectedCommonFriends = new ArrayList<>();
        expectedCommonFriends.add(user3);

        List<User> savedCommonFriend = userService.getCommonFriends(user1.getId(), user2.getId());
        assertEquals(expectedCommonFriends, savedCommonFriend);
    }

    @Test
    public void getCommonFriendsEmpty() {
        init();

        List<User> savedCommonFriend = userService.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(savedCommonFriend.isEmpty());
    }

}
