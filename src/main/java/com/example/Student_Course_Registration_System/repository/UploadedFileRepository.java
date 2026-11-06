package com.example.Student_Course_Registration_System.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Stores uploaded files (payment receipts, course materials) inside the database.
 * Free hosts like Render wipe their disk on every restart, so files cannot stay
 * in the uploads/ folder any more.
 */
@Repository
public class UploadedFileRepository {

    public static final String PAYMENTS = "payments";
    public static final String MATERIALS = "materials";

    /** The bytes of one stored file plus its type, e.g. "application/pdf". */
    public record StoredFile(String fileName, String contentType, byte[] data) {}

    private final JdbcTemplate jdbc;

    public UploadedFileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Saves an uploaded file. The name gets a timestamp in front (same as before)
     * so two files with the same name never clash. Returns the saved name, or null on error.
     */
    public String save(String folder, MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            saveBytes(folder, fileName, file.getContentType(), file.getBytes());
            return fileName;
        } catch (IOException e) {
            System.out.println("Error uploading file: " + e.getMessage());
            return null;
        }
    }

    /** Saves raw bytes. If a file with that name already exists it is left unchanged. */
    public void saveBytes(String folder, String fileName, String contentType, byte[] data) {
        jdbc.update("INSERT INTO uploaded_files (folder, file_name, content_type, data) VALUES (?, ?, ?, ?) "
                        + "ON CONFLICT (folder, file_name) DO NOTHING",
                folder, fileName, contentType, data);
    }

    /** Loads a file, or returns null if it does not exist. */
    public StoredFile find(String folder, String fileName) {
        if (fileName == null || fileName.isEmpty()) return null;
        return jdbc.query("SELECT file_name, content_type, data FROM uploaded_files "
                                + "WHERE folder = ? AND file_name = ?",
                        (rs, rowNum) -> new StoredFile(rs.getString("file_name"),
                                rs.getString("content_type"), rs.getBytes("data")),
                        folder, fileName)
                .stream().findFirst().orElse(null);
    }

    /** Deletes a file (does nothing if it does not exist). */
    public void delete(String folder, String fileName) {
        int deleted = jdbc.update("DELETE FROM uploaded_files WHERE folder = ? AND file_name = ?",
                folder, fileName);
        if (deleted > 0) {
            System.out.println("File deleted successfully: " + fileName);
        }
    }
}
