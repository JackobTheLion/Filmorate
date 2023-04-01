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

    public void addFriend(User user1, User user2) {
        log.info("Making friends id {} and {}", user1.getId(), user2.getId());
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void deleteFriend(User user1, User user2) {
        log.info("Deleting friends id {} and {}", user1.getId(), user2.getId());
        user1.deleteFriend(user2.getId());
        user2.deleteFriend(user1.getId());
    }

    public List<User> getFriends(User user) {
        log.info("Friend's ids are: {}", user.getFriends());
        return userStorage.getUsers().stream()
                .filter(p -> user.getFriends().contains(p.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(User user1, User user2) {
        log.info("Looking for common friends for {}  and {}", user1.getId(), user2.getId());
        Set<Integer> commonFriendsId = new HashSet<>(user1.getFriends());
        commonFriendsId.retainAll(user2.getFriends());

        return userStorage.getUsers().stream()
                .filter(p -> commonFriendsId.contains(p.getId()))
                .collect(Collectors.toList());
    }
}
