package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class Event {
    private final Timestamp timestamp;
    private final Long userId;
    private final EventType eventType;
    private final Operation operation;
    private Long eventId;
    private final Long entityId;
}
