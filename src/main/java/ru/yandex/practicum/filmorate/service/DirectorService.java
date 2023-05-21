package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDaoStorage;

import java.util.List;

@Service
public class DirectorService implements FilmorateService<Director> {

    private final DirectorDaoStorage directorStorage;

    public DirectorService(DirectorDaoStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    @Override
    public Director getById(Long id) {
        Director director = directorStorage.getDirector(id);
        if (director == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        return director;
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public void deleteDirector(Long id) {
        if (directorStorage.getDirector(id) == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        directorStorage.deleteDirector(id);
    }

    public Director updateDirector(Director director) {
        if (directorStorage.getDirector(director.getId()) == null) {
            throw new NotFoundException("Режиссера с данным id не существует");
        }
        return directorStorage.updateDirector(director);
    }

}