package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Autowired UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        log.info("Making friends id {} and {}", userId, friendId);
        User user = userStorage.findUser(userId);
        user.getFriends().add(friendId);
        userStorage.findUser(friendId).getFriends().add(userId);
        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        log.info("Deleting friends id {} and {}", userId, friendId);
        User user = userStorage.findUser(userId);
        user.getFriends().remove(friendId);
        userStorage.findUser(friendId).getFriends().remove(userId);
        return user;
    }

    public List<User> getFriends(Long id) {
        log.info("Looking for friend of Id: {}", id);
        User user = userStorage.findUser(id);
        return userStorage.getUsers().stream()
                .filter(p -> user.getFriends().contains(p.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Looking for common friends for {}  and {}", id, otherId);
        Set<Long> commonFriendsId = new HashSet<>(userStorage.findUser(id).getFriends());
        commonFriendsId.retainAll(userStorage.findUser(otherId).getFriends());

        return userStorage.getUsers().stream()
                .filter(p -> commonFriendsId.contains(p.getId()))
                .collect(Collectors.toList());
    }
}
