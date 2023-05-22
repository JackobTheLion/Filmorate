package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Qualifier("dbStorage")
@Slf4j
public class DbEventStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbEventStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event addEvent(Event event) {
        log.info("Adding event {} to DB", event);
        String sql = "";
        EventType eventType = event.getEventType();
        log.info("Event type: {}", eventType);
        switch (eventType) {
            case LIKE:
                sql = "INSERT INTO like_event (timestamp, user_id, operation, like_id) VALUES (?,?,?,?)";
                break;
            case REVIEW:
                sql = "INSERT INTO review_event (timestamp, user_id, operation, review_id) VALUES (?,?,?,?)";
                break;
            case FRIEND:
                sql = "INSERT INTO friend_event (timestamp, user_id, operation, friendship_id) VALUES (?,?,?,?)";
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String finalSql = sql;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(finalSql, new String[]{"event_id"});
            stmt.setTimestamp(1, event.getTimestamp());
            stmt.setLong(2, event.getUserId());
            stmt.setString(3, event.getEventType().toString());
            stmt.setLong(4, event.getEntityId());
            return stmt;
        }, keyHolder);
        event.setEventId(keyHolder.getKey().longValue());
        return event;
    }

    @Override
    public List<Event> getFeedForUser(Long userId) {
        String sql = "SELECT * FROM " +
                "(SELECT * FROM LIKE_EVENT " +
                "UNION SELECT * FROM FRIEND_EVENT " +
                "UNION SELECT * FROM REVIEW_EVENT) WHERE user_id = ?;";
        List<Event> events = jdbcTemplate.query(sql, (rs, rowNum) -> mapEvent(rs), userId);
        return events;
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .timestamp(rs.getTimestamp("timestamp"))
                .userId(rs.getLong("userId"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entityId"))
                .build();
    }
}