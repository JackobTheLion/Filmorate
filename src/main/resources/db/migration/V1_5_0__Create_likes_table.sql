CREATE TABLE likes
(
    film_id integer NOT NULL REFERENCES films (film_id),
    user_id integer NOT NULL REFERENCES filmorate_users (user_id),
    PRIMARY KEY (film_id, user_id)
);