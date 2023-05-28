package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class Film {

    private long id;
    @NotBlank
    private final String name;
    @Size(min = 1, max = 200)
    private final String description;
    @NotNull
    @ReleaseDate
    private final LocalDate releaseDate;
    @Min(1)
    private final long duration;
    @NotNull
    private Mpa mpa;
    private List<Genre> genres;
    private List<Long> likes;
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
