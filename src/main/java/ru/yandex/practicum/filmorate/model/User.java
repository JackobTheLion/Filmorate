package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email
    @NotNull
    private final String email;
    @NotEmpty
    @NotBlank
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;

}
