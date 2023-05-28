package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Event {
    private final Long timestamp;
    private final Long userId;
    private final EventType eventType;
    private final Operation operation;
    private Long eventId;
    private final Long entityId;
}
