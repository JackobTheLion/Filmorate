package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FriendshipRequestExistsException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.feed.EventStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.model.EventType.FRIEND;
import static ru.yandex.practicum.filmorate.model.Operation.ADD;
import static ru.yandex.practicum.filmorate.model.Operation.REMOVE;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final EventStorage eventStorage;

    @Autowired
    public UserService(@Qualifier("dbStorage") UserStorage userStorage,
                       @Qualifier("dbStorage") FriendsStorage friendsStorage,
                       @Qualifier("dbStorage") EventStorage eventStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.eventStorage = eventStorage;
    }

    public User addUser(User user) {
        log.info("Trying to add user: {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User putUser(User user) {
        log.info("Trying to update user: {}", user);
        return userStorage.putUser(user);
    }

    public List<User> getUsers() {
        List<User> users = userStorage.getUsers();
        log.info("Number of users registered: {}", users.size());
        return users;
    }

    public User findUser(Long userId) {
        log.info("Looking for user: {}", userId);
        return userStorage.findUser(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Making friends id {} and {}", userId, friendId);
        try {
            friendsStorage.addFriend(userId, friendId);
            eventStorage.addEvent(userId, FRIEND, ADD, friendId);
        } catch (FriendshipRequestExistsException e) {
            log.info(e.getMessage());
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Deleting friends id {} and {}", userId, friendId);
        friendsStorage.removeFriend(userId, friendId);
        eventStorage.addEvent(userId, FRIEND, REMOVE, friendId);
    }

    public List<User> getFriends(Long id) {
        findUser(id);
        log.info("Looking for friend of Id: {}", id);
        return friendsStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Looking for common friends for {}  and {}", id, otherId);
        return friendsStorage.getCommonFriends(id, otherId);
    }

    public List<Event> getFeedForUser(Long userId) {
        log.info("Getting feed for user id {}", userId);
        findUser(userId);
        return eventStorage.getFeedForUser(userId);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);
        userStorage.deleteUser(id);
    }
}