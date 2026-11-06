package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Feedback;
import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class FeedbackRepository {

    private static final String SELECT =
            "SELECT feedback_id, student_id, course_id, rating, comment, feedback_date, submitted FROM feedbacks";

    private final JdbcTemplate jdbc;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public FeedbackRepository(JdbcTemplate jdbc, StudentRepository studentRepository,
                              CourseRepository courseRepository) {
        this.jdbc = jdbc;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // Save new feedback
    public void save(Feedback f) {
        jdbc.update("INSERT INTO feedbacks (feedback_id, student_id, course_id, rating, comment, feedback_date, "
                        + "submitted) VALUES (?, ?, ?, ?, ?, CAST(? AS DATE), ?)",
                f.getFeedbackId(), f.getStudent().getStudentId(), f.getCourse().getCourseId(),
                f.getRating(), f.getComment(), f.getDate(), f.isSubmitted());
    }

    // Find all feedbacks
    public List<Feedback> findAll() {
        return query(" ORDER BY created_at, feedback_id");
    }

    // Find feedback by ID
    public Feedback findById(String feedbackId) {
        return query(" WHERE feedback_id = ?", feedbackId).stream().findFirst().orElse(null);
    }

    // Find all feedbacks by student ID
    public List<Feedback> findByStudentId(String studentId) {
        return query(" WHERE student_id = ? ORDER BY created_at, feedback_id", studentId);
    }

    // Find all feedbacks by course ID
    public List<Feedback> findByCourseId(String courseId) {
        return query(" WHERE course_id = ? ORDER BY created_at, feedback_id", courseId);
    }

    // Update feedback
    public void update(Feedback f) {
        jdbc.update("UPDATE feedbacks SET student_id = ?, course_id = ?, rating = ?, comment = ?, "
                        + "feedback_date = CAST(? AS DATE), submitted = ? WHERE feedback_id = ?",
                f.getStudent().getStudentId(), f.getCourse().getCourseId(), f.getRating(),
                f.getComment(), f.getDate(), f.isSubmitted(), f.getFeedbackId());
    }

    // Delete feedback
    public void delete(String feedbackId) {
        jdbc.update("DELETE FROM feedbacks WHERE feedback_id = ?", feedbackId);
    }

    // Runs one SELECT and attaches the Student and Course objects to each row.
    private List<Feedback> query(String whereAndOrder, Object... args) {
        Map<String, Student> students = RepositoryHelper.byId(studentRepository.findAll(), Student::getStudentId);
        Map<String, Course> courses = RepositoryHelper.byId(courseRepository.findAll(), Course::getCourseId);
        return jdbc.query(SELECT + whereAndOrder, (rs, rowNum) -> {
            Student student = students.get(rs.getString("student_id"));
            Course course = courses.get(rs.getString("course_id"));
            if (student == null || course == null) return null;   // cannot happen thanks to foreign keys
            Feedback feedback = new Feedback(
                    rs.getString("feedback_id"), student, course, rs.getInt("rating"),
                    rs.getString("comment"), rs.getString("feedback_date"));
            if (rs.getBoolean("submitted")) {
                feedback.submitFeedback();
            }
            return feedback;
        }, args).stream().filter(Objects::nonNull).toList();
    }
}
