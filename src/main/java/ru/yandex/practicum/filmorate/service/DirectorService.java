package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDaoStorage directorStorage;

    @Autowired
    public DirectorService(@Qualifier("dbStorage") DirectorDaoStorage directorStorage) {
        this.directorStorage = directorStorage;
    }


    public List<Director> getAll() {
        return directorStorage.getAll();
    }


    public Director getById(Long id) {
        Director director = directorStorage.getDirector(id);
        return director;
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public void deleteDirector(Long id) {
        // проверяем что директор с id существует
        directorStorage.getDirector(id);
        directorStorage.deleteDirector(id);
    }

    public Director updateDirector(Director director) {
        // проверяем что директор с id существует
        directorStorage.getDirector(director.getId());
        return directorStorage.updateDirector(director);
    }
}