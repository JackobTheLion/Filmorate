package ru.yandex.practicum.filmorate.exceptions;

public class IncorrectParameterException extends NotFoundException {
    public IncorrectParameterException(String message) {
        super(message);
    }
}
