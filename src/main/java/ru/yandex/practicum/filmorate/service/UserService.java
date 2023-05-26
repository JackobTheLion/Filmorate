package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Likes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    private final FriendsStorage friendsStorage;
    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public UserService(@Qualifier("dbStorage") UserStorage userStorage,
                       @Qualifier("dbStorage") FriendsStorage friendsStorage,
                       @Qualifier("dbStorage") LikesStorage likesStorage,
                       @Qualifier("dbStorage") FilmStorage filmStorage,
                       @Qualifier("dbStorage") GenreStorage genreStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.likesStorage = likesStorage;
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
    }

    public User addUser(User user) {
        log.info("Trying to add user: {}", user);
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("User name empty. Set login {} as name", user.getLogin());
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User putUser(User user) {
        log.info("Trying to update user: {}", user);
        return userStorage.putUser(user);
    }

    public List<User> getUsers() {
        List<User> users = userStorage.getUsers();
        log.info("Number of users registered: {}", users.size());
        return users;
    }

    public User findUser(Long userId) {
        log.info("Looking for user: {}", userId);
        return userStorage.findUser(userId);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Making friends id {} and {}", userId, friendId);
        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId == friendId) {
            log.error("UserID and FriendID should be different");
            throw new ValidationException("UserID and FriendID should be different");
        }
        log.info("Deleting friends id {} and {}", userId, friendId);
        friendsStorage.removeFriend(userId, friendId);
    }

    public List<Film> recomendFilms(long userId) {
        List<User> users = getUsers();
        User currentUser = users.stream()
                .filter(u -> u.getId() == userId)
                .findFirst().get();

        users.remove(currentUser);
        List<Likes> likes = likesStorage.getAllLikes();

        List<Long> currentUserLikes = likes.stream()
                .filter(l -> l.getUserId() == userId)
                .map(l -> l.getFilmId())
                .collect(Collectors.toList());

        int count = 0;//max возможное пересечение
        List<Long> otherUserLikes = new ArrayList<>();
        for (User user : users) {
            List<Long> userLikes = likes.stream()//получаем список фильмов кот like user из списка
                    .filter(l -> l.getUserId() == user.getId())
                    .map(l -> l.getFilmId())
                    .collect(Collectors.toList());
            int currentCount = (int) currentUserLikes.stream() // текущий счетчик пересечения
                    .filter(userLikes::contains)
                    .count();
            if (currentCount > count) {
                count = currentCount;
                otherUserLikes = userLikes;
            }
        }
        List<Long> otherUserLikesNotMatch = otherUserLikes.stream()
                .filter(newUserFilmId -> currentUserLikes.stream()
                        .noneMatch(userFilmId -> newUserFilmId == userFilmId))
                .collect(Collectors.toList());

        var result = filmStorage.findAllFilmsByIds(otherUserLikesNotMatch);
        for (Film film : result) {
            film.setGenres(genreStorage.getFilmGenres(film.getId()));
            film.setLikes(likesStorage.getLikes(film.getId()).stream()
                    .map(f -> f.getUserId())
                    .collect(Collectors.toList()));
        }

        return result;
    }

    public List<User> getFriends(Long id) {
        findUser(id);
        log.info("Looking for friend of Id: {}", id);
        return friendsStorage.getFriends(id);
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        log.info("Looking for common friends for {}  and {}", id, otherId);
        return friendsStorage.getCommonFriends(id, otherId);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id {}", id);
        userStorage.deleteUser(id);
    }

}
