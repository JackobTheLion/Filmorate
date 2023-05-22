package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class Review {
    private final Integer reviewID;
    @NotNull
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Integer userID;
    @NotNull
    private final Integer filmId;
    private int useful;
}
