package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.feed.EventStorage;

import java.util.List;

@Service
@Slf4j
public class FeedService {

    private final EventStorage eventStorage;

    @Autowired
    public FeedService(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public Event addEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        log.debug("Making event: user id {}, event type {}, operation {}, entity id {}.",
                userId, eventType, operation, entityId);
        Event event = Event.builder()
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        log.info("Adding event {} to DB", event);
        return eventStorage.addEvent(event);
    }

    public List<Event> getFeedForUser(Long userId) {
        log.info("Getting feed for user id {}", userId);
        return eventStorage.getFeedForUser(userId);
    }
}
