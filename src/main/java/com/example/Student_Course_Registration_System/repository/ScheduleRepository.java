package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.model.Room;
import com.example.Student_Course_Registration_System.model.Schedule;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ScheduleRepository {

    private static final String FILE_PATH = "src/main/resources/data/schedules.txt";
    private final CourseRepository courseRepository = new CourseRepository();
    private final LecturerRepository lecturerRepository = new LecturerRepository();
    private final RoomRepository roomRepository = new RoomRepository();

    // Save schedule to txt file
    public void save(Schedule schedule) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(schedule.getScheduleId() + "," +
                    schedule.getCourse().getCourseId() + "," +
                    schedule.getLecturer().getLecturerId() + "," +
                    schedule.getDay() + "," +
                    schedule.getStartTime() + "," +
                    schedule.getEndTime() + "," +
                    schedule.getRoom().getRoomId());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving schedule: " + e.getMessage());
        }
    }

    // Find all schedules from txt file
    public List<Schedule> findAll() {
        List<Schedule> schedules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Course course = courseRepository.findById(parts[1]);
                    Lecturer lecturer = lecturerRepository.findById(parts[2]);
                    Room room = roomRepository.findById(parts[6]);
                    // Skip schedules whose course, lecturer, or room has been deleted
                    if (course == null || lecturer == null || room == null) {
                        continue;
                    }
                    Schedule schedule = new Schedule(
                            parts[0], // scheduleId
                            course,   // course object
                            lecturer, // lecturer object
                            parts[3], // day
                            parts[4], // startTime
                            parts[5], // endTime
                            room      // room object
                    );
                    schedules.add(schedule);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading schedules: " + e.getMessage());
        }
        return schedules;
    }

    // Find schedule by ID
    public Schedule findById(String scheduleId) {
        List<Schedule> schedules = findAll();
        for (Schedule schedule : schedules) {
            if (schedule.getScheduleId().equals(scheduleId)) {
                return schedule;
            }
        }
        return null;
    }

    // Find schedules by lecturer ID
    public List<Schedule> findByLecturerId(String lecturerId) {
        List<Schedule> schedules = findAll();
        List<Schedule> lecturerSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getLecturer().getLecturerId().equals(lecturerId)) {
                lecturerSchedules.add(schedule);
            }
        }
        return lecturerSchedules;
    }

    // Find schedules by course ID
    public List<Schedule> findByCourseId(String courseId) {
        List<Schedule> schedules = findAll();
        List<Schedule> courseSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getCourse().getCourseId().equals(courseId)) {
                courseSchedules.add(schedule);
            }
        }
        return courseSchedules;
    }

    // Update schedule in txt file
    public void update(Schedule updatedSchedule) {
        List<Schedule> schedules = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Schedule schedule : schedules) {
                if (schedule.getScheduleId().equals(updatedSchedule.getScheduleId())) {
                    writer.write(updatedSchedule.getScheduleId() + "," +
                            updatedSchedule.getCourse().getCourseId() + "," +
                            updatedSchedule.getLecturer().getLecturerId() + "," +
                            updatedSchedule.getDay() + "," +
                            updatedSchedule.getStartTime() + "," +
                            updatedSchedule.getEndTime() + "," +
                            updatedSchedule.getRoom().getRoomId());
                } else {
                    writer.write(schedule.getScheduleId() + "," +
                            schedule.getCourse().getCourseId() + "," +
                            schedule.getLecturer().getLecturerId() + "," +
                            schedule.getDay() + "," +
                            schedule.getStartTime() + "," +
                            schedule.getEndTime() + "," +
                            schedule.getRoom().getRoomId());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating schedule: " + e.getMessage());
        }
    }

    // Delete schedule from txt file
    public void delete(String scheduleId) {
        List<Schedule> schedules = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Schedule schedule : schedules) {
                if (!schedule.getScheduleId().equals(scheduleId)) {
                    writer.write(schedule.getScheduleId() + "," +
                            schedule.getCourse().getCourseId() + "," +
                            schedule.getLecturer().getLecturerId() + "," +
                            schedule.getDay() + "," +
                            schedule.getStartTime() + "," +
                            schedule.getEndTime() + "," +
                            schedule.getRoom().getRoomId());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting schedule: " + e.getMessage());
        }
    }
}