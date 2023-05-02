package ru.yandex.practicum.filmorate.exceptions;

public class LikeNotFoundException extends NotFoundException {
    public LikeNotFoundException() {
    }

    public LikeNotFoundException(String message) {
        super(message);
    }

    public LikeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public LikeNotFoundException(Throwable cause) {
        super(cause);
    }
}
