package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> userEmails = new HashSet<>();
    private int id = 0;

    @PostMapping
    public User addUser(@NotNull @Valid @RequestBody User user) {
        log.info("POST request received: {}", user);
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

    @PutMapping
    public User putUser(@NotNull @Valid @RequestBody User user) {
        log.info("PUT request received: {}", user);
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

    @GetMapping
    public List<User> getUsers() {
        log.info("Currently {} users registered.", users.size());
        return new ArrayList<>(users.values());
    }
}
