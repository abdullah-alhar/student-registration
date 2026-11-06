package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.CourseCategory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CourseRepository {

    private static final String SELECT =
            "SELECT course_id, course_name, credits, description, max_students, enrolled_students, "
            + "category_id FROM courses";

    private final JdbcTemplate jdbc;
    private final CourseCategoryRepository categoryRepository;

    public CourseRepository(JdbcTemplate jdbc, CourseCategoryRepository categoryRepository) {
        this.jdbc = jdbc;
        this.categoryRepository = categoryRepository;
    }

    // Save new course
    public void save(Course c) {
        jdbc.update("INSERT INTO courses (course_id, course_name, credits, description, max_students, "
                        + "enrolled_students, category_id) VALUES (?, ?, ?, ?, ?, ?, ?)",
                c.getCourseId(), c.getCourseName(), c.getCredits(), c.getDescription(),
                c.getMaxStudents(), c.getEnrolledStudents(), c.getCategory().getCategoryId());
    }

    // Find all courses
    public List<Course> findAll() {
        return query(" ORDER BY created_at, course_id");
    }

    // Find course by ID
    public Course findById(String courseId) {
        return query(" WHERE course_id = ?", courseId).stream().findFirst().orElse(null);
    }

    // Update course
    public void update(Course c) {
        jdbc.update("UPDATE courses SET course_name = ?, credits = ?, description = ?, max_students = ?, "
                        + "enrolled_students = ?, category_id = ? WHERE course_id = ?",
                c.getCourseName(), c.getCredits(), c.getDescription(), c.getMaxStudents(),
                c.getEnrolledStudents(), c.getCategory().getCategoryId(), c.getCourseId());
    }

    // Delete course (its registrations, schedules, feedback and materials go too - see schema.sql)
    public void delete(String courseId) {
        jdbc.update("DELETE FROM courses WHERE course_id = ?", courseId);
    }

    // Runs one SELECT and attaches the category object to each course.
    // Categories are loaded once per call instead of once per course.
    private List<Course> query(String whereAndOrder, Object... args) {
        Map<String, CourseCategory> categories =
                RepositoryHelper.byId(categoryRepository.findAll(), CourseCategory::getCategoryId);
        return jdbc.query(SELECT + whereAndOrder, (rs, rowNum) -> {
            Course course = new Course(
                    rs.getString("course_id"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getString("description"),
                    rs.getInt("max_students"),
                    categories.get(rs.getString("category_id")));
            course.setEnrolledStudents(rs.getInt("enrolled_students"));
            return course;
        }, args);
    }
}
