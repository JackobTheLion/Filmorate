package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface FilmorateService<T> {
    List<T> getAll();
    T getById(Long id);
}
