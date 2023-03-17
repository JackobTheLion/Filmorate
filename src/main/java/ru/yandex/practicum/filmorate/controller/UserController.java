package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final Map<Integer, User> users = new HashMap<>(); // Map <Id, User>
    private  final Set<String> userEmails = new HashSet<>();
    private int id = 0;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.warn("Login cannot be empty or blank");
            throw new ValidationException("Login cannot be empty or blank");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Login must not contain spaces");
            throw new ValidationException("Login must not contain spaces");
        }
        if (userEmails.contains(user.getEmail())) {
            log.warn("User with such email already exists");
            throw new ValidationException("User with such email already exists");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        id++;
        user.setId(id);
        users.put(user.getId(), user);
        userEmails.add(user.getEmail());
        log.info("User added: {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("No user with id {}", user.getId());
            throw new ValidationException("No user with id " + user.getId());
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("User {} was updated {}", user.getId(), user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Currently {} users registered.", users.size());
        return new ArrayList<User>(users.values());
    }

}
