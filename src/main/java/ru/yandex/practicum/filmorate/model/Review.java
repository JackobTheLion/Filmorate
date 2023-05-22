package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Review {
    private final int reviewID;
    private final String content;
    private final boolean isPositive;
    private final int userID;
    private final int filmId;
    private int useful;
}
