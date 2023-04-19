package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class User {
    private long id;
    @Email
    @NotNull
    private final String email;
    @NotBlank
    private final String login;
    private String name;
    @Past
    private final LocalDate birthdate;
    @JsonIgnore
    private final Set<Long> friends = new HashSet<>();
}
