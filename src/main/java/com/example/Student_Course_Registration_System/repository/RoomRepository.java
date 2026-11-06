package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Room;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomRepository {

    private static final String SELECT = "SELECT room_id, room_name, capacity, available FROM rooms";

    private static final RowMapper<Room> ROW_MAPPER = (rs, rowNum) -> {
        Room room = new Room(rs.getString("room_id"), rs.getString("room_name"), rs.getInt("capacity"));
        room.setAvailable(rs.getBoolean("available"));
        return room;
    };

    private final JdbcTemplate jdbc;

    public RoomRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Save new room
    public void save(Room room) {
        jdbc.update("INSERT INTO rooms (room_id, room_name, capacity, available) VALUES (?, ?, ?, ?)",
                room.getRoomId(), room.getRoomName(), room.getCapacity(), room.isAvailable());
    }

    // Find all rooms
    public List<Room> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, room_id", ROW_MAPPER);
    }

    // Find room by ID
    public Room findById(String roomId) {
        return jdbc.query(SELECT + " WHERE room_id = ?", ROW_MAPPER, roomId)
                .stream().findFirst().orElse(null);
    }

    // Find all available rooms
    public List<Room> findAvailableRooms() {
        return jdbc.query(SELECT + " WHERE available = TRUE ORDER BY created_at, room_id", ROW_MAPPER);
    }

    // Update room
    public void update(Room room) {
        jdbc.update("UPDATE rooms SET room_name = ?, capacity = ?, available = ? WHERE room_id = ?",
                room.getRoomName(), room.getCapacity(), room.isAvailable(), room.getRoomId());
    }

    // Delete room
    public void delete(String roomId) {
        jdbc.update("DELETE FROM rooms WHERE room_id = ?", roomId);
    }
}
