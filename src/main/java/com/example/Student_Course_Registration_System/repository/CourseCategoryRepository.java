package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.CourseCategory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseCategoryRepository {

    private static final String SELECT = "SELECT category_id, category_name FROM course_categories";

    private static final RowMapper<CourseCategory> ROW_MAPPER = (rs, rowNum) ->
            new CourseCategory(rs.getString("category_id"), rs.getString("category_name"));

    private final JdbcTemplate jdbc;

    public CourseCategoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Save new category
    public void save(CourseCategory category) {
        jdbc.update("INSERT INTO course_categories (category_id, category_name) VALUES (?, ?)",
                category.getCategoryId(), category.getCategoryName());
    }

    // Find all categories
    public List<CourseCategory> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, category_id", ROW_MAPPER);
    }

    // Find category by ID
    public CourseCategory findById(String categoryId) {
        return jdbc.query(SELECT + " WHERE category_id = ?", ROW_MAPPER, categoryId)
                .stream().findFirst().orElse(null);
    }

    // Update category
    public void update(CourseCategory category) {
        jdbc.update("UPDATE course_categories SET category_name = ? WHERE category_id = ?",
                category.getCategoryName(), category.getCategoryId());
    }

    // Delete category. A category that still has courses is kept, because
    // removing it would leave those courses without a category.
    public void delete(String categoryId) {
        try {
            jdbc.update("DELETE FROM course_categories WHERE category_id = ?", categoryId);
        } catch (DataIntegrityViolationException e) {
            System.out.println("Cannot delete category " + categoryId + ": it still has courses");
        }
    }
}
