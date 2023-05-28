package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.DbGenreStorage;

import java.util.List;

@Service
@Slf4j
public class GenreService {

    private final DbGenreStorage dbGenreStorage;

    @Autowired
    public GenreService(@Qualifier("dbStorage") DbGenreStorage dbGenreStorage) {
        this.dbGenreStorage = dbGenreStorage;
    }

    public List<Genre> getAllGenres() {
        return dbGenreStorage.getAllGenres();
    }

    public Genre findGenre(Long id) {
        return dbGenreStorage.findGenre(id);
    }
}