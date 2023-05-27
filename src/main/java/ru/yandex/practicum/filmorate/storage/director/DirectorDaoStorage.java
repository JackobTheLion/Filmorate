package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Set;

public interface DirectorDaoStorage {

    List<Director> getAll();

    Director createDirector(Director director);

    void deleteDirector(Long id);

    Director updateDirector(Director director);

    Director getDirector(Long id);

    List<Long> findFilmsByDirector(Long directorId, String sortBy);

    Set<Director> getDirectorsByFilm(Long filmId);

    void setDirectorsToFilm(Set<Director> directors, Long filmId);

}