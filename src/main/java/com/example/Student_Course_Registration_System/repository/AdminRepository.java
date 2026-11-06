package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Admin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminRepository {

    private static final String SELECT =
            "SELECT admin_id, name, email, phone, address, password, access_level FROM admins";

    private static final RowMapper<Admin> ROW_MAPPER = (rs, rowNum) -> new Admin(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("password"),
            rs.getString("admin_id"),
            rs.getInt("access_level"));

    private final JdbcTemplate jdbc;

    public AdminRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Auto generate admin ID (A001, A002, ...)
    public String generateId() {
        int max = 0;
        for (String id : jdbc.queryForList("SELECT admin_id FROM admins", String.class)) {
            try {
                int num = Integer.parseInt(id.replace("A", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("A%03d", max + 1);
    }

    // Save new admin
    public void save(Admin a) {
        jdbc.update("INSERT INTO admins (admin_id, name, email, phone, address, password, access_level) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                a.getAdminId(), a.getName(), a.getEmail(), a.getPhone(), a.getAddress(),
                a.getPassword(), a.getAccessLevel());
    }

    // Find all admins
    public List<Admin> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, admin_id", ROW_MAPPER);
    }

    // Find admin by ID
    public Admin findById(String adminId) {
        return jdbc.query(SELECT + " WHERE admin_id = ?", ROW_MAPPER, adminId)
                .stream().findFirst().orElse(null);
    }

    // Update admin
    public void update(Admin a) {
        jdbc.update("UPDATE admins SET name = ?, email = ?, phone = ?, address = ?, password = ?, "
                        + "access_level = ? WHERE admin_id = ?",
                a.getName(), a.getEmail(), a.getPhone(), a.getAddress(), a.getPassword(),
                a.getAccessLevel(), a.getAdminId());
    }

    // Delete admin
    public void delete(String adminId) {
        jdbc.update("DELETE FROM admins WHERE admin_id = ?", adminId);
    }
}
