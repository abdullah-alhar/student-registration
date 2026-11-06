package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.CourseMaterial;
import com.example.Student_Course_Registration_System.repository.UploadedFileRepository.StoredFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Repository
public class CourseMaterialRepository {

    private static final String SELECT =
            "SELECT material_id, title, file_type, course_id, file_name FROM course_materials";

    private static final RowMapper<CourseMaterial> ROW_MAPPER = (rs, rowNum) -> {
        CourseMaterial material = new CourseMaterial(
                rs.getString("material_id"),
                rs.getString("title"),
                rs.getString("file_type"),
                rs.getString("course_id"));
        material.saveFile(rs.getString("file_name"));
        return material;
    };

    private final JdbcTemplate jdbc;
    private final UploadedFileRepository fileRepository;

    public CourseMaterialRepository(JdbcTemplate jdbc, UploadedFileRepository fileRepository) {
        this.jdbc = jdbc;
        this.fileRepository = fileRepository;
    }

    // Save new course material
    public void save(CourseMaterial m) {
        jdbc.update("INSERT INTO course_materials (material_id, title, file_type, course_id, file_name) "
                        + "VALUES (?, ?, ?, ?, ?)",
                m.getMaterialId(), m.getTitle(), m.getFileType(), m.getCourseId(), m.getFileName());
    }

    // Save uploaded file (stored in the database). Returns the saved file name.
    public String saveUploadedFile(MultipartFile file) {
        return fileRepository.save(UploadedFileRepository.MATERIALS, file);
    }

    // Load an uploaded material file, or null if it does not exist
    public StoredFile loadUploadedFile(String fileName) {
        return fileRepository.find(UploadedFileRepository.MATERIALS, fileName);
    }

    // Find all course materials
    public List<CourseMaterial> findAll() {
        return jdbc.query(SELECT + " ORDER BY created_at, material_id", ROW_MAPPER);
    }

    // Find material by ID
    public CourseMaterial findById(String materialId) {
        return jdbc.query(SELECT + " WHERE material_id = ?", ROW_MAPPER, materialId)
                .stream().findFirst().orElse(null);
    }

    // Find all materials by course ID
    public List<CourseMaterial> findByCourseId(String courseId) {
        return jdbc.query(SELECT + " WHERE course_id = ? ORDER BY created_at, material_id", ROW_MAPPER, courseId);
    }

    // Update material
    public void update(CourseMaterial m) {
        jdbc.update("UPDATE course_materials SET title = ?, file_type = ?, course_id = ?, file_name = ? "
                        + "WHERE material_id = ?",
                m.getTitle(), m.getFileType(), m.getCourseId(), m.getFileName(), m.getMaterialId());
    }

    // Delete material
    public void delete(String materialId) {
        jdbc.update("DELETE FROM course_materials WHERE material_id = ?", materialId);
    }

    // Delete uploaded file
    public void deleteUploadedFile(String fileName) {
        fileRepository.delete(UploadedFileRepository.MATERIALS, fileName);
    }
}
