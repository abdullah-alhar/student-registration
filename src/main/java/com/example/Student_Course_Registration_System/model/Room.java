package com.example.Student_Course_Registration_System.model;

public class Room {

    private String roomId;
    private String roomName;
    private int capacity;
    private boolean available;

    public Room(String roomId, String roomName, int capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.available = true;
    }

    // Getters
    public String getRoomId() {
        return roomId;
    }
    public String getRoomName() {
        return roomName;
    }
    public int getCapacity() {
        return capacity;
    }
    public boolean isAvailable() {
        return available;
    }

    // Setters only for changeable fields
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
}