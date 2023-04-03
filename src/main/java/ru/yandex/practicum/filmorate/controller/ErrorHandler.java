package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({FilmNotFoundException.class,
            UserNotFoundException.class,
            IllegalArgumentException.class,
            LikeNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFoundException(final RuntimeException e) {
        return new ResponseEntity<>(
                Map.of("Error: ", e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(final RuntimeException e) {
        return new ResponseEntity<>(
                Map.of("Error: ", e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleThrowable(final Throwable e) {
        return new ResponseEntity<>(
                Map.of("Internal server error: ", e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
