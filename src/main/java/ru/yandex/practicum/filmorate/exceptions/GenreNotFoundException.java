package ru.yandex.practicum.filmorate.exceptions;

public class GenreNotFoundException extends NotFoundException {
    public GenreNotFoundException() {
    }

    public GenreNotFoundException(String message) {
        super(message);
    }

    public GenreNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreNotFoundException(Throwable cause) {
        super(cause);
    }
}
