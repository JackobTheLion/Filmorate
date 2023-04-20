package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
@Slf4j
@Qualifier("dbStorage")
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(@Qualifier("dbStorage") RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getAllRatings() {
        return ratingStorage.getAllRatings();
    }

    public Rating findRating(Long id) {
        return ratingStorage.findRating(id);
    }
}
