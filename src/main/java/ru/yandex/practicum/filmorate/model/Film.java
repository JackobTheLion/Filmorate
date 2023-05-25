package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder=true)
@Setter
public class Film {

    private long id;
    @NotBlank
    private  String name;
    @Size(min = 1, max = 200)
    private  String description;
    @NotNull
    @ReleaseDate
    private  LocalDate releaseDate;
    @Min(1)
    private  long duration;
    @NotNull
    private Mpa mpa;
    private List<Genre> genres;
    @Builder.Default
    private Set<Director> directors = new HashSet<>();
}
