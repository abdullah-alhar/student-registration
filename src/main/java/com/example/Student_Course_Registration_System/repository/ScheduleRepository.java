package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.model.Room;
import com.example.Student_Course_Registration_System.model.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class ScheduleRepository {

    // Times are returned as "HH:mm" (e.g. 11:30), the same text the app used before
    private static final String SELECT =
            "SELECT schedule_id, course_id, lecturer_id, day, "
            + "to_char(start_time, 'HH24:MI') AS start_time, to_char(end_time, 'HH24:MI') AS end_time, "
            + "room_id FROM schedules";

    private final JdbcTemplate jdbc;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final RoomRepository roomRepository;

    public ScheduleRepository(JdbcTemplate jdbc, CourseRepository courseRepository,
                              LecturerRepository lecturerRepository, RoomRepository roomRepository) {
        this.jdbc = jdbc;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
        this.roomRepository = roomRepository;
    }

    // Save new schedule
    public void save(Schedule s) {
        jdbc.update("INSERT INTO schedules (schedule_id, course_id, lecturer_id, day, start_time, end_time, room_id) "
                        + "VALUES (?, ?, ?, ?, CAST(? AS TIME), CAST(? AS TIME), ?)",
                s.getScheduleId(), s.getCourse().getCourseId(), s.getLecturer().getLecturerId(),
                s.getDay(), s.getStartTime(), s.getEndTime(), s.getRoom().getRoomId());
    }

    // Find all schedules
    public List<Schedule> findAll() {
        return query(" ORDER BY created_at, schedule_id");
    }

    // Find schedule by ID
    public Schedule findById(String scheduleId) {
        return query(" WHERE schedule_id = ?", scheduleId).stream().findFirst().orElse(null);
    }

    // Find schedules by lecturer ID
    public List<Schedule> findByLecturerId(String lecturerId) {
        return query(" WHERE lecturer_id = ? ORDER BY created_at, schedule_id", lecturerId);
    }

    // Find schedules by course ID
    public List<Schedule> findByCourseId(String courseId) {
        return query(" WHERE course_id = ? ORDER BY created_at, schedule_id", courseId);
    }

    // Update schedule
    public void update(Schedule s) {
        jdbc.update("UPDATE schedules SET course_id = ?, lecturer_id = ?, day = ?, start_time = CAST(? AS TIME), "
                        + "end_time = CAST(? AS TIME), room_id = ? WHERE schedule_id = ?",
                s.getCourse().getCourseId(), s.getLecturer().getLecturerId(), s.getDay(),
                s.getStartTime(), s.getEndTime(), s.getRoom().getRoomId(), s.getScheduleId());
    }

    // Delete schedule
    public void delete(String scheduleId) {
        jdbc.update("DELETE FROM schedules WHERE schedule_id = ?", scheduleId);
    }

    // Runs one SELECT and attaches the Course, Lecturer and Room objects to each row.
    private List<Schedule> query(String whereAndOrder, Object... args) {
        Map<String, Course> courses = RepositoryHelper.byId(courseRepository.findAll(), Course::getCourseId);
        Map<String, Lecturer> lecturers = RepositoryHelper.byId(lecturerRepository.findAll(), Lecturer::getLecturerId);
        Map<String, Room> rooms = RepositoryHelper.byId(roomRepository.findAll(), Room::getRoomId);
        return jdbc.query(SELECT + whereAndOrder, (rs, rowNum) -> {
            Course course = courses.get(rs.getString("course_id"));
            Lecturer lecturer = lecturers.get(rs.getString("lecturer_id"));
            Room room = rooms.get(rs.getString("room_id"));
            if (course == null || lecturer == null || room == null) return null;  // cannot happen thanks to foreign keys
            return new Schedule(rs.getString("schedule_id"), course, lecturer, rs.getString("day"),
                    rs.getString("start_time"), rs.getString("end_time"), room);
        }, args).stream().filter(Objects::nonNull).toList();
    }
}
