package ru.yandex.practicum.filmorate.exceptions;

public class RatingNotFoundException extends NotFoundException {
    public RatingNotFoundException() {
    }

    public RatingNotFoundException(String message) {
        super(message);
    }

    public RatingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RatingNotFoundException(Throwable cause) {
        super(cause);
    }
}
