package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
public class Film {

    private int id;
    @NotBlank
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @Min(1)
    private final long duration;
}
