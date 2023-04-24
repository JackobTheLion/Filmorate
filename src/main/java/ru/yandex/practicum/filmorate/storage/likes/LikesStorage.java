package ru.yandex.practicum.filmorate.storage.likes;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikesStorage {

    Like addLike(Like like);

    Like deleteLike(Like like);
}
