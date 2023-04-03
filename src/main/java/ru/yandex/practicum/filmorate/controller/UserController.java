package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping
    public User addUser(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
        return userStorage.addUser(user);
    }

    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
        return userStorage.putUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userStorage.getUsers();
        log.info("Currently {} users registered.", users.size());
        return users;
    }

    @GetMapping("/{userId}")
    public User findUser(@PathVariable Long userId) {
        log.info("Looking for user: {}", userId);
        return userStorage.findUser(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Making friends id {} and {}", userId, friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Deleting friends id {} and {}", userId, friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Looking for friend of Id: {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable() Long id, @PathVariable Long otherId) {
        log.info("Looking for common friends for {}  and {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
