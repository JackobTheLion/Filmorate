package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Likes;

import java.util.List;

public interface LikesStorage {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Likes> getLikes(Long filmId);

    List<Likes> getAllLikes();
}