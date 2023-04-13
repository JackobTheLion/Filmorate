package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
<<<<<<< Updated upstream
=======
import java.util.HashSet;
import java.util.List;
import java.util.Set;
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
=======
    private final String rating;
    private final List<String> genre;
    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();
>>>>>>> Stashed changes
}
