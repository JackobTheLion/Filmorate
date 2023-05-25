package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre findGenre(Long id);

    Genre addGenreToFilm(Film film, Genre genre);

    List<Genre> getFilmGenres(Long filmId);

    void removeGenreFromFilm(Film film);
    List<Film> loadFilmsGenre(List<Film> films);

}