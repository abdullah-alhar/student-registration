package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepository {

    private static final String SELECT =
            "SELECT student_id, name, email, phone, address, password, semester, "
            + "enrollment_date, status FROM students";

    // Turns one database row into a Student object
    private static final RowMapper<Student> ROW_MAPPER = (rs, rowNum) -> {
        Student student = new Student(
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("address"),
                rs.getString("password"),
                rs.getString("student_id"),
                rs.getString("enrollment_date"),   // DATE comes back as "yyyy-MM-dd"
                rs.getInt("semester"));
        student.setStatus(rs.getString("status"));
        return student;
    };

    private final JdbcTemplate jdbc;

    public StudentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Auto generate student ID (S001, S002, ...)
    public String generateId() {
        int max = 0;
        for (String id : jdbc.queryForList("SELECT student_id FROM students", String.class)) {
            try {
                int num = Integer.parseInt(id.replace("S", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("S%03d", max + 1);
    }

    // Save new student
    public void save(Student student) {
        jdbc.update("INSERT INTO students (student_id, name, email, phone, address, password, "
                        + "semester, enrollment_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS DATE), ?)",
                student.getStudentId(), student.getName(), student.getEmail(), student.getPhone(),
                student.getAddress(), student.getPassword(), student.getSemester(),
                student.getEnrollmentDate(), student.getStatus());
    }

    // Find all students
    public List<Student> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, student_id", ROW_MAPPER);
    }

    // Find student by ID
    public Student findById(String studentId) {
        return jdbc.query(SELECT + " WHERE student_id = ?", ROW_MAPPER, studentId)
                .stream().findFirst().orElse(null);
    }

    // Update student
    public void update(Student s) {
        jdbc.update("UPDATE students SET name = ?, email = ?, phone = ?, address = ?, password = ?, "
                        + "semester = ?, enrollment_date = CAST(? AS DATE), status = ? WHERE student_id = ?",
                s.getName(), s.getEmail(), s.getPhone(), s.getAddress(), s.getPassword(),
                s.getSemester(), s.getEnrollmentDate(), s.getStatus(), s.getStudentId());
    }

    // Delete student (their registrations, payments and feedback are removed too - see schema.sql)
    public void delete(String studentId) {
        jdbc.update("DELETE FROM students WHERE student_id = ?", studentId);
    }
}
