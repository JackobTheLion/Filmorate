package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ReviewStorage {

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Integer id);

    Review getReview(Integer id);

    List<Review> getFilmReviews(Integer filmId, Integer count);

    void addLike(Integer id, Integer userId);

    void addDislike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    void deleteDislike(Integer id, Integer userId);
}
