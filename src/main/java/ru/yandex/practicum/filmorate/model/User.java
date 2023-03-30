package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @Email
    @NotNull
    private final String email;
    @NotBlank
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
    private final Set<Integer> friends;

    public boolean addFriend(Integer id) {
        return friends.add(id);
    }

    public boolean deleteFriend(Integer id) {
        return friends.remove(id);
    }
}
