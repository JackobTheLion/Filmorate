package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film putFilm(Film film);

    List<Film> getFilms();

    Film findFilm(Long id);

    List<Film> getPopularFilms(Integer limit);

    List<Film> getCommonFilms(Long userId, Long friendId);

    void deleteFilm(Long id);

}
