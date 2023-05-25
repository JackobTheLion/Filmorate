package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorDaoStorage {

    List<Director> getAll();

    Director createDirector(Director director);

    void deleteDirector(Long id);

    Director updateDirector(Director director);

    Director getDirector(Long id);

    List<Long> findFilmsByDirector(Long directorId, String sortBy);

}