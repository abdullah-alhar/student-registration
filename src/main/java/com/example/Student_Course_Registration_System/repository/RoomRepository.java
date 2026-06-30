package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Room;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomRepository {

    private static final String FILE_PATH = "src/main/resources/data/rooms.txt";

    // Save room to txt file
    public void save(Room room) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(room.getRoomId() + "," +
                    room.getRoomName() + "," +
                    room.getCapacity() + "," +
                    room.isAvailable());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving room: " + e.getMessage());
        }
    }

    // Find all rooms from txt file
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Room room = new Room(
                            parts[0],                          // roomId
                            parts[1],                          // roomName
                            Integer.parseInt(parts[2])         // capacity
                    );
                    room.setAvailable(Boolean.parseBoolean(parts[3]));
                    rooms.add(room);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rooms: " + e.getMessage());
        }
        return rooms;
    }

    // Find room by ID
    public Room findById(String roomId) {
        List<Room> rooms = findAll();
        for (Room room : rooms) {
            if (room.getRoomId().equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    // Find all available rooms
    public List<Room> findAvailableRooms() {
        List<Room> rooms = findAll();
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    // Update room in txt file
    public void update(Room updatedRoom) {
        List<Room> rooms = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Room room : rooms) {
                if (room.getRoomId().equals(updatedRoom.getRoomId())) {
                    writer.write(updatedRoom.getRoomId() + "," +
                            updatedRoom.getRoomName() + "," +
                            updatedRoom.getCapacity() + "," +
                            updatedRoom.isAvailable());
                } else {
                    writer.write(room.getRoomId() + "," +
                            room.getRoomName() + "," +
                            room.getCapacity() + "," +
                            room.isAvailable());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating room: " + e.getMessage());
        }
    }

    // Delete room from txt file
    public void delete(String roomId) {
        List<Room> rooms = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Room room : rooms) {
                if (!room.getRoomId().equals(roomId)) {
                    writer.write(room.getRoomId() + "," +
                            room.getRoomName() + "," +
                            room.getCapacity() + "," +
                            room.isAvailable());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting room: " + e.getMessage());
        }
    }
}