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
        switch (eventType) {
            case LIKE:
                sql = "INSERT INTO like_event (user_id, operation, entity_id) VALUES (?,?,?)";
                break;
            case REVIEW:
                sql = "INSERT INTO review_event (user_id, operation, entity_id) VALUES (?,?,?)";
                break;
            case FRIEND:
                sql = "INSERT INTO friend_event (user_id, operation, entity_id) VALUES (?,?,?)";
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String finalSql = sql;
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(finalSql, new String[]{"event_id"});
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getOperation().toString());
            stmt.setLong(3, event.getEntityId());
            return stmt;
        }, keyHolder);
        event.setEventId(keyHolder.getKey().longValue());
        log.info("Event id {}", event.getEventId());
        return event;
    }

    @Override
    public List<Event> getFeedForUser(Long userId) {
        log.info("Getting feed for user id {}", userId);
        String sql = "SELECT * FROM " +
                "(SELECT * FROM LIKE_EVENT " +
                "UNION SELECT * FROM FRIEND_EVENT " +
                "UNION SELECT * FROM REVIEW_EVENT) WHERE user_id = ? ORDER BY timestamp ASC;";
        List<Event> events = jdbcTemplate.query(sql, (rs, rowNum) -> mapEvent(rs), userId);
        log.info("{} events registered for user", events.size());
        return events;
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .timestamp(rs.getTimestamp("timestamp").toInstant().toEpochMilli())
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("eventType")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}