package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.Room;
import com.example.Student_Course_Registration_System.model.Schedule;
import com.example.Student_Course_Registration_System.repository.RoomRepository;
import com.example.Student_Course_Registration_System.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Add new schedule
    public void addSchedule(Schedule schedule) {
        // Check if schedule ID already exists
        Schedule existing = scheduleRepository.findById(schedule.getScheduleId());
        if (existing != null) {
            System.out.println("Schedule ID already exists");
            return;
        }
        // Check if room is available
        Room room = roomRepository.findById(schedule.getRoom().getRoomId());
        if (room == null || !room.isAvailable()) {
            System.out.println("Room is not available");
            return;
        }
        // Check if room capacity is enough for course
        if (room.getCapacity() < schedule.getCourse().getMaxStudents()) {
            System.out.println("Room capacity is not enough for this course");
            return;
        }
        // Check for schedule conflicts
        List<Schedule> allSchedules = scheduleRepository.findAll();
        for (Schedule s : allSchedules) {
            if (schedule.hasConflict(s)) {
                System.out.println("Schedule conflict detected with schedule: " + s.getScheduleId());
                return;
            }
        }
        // Update room availability
        // room.setAvailable(false); // REMOVED to fix room booking bug (prevents locking room for whole day)
        // roomRepository.update(room);
        scheduleRepository.save(schedule);
        System.out.println("Schedule added successfully");
    }

    // Get all schedules
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // Get schedule by ID
    public Schedule getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) {
            System.out.println("Schedule not found");
        }
        return schedule;
    }

    // Get schedules by lecturer ID
    public List<Schedule> getSchedulesByLecturerId(String lecturerId) {
        return scheduleRepository.findByLecturerId(lecturerId);
    }

    // Get schedules by course ID
    public List<Schedule> getSchedulesByCourseId(String courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }

    // Update schedule
    public void updateSchedule(Schedule schedule) {
        Schedule existing = scheduleRepository.findById(schedule.getScheduleId());
        if (existing == null) {
            System.out.println("Schedule not found");
            return;
        }
        // Check for conflicts with other schedules
        List<Schedule> allSchedules = scheduleRepository.findAll();
        for (Schedule s : allSchedules) {
            if (!s.getScheduleId().equals(schedule.getScheduleId())) {
                if (schedule.hasConflict(s)) {
                    System.out.println("Schedule conflict detected");
                    return;
                }
            }
        }
        scheduleRepository.update(schedule);
        System.out.println("Schedule updated successfully");
    }

    // Delete schedule
    public void deleteSchedule(String scheduleId) {
        Schedule existing = scheduleRepository.findById(scheduleId);
        if (existing == null) {
            System.out.println("Schedule not found");
            return;
        }
        // Free up room when schedule deleted
        Room room = roomRepository.findById(existing.getRoom().getRoomId());
        if (room != null) {
            // room.setAvailable(true); // REMOVED to fix room booking bug
            // roomRepository.update(room);
        }
        scheduleRepository.delete(scheduleId);
        System.out.println("Schedule deleted successfully");
    }

    // Get total schedule count
    public int getTotalSchedules() {
        return scheduleRepository.findAll().size();
    }
}