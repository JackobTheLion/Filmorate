package ru.yandex.practicum.filmorate.storage.friends;

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
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbFriendsStorageTest {

    private final DbUserStorage userStorage;
    private final DbFriendsStorage friendsStorage;
    private final JdbcTemplate jdbcTemplate;
    private User user1;
    private User user2;
    private User user3;
    private User user4;

    @BeforeEach
    public void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "friends");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "filmorate_users");
        jdbcTemplate.update("ALTER TABLE filmorate_users ALTER COLUMN user_id RESTART WITH 1");
    }

    public void initUsers() {
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

        user3 = User.builder()
                .email("user3@ya.ru")
                .login("user3")
                .name("user3")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user3);

        user4 = User.builder()
                .email("user4@ya.ru")
                .login("user4")
                .name("user4")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();
        userStorage.addUser(user4);
    }

    @Test
    public void addFriendsNormal() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        List<User> user1Friends = friendsStorage.getFriends(user1.getId());
        List<User> user2Friends = friendsStorage.getFriends(user2.getId());
        assertEquals(1, user1Friends.size());
        assertThat(user1Friends.get(0))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                .hasFieldOrPropertyWithValue("name", user2.getName());
        assertTrue(user2Friends.isEmpty());
    }

    @Test
    public void addFriendsReturnRequest() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());
        List<User> user1Friends = friendsStorage.getFriends(user1.getId());
        List<User> user2Friends = friendsStorage.getFriends(user2.getId());
        assertEquals(1, user1Friends.size());
        assertThat(user1Friends.get(0))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                .hasFieldOrPropertyWithValue("name", user2.getName());
        assertEquals(1, user2Friends.size());
        assertThat(user2Friends.get(0))
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("email", user1.getEmail())
                .hasFieldOrPropertyWithValue("login", user1.getLogin())
                .hasFieldOrPropertyWithValue("name", user1.getName());
    }

    @Test
    public void addFriendsWrongUserId() {
        initUsers();
        Long wrongId = user1.getId() + 9999;
        Throwable exception1 = assertThrows(UserNotFoundException.class, () ->
                friendsStorage.addFriend(user1.getId(), wrongId));
        assertEquals(String.format("User with id %s or %s not found", user1.getId(), wrongId), exception1.getMessage());
        Throwable exception2 = assertThrows(UserNotFoundException.class, () ->
                friendsStorage.addFriend(wrongId, user1.getId()));
        assertEquals(String.format("User with id %s or %s not found", wrongId, user1.getId()), exception2.getMessage());
    }

    @Test
    public void removeFriendNormal() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());
        friendsStorage.removeFriend(user2.getId(), user1.getId());
        List<User> user1Friends = friendsStorage.getFriends(user1.getId());
        List<User> user2Friends = friendsStorage.getFriends(user2.getId());
        assertEquals(1, user1Friends.size());
        assertThat(user1Friends.get(0))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                .hasFieldOrPropertyWithValue("name", user2.getName());
        assertTrue(user2Friends.isEmpty());
    }

    @Test
    public void removeFriendNormalReverse() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());
        friendsStorage.removeFriend(user1.getId(), user2.getId());
        List<User> user1Friends = friendsStorage.getFriends(user1.getId());
        List<User> user2Friends = friendsStorage.getFriends(user2.getId());
        assertEquals(1, user2Friends.size());
        assertThat(user2Friends.get(0))
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("email", user1.getEmail())
                .hasFieldOrPropertyWithValue("login", user1.getLogin())
                .hasFieldOrPropertyWithValue("name", user1.getName());
        assertTrue(user1Friends.isEmpty());
    }

    @Test
    public void removeFriendDeleteUnconfirmed() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.removeFriend(user1.getId(), user2.getId());
        List<User> user1Friends = friendsStorage.getFriends(user1.getId());
        List<User> user2Friends = friendsStorage.getFriends(user2.getId());
        assertTrue(user1Friends.isEmpty());
        assertTrue(user2Friends.isEmpty());
    }

    @Test
    public void getCommonFriendsNormal() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());

        friendsStorage.addFriend(user2.getId(), user3.getId());
        friendsStorage.addFriend(user3.getId(), user2.getId());

        List<User> savedCommonFriends = friendsStorage.getCommonFriends(user1.getId(), user3.getId());

        assertEquals(1, savedCommonFriends.size());
        assertThat(savedCommonFriends.get(0))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                .hasFieldOrPropertyWithValue("name", user2.getName());

        List<User> savedCommonFriendsReverse = friendsStorage.getCommonFriends(user3.getId(), user1.getId());

        assertEquals(1, savedCommonFriends.size());
        assertThat(savedCommonFriends.get(0))
                .hasFieldOrPropertyWithValue("id", user2.getId())
                .hasFieldOrPropertyWithValue("email", user2.getEmail())
                .hasFieldOrPropertyWithValue("login", user2.getLogin())
                .hasFieldOrPropertyWithValue("name", user2.getName());
    }

    @Test
    public void getCommonFriendsEmpty() {
        initUsers();
        friendsStorage.addFriend(user1.getId(), user2.getId());
        friendsStorage.addFriend(user2.getId(), user1.getId());
        friendsStorage.addFriend(user4.getId(), user3.getId());
        friendsStorage.addFriend(user3.getId(), user4.getId());
        List<User> savedCommonFriends = friendsStorage.getCommonFriends(user1.getId(), user3.getId());
        assertTrue(savedCommonFriends.isEmpty());

        List<User> savedCommonFriendsReverse = friendsStorage.getCommonFriends(user3.getId(), user1.getId());
        assertTrue(savedCommonFriendsReverse.isEmpty());
    }
}
