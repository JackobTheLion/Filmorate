CREATE TABLE film_genre (
  film_id integer NOT NULL REFERENCES films (film_id),
  genre_id integer NOT NULL REFERENCES genre (genre_id),
  PRIMARY KEY (film_id, genre_id)
);