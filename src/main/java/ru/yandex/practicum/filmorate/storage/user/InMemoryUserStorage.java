package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private int id = 0;
    @Override
    public User addUser(User user) {
        if (userEmails.contains(user.getEmail())) {
            log.error("User with such email already exists");
            throw new ValidationException("User with such email already exists");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());

        log.info("User added: {}", user);

        return user;
    }

    @Override
    public User putUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("No user with id {}", user.getId());
            throw new ValidationException("No user with id " + user.getId());
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
        return new ArrayList<>(users.values());
    }
}
