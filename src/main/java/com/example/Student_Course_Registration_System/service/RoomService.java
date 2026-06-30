package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.Room;
import com.example.Student_Course_Registration_System.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    // Add new room - returns error message or null on success
    public String addRoom(Room room) {
        // Check if room ID already exists
        Room existing = roomRepository.findById(room.getRoomId());
        if (existing != null) {
            return "Room ID already exists";
        }
        // Check if room name already exists
        List<Room> rooms = roomRepository.findAll();
        for (Room r : rooms) {
            if (r.getRoomName().equalsIgnoreCase(room.getRoomName())) {
                return "A room with this name already exists";
            }
        }
        // Check if capacity is valid
        if (room.getCapacity() <= 0) {
            return "Room capacity must be greater than zero";
        }
        roomRepository.save(room);
        return null;
    }

    // Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get room by ID
    public Room getRoomById(String roomId) {
        Room room = roomRepository.findById(roomId);
        if (room == null) {
            System.out.println("Room not found");
        }
        return room;
    }

    // Get all available rooms
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    // Get available rooms by capacity
    public List<Room> getAvailableRoomsByCapacity(int requiredCapacity) {
        List<Room> rooms = roomRepository.findAvailableRooms();
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getCapacity() >= requiredCapacity) {
                result.add(room);
            }
        }
        return result;
    }

    // Update room - returns error message or null on success
    public String updateRoom(Room room) {
        Room existing = roomRepository.findById(room.getRoomId());
        if (existing == null) {
            return "Room not found";
        }
        // Check if capacity is valid
        if (room.getCapacity() <= 0) {
            return "Room capacity must be greater than zero";
        }
        // Check duplicate room name (exclude self)
        List<Room> rooms = roomRepository.findAll();
        for (Room r : rooms) {
            if (!r.getRoomId().equals(room.getRoomId())) {
                if (r.getRoomName().equalsIgnoreCase(room.getRoomName())) {
                    return "A room with this name already exists";
                }
            }
        }
        roomRepository.update(room);
        return null;
    }

    // Delete room
    public void deleteRoom(String roomId) {
        Room existing = roomRepository.findById(roomId);
        if (existing == null) {
            System.out.println("Room not found");
            return;
        }
        // Check if room is currently in use
        if (!existing.isAvailable()) {
            System.out.println("Cannot delete room that is currently in use");
            return;
        }
        roomRepository.delete(roomId);
        System.out.println("Room deleted successfully");
    }

    // Search room by name
    public List<Room> searchByName(String name) {
        List<Room> rooms = roomRepository.findAll();
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getRoomName().toLowerCase().contains(name.toLowerCase())) {
                result.add(room);
            }
        }
        return result;
    }

    // Get total room count
    public int getTotalRooms() {
        return roomRepository.findAll().size();
    }
}