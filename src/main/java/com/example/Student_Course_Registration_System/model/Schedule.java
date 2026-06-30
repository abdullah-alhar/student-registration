package com.example.Student_Course_Registration_System.model;

public class Schedule {

    private String scheduleId;
    private Course course;
    private Lecturer lecturer;
    private String day;
    private String startTime;
    private String endTime;
    private Room room;

    // store other reference variables
    public Schedule(String scheduleId, Course course, Lecturer lecturer, String day, String startTime, String endTime, Room room) {
        this.scheduleId = scheduleId;
        this.course = course;
        this.lecturer = lecturer;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    // Getters
    public String getScheduleId() {
        return scheduleId;
    }
    public Course getCourse() {
        return course;
    }
    public Lecturer getLecturer() {
        return lecturer;
    }
    public String getDay() {
        return day;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public Room getRoom() {
        return room;
    }


    public void setRoom(Room room) {
        this.room = room;
    }

    //this method depent on room class
    public boolean hasConflict(Schedule other) {
        if (this.day.equals(other.day) && this.room.getRoomId().equals(other.room.getRoomId())) {
            if (this.startTime.compareTo(other.endTime) < 0 &&
                    this.endTime.compareTo(other.startTime) > 0) {
                System.out.println("Schedule conflict detected");
                return true;
            }
        }
        return false;
    }
}