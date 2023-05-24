package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class Review {
    private Long reviewId;
    @NotNull
    private final String content;
    @NotNull
    private final Boolean isPositive;
    @NotNull
    private final Long userId;
    @NotNull
    private final Long filmId;
    private int useful;

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("reviewid", reviewId);
        values.put("content", content);
        values.put("isPositive", isPositive);
        values.put("userId", userId);
        values.put("filmId", filmId);
        values.put("useful", useful);
        return values;
    }
}
