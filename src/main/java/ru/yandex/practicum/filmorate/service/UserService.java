package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Autowired UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(User user1, User user2) {
        user1.addFriend(user2.getId());
        user2.addFriend(user1.getId());
    }

    public void deleteFriend(User user1, User user2) {
        user1.deleteFriend(user2.getId());
        user2.deleteFriend(user1.getId());
    }

    public List<User> getFriends(User user) {

    }
}
