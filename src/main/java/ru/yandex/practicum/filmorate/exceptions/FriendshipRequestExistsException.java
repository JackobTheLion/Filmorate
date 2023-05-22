package ru.yandex.practicum.filmorate.exceptions;

public class FriendshipRequestExistsException extends RuntimeException {
    public FriendshipRequestExistsException() {
    }

    public FriendshipRequestExistsException(String message) {
        super(message);
    }

    public FriendshipRequestExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendshipRequestExistsException(Throwable cause) {
        super(cause);
    }
}
