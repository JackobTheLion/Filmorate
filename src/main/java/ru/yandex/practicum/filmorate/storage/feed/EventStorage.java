package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {

    Event addEvent(Event event);

    List<Event> getFeedForUser(Long userId);
}
