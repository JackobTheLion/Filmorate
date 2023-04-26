package ru.yandex.practicum.filmorate.exceptions;

public class MpaNotFoundException extends NotFoundException {
    public MpaNotFoundException() {
    }

    public MpaNotFoundException(String message) {
        super(message);
    }

    public MpaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MpaNotFoundException(Throwable cause) {
        super(cause);
    }
}
