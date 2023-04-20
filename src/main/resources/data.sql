INSERT INTO filmorate_users (email, login, name, birthday)
VALUES ('email@email.ru', 'login', 'name', '1990-12-26'),
    ('email@ya.ru', 'login2', 'name2', '2000-12-26'),
    ('ya@ya.ru', 'login3', 'name3', '1995-01-01'),
    ('ya1@ya.ru', 'login4', 'name4', '2003-01-01');

INSERT INTO films (name, description, release_date, duration)
VALUES ('Крепкий орешек', 'Крутой боевик с Брюсом Уиллисом', '1988-07-22', 133),
    ('Крепкий орешек 2', 'Крутой боевик с Брюсом Уиллисом. Вторая часть', '1990-07-02', 124);

INSERT INTO ratings (mpa) VALUES ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');

INSERT INTO genre (genre) VALUES ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');
