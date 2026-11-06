package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Lecturer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LecturerRepository {

    private static final String SELECT =
            "SELECT lecturer_id, name, email, phone, address, password, department, specialization "
            + "FROM lecturers";

    private static final RowMapper<Lecturer> ROW_MAPPER = (rs, rowNum) -> new Lecturer(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("password"),
            rs.getString("lecturer_id"),
            rs.getString("department"),
            rs.getString("specialization"));

    private final JdbcTemplate jdbc;

    public LecturerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Auto generate lecturer ID (L001, L002, ...)
    public String generateId() {
        int max = 0;
        for (String id : jdbc.queryForList("SELECT lecturer_id FROM lecturers", String.class)) {
            try {
                int num = Integer.parseInt(id.replace("L", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("L%03d", max + 1);
    }

    // Save new lecturer
    public void save(Lecturer l) {
        jdbc.update("INSERT INTO lecturers (lecturer_id, name, email, phone, address, password, "
                        + "department, specialization) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                l.getLecturerId(), l.getName(), l.getEmail(), l.getPhone(), l.getAddress(),
                l.getPassword(), l.getDepartment(), l.getSpecialization());
    }

    // Find all lecturers
    public List<Lecturer> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, lecturer_id", ROW_MAPPER);
    }

    // Find lecturer by ID
    public Lecturer findById(String lecturerId) {
        return jdbc.query(SELECT + " WHERE lecturer_id = ?", ROW_MAPPER, lecturerId)
                .stream().findFirst().orElse(null);
    }

    // Update lecturer
    public void update(Lecturer l) {
        jdbc.update("UPDATE lecturers SET name = ?, email = ?, phone = ?, address = ?, password = ?, "
                        + "department = ?, specialization = ? WHERE lecturer_id = ?",
                l.getName(), l.getEmail(), l.getPhone(), l.getAddress(), l.getPassword(),
                l.getDepartment(), l.getSpecialization(), l.getLecturerId());
    }

    // Delete lecturer
    public void delete(String lecturerId) {
        jdbc.update("DELETE FROM lecturers WHERE lecturer_id = ?", lecturerId);
    }
}
