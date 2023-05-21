package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mappers {
    public Film makeFilm(ResultSet rs) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getInt("duration"))
                .mpa(Mpa.builder().id(rs.getLong("mpa_id")).name(rs.getString("mpa_name")).build())
                .build();
        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        } else {
            film.setReleaseDate(null);
        }
        return film;
    }

    public Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

}
