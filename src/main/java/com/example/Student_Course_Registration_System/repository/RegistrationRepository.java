package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class RegistrationRepository {

    private static final String SELECT =
            "SELECT registration_id, student_id, course_id, registration_date, status FROM registrations";

    private final JdbcTemplate jdbc;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public RegistrationRepository(JdbcTemplate jdbc, StudentRepository studentRepository,
                                  CourseRepository courseRepository) {
        this.jdbc = jdbc;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // Save new registration
    public void save(Registration r) {
        jdbc.update("INSERT INTO registrations (registration_id, student_id, course_id, registration_date, status) "
                        + "VALUES (?, ?, ?, CAST(? AS DATE), ?)",
                r.getRegistrationId(), r.getStudent().getStudentId(), r.getCourse().getCourseId(),
                r.getRegistrationDate(), r.getStatus().name());
    }

    // Find all registrations
    public List<Registration> findAll() {
        return query(" ORDER BY created_at, registration_id");
    }

    // Find registration by ID
    public Registration findById(String registrationId) {
        return query(" WHERE registration_id = ?", registrationId).stream().findFirst().orElse(null);
    }

    // Find all registrations by student ID
    public List<Registration> findByStudentId(String studentId) {
        return query(" WHERE student_id = ? ORDER BY created_at, registration_id", studentId);
    }

    // Update registration
    public void update(Registration r) {
        jdbc.update("UPDATE registrations SET student_id = ?, course_id = ?, registration_date = CAST(? AS DATE), "
                        + "status = ? WHERE registration_id = ?",
                r.getStudent().getStudentId(), r.getCourse().getCourseId(), r.getRegistrationDate(),
                r.getStatus().name(), r.getRegistrationId());
    }

    // Delete registration
    public void delete(String registrationId) {
        jdbc.update("DELETE FROM registrations WHERE registration_id = ?", registrationId);
    }

    // Runs one SELECT and attaches the Student and Course objects to each row.
    private List<Registration> query(String whereAndOrder, Object... args) {
        Map<String, Student> students = RepositoryHelper.byId(studentRepository.findAll(), Student::getStudentId);
        Map<String, Course> courses = RepositoryHelper.byId(courseRepository.findAll(), Course::getCourseId);
        return jdbc.query(SELECT + whereAndOrder, (rs, rowNum) -> {
            Student student = students.get(rs.getString("student_id"));
            Course course = courses.get(rs.getString("course_id"));
            if (student == null || course == null) return null;   // cannot happen thanks to foreign keys
            Registration registration = new Registration(
                    rs.getString("registration_id"), student, course, rs.getString("registration_date"));
            // Restore the saved status (new Registration objects start as PENDING).
            // The status is set directly so the course enrolled count is NOT changed again.
            registration.restoreStatus(rs.getString("status"));
            return registration;
        }, args).stream().filter(Objects::nonNull).toList();
    }
}
