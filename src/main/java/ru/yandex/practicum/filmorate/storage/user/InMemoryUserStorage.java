package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("inMemoryStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private long id = 0;

    @Override
    public User addUser(User user) {
        log.info("Trying to add user: {}", user);
        users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(u -> {
            log.error("User with such email already exists");
            throw new ValidationException("User with such email already exists");
        });

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("User added: {}", user);
        return user;
    }

    @Override
    public User putUser(User user) {
        log.info("Trying to update user: {}", user);
        if (!users.containsKey(user.getId())) {
            log.error("No user with id {}", user.getId());
            throw new UserNotFoundException("No user with id " + user.getId());
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User {} was updated {}", user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        log.info("Number of users registered: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUser(Long id) {
        log.info("Looking for user with id: {}", id);
        User userFound = users.get(id);
        if (userFound == null) {
            log.info("User with id {} not found", id);
            throw new UserNotFoundException(String.format("User with id %s not found", id));
        }
        log.info("User found: {}", userFound);
        return userFound;
    }
}
