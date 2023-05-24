package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface EventStorage {

    Event addEvent(Long userId, EventType eventType, Operation operation, Long entityId);

    List<Event> getFeedForUser(Long userId);
}
