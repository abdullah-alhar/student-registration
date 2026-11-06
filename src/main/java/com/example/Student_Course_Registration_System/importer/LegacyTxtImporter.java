package com.example.Student_Course_Registration_System.importer;

import com.example.Student_Course_Registration_System.config.DatabaseConfig;
import com.example.Student_Course_Registration_System.config.PasswordHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ONE-TIME import of the old .txt data (and uploaded files) into PostgreSQL.
 *
 * Run it with:
 *   mvn spring-boot:run -Dspring-boot.run.profiles=import
 *
 * - Reads legacy-data/data/*.txt and legacy-data/uploads/{payments,materials}/
 *   (change with -Dspring-boot.run.arguments="--import.data-dir=... --import.uploads-dir=...")
 * - Hashes every plain-text password with BCrypt on the way in.
 * - Safe to run more than once: rows that already exist are skipped (ON CONFLICT DO NOTHING).
 * - Everything happens in ONE transaction: if anything fails, nothing is saved.
 * - Uses the DIRECT (unpooled) database connection, as Neon recommends for bulk loads.
 */
@Component
@Profile("import")
public class LegacyTxtImporter implements CommandLineRunner {

    private final Environment env;
    private final PasswordHasher passwordHasher;
    private final ApplicationContext context;
    private final Path dataDir;
    private final Path uploadsDir;

    private JdbcTemplate jdbc;
    private final List<String> report = new ArrayList<>();
    private final List<String> warnings = new ArrayList<>();
    private final Set<String> students = new HashSet<>(), courses = new HashSet<>(), lecturers = new HashSet<>(),
            rooms = new HashSet<>(), categories = new HashSet<>();

    public LegacyTxtImporter(Environment env, PasswordHasher passwordHasher, ApplicationContext context,
                             @Value("${import.data-dir:legacy-data/data}") String dataDir,
                             @Value("${import.uploads-dir:legacy-data/uploads}") String uploadsDir) {
        this.env = env;
        this.passwordHasher = passwordHasher;
        this.context = context;
        this.dataDir = Path.of(dataDir);
        this.uploadsDir = Path.of(uploadsDir);
    }

    @Override
    public void run(String... args) {
        System.out.println("\n=== Importing old .txt data from " + dataDir.toAbsolutePath() + " ===");
        if (!Files.isDirectory(dataDir)) {
            throw new IllegalStateException("Folder not found: " + dataDir.toAbsolutePath());
        }

        DataSource direct = DatabaseConfig.directDataSource(env);
        jdbc = new JdbcTemplate(direct);
        new TransactionTemplate(new DataSourceTransactionManager(direct)).executeWithoutResult(tx -> {
            importCategories();
            importCourses();
            importRooms();
            importStudents();
            importLecturers();
            importAdmins();
            importRegistrations();
            importSchedules();
            importPayments();
            importFeedbacks();
            importMaterials();
            recalculateEnrolledCounts();
        });

        System.out.println("\n=== Import finished ===");
        report.forEach(line -> System.out.println("  " + line));
        if (!warnings.isEmpty()) {
            System.out.println("\nWarnings (these rows/files were skipped):");
            warnings.forEach(w -> System.out.println("  - " + w));
        }
        System.out.println("\nAll passwords were stored as BCrypt hashes. You can now log in with the old passwords.\n");

        System.exit(SpringApplication.exit(context, () -> 0));
    }

    // ------------------------------------------------------------------ tables

    private void importCategories() {
        int added = 0, total = 0;
        for (String[] p : read("courseCategories.txt", 2)) {
            total++;
            String name = join(p, 1, p.length);
            added += jdbc.update("INSERT INTO course_categories (category_id, category_name) VALUES (?, ?) "
                    + "ON CONFLICT DO NOTHING", p[0], name);
            categories.add(p[0]);
        }
        done("course_categories", added, total);
    }

    private void importCourses() {
        int added = 0, total = 0;
        for (String[] p : read("courses.txt", 7)) {
            total++;
            if (!categories.contains(p[6])) { warn("courses.txt: course " + p[0] + " has unknown category " + p[6]); continue; }
            added += jdbc.update("INSERT INTO courses (course_id, course_name, credits, description, max_students, "
                            + "enrolled_students, category_id) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], Integer.parseInt(p[2]), p[3], Integer.parseInt(p[4]), Integer.parseInt(p[5]), p[6]);
            courses.add(p[0]);
        }
        done("courses", added, total);
    }

    private void importRooms() {
        int added = 0, total = 0;
        for (String[] p : read("rooms.txt", 4)) {
            total++;
            added += jdbc.update("INSERT INTO rooms (room_id, room_name, capacity, available) VALUES (?, ?, ?, ?) "
                    + "ON CONFLICT DO NOTHING", p[0], p[1], Integer.parseInt(p[2]), Boolean.parseBoolean(p[3]));
            rooms.add(p[0]);
        }
        done("rooms", added, total);
    }

    private void importStudents() {
        int added = 0, total = 0;
        for (String[] p : read("students.txt", 9)) {
            total++;
            added += jdbc.update("INSERT INTO students (student_id, name, email, phone, address, password, semester, "
                            + "enrollment_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS DATE), ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], p[2], p[3], p[4], passwordHasher.hashIfNeeded(p[5]), Integer.parseInt(p[6]), p[7], p[8]);
            students.add(p[0]);
        }
        done("students", added, total);
    }

    private void importLecturers() {
        int added = 0, total = 0;
        for (String[] p : read("lecturers.txt", 8)) {
            total++;
            added += jdbc.update("INSERT INTO lecturers (lecturer_id, name, email, phone, address, password, department, "
                            + "specialization) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], p[2], p[3], p[4], passwordHasher.hashIfNeeded(p[5]), p[6], join(p, 7, p.length));
            lecturers.add(p[0]);
        }
        done("lecturers", added, total);
    }

    private void importAdmins() {
        int added = 0, total = 0;
        for (String[] p : read("admins.txt", 7)) {
            total++;
            added += jdbc.update("INSERT INTO admins (admin_id, name, email, phone, address, password, access_level) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], p[2], p[3], p[4], passwordHasher.hashIfNeeded(p[5]), Integer.parseInt(p[6]));
        }
        done("admins", added, total);
    }

    private void importRegistrations() {
        int added = 0, total = 0;
        for (String[] p : read("registrations.txt", 5)) {
            total++;
            if (!students.contains(p[1]) || !courses.contains(p[2])) {
                warn("registrations.txt: " + p[0] + " points to a deleted student or course"); continue;
            }
            added += jdbc.update("INSERT INTO registrations (registration_id, student_id, course_id, registration_date, "
                    + "status) VALUES (?, ?, ?, CAST(? AS DATE), ?) ON CONFLICT DO NOTHING", p[0], p[1], p[2], p[3], p[4]);
        }
        done("registrations", added, total);
    }

    private void importSchedules() {
        int added = 0, total = 0;
        for (String[] p : read("schedules.txt", 7)) {
            total++;
            if (!courses.contains(p[1]) || !lecturers.contains(p[2]) || !rooms.contains(p[6])) {
                warn("schedules.txt: " + p[0] + " points to a deleted course, lecturer or room"); continue;
            }
            added += jdbc.update("INSERT INTO schedules (schedule_id, course_id, lecturer_id, day, start_time, end_time, "
                            + "room_id) VALUES (?, ?, ?, ?, CAST(? AS TIME), CAST(? AS TIME), ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
        }
        done("schedules", added, total);
    }

    private void importPayments() {
        int added = 0, total = 0;
        for (String[] p : read("payments.txt", 7)) {
            total++;
            if (!students.contains(p[1])) { warn("payments.txt: " + p[0] + " points to a deleted student"); continue; }
            String receipt = nullIfEmpty(join(p, 6, p.length));
            added += jdbc.update("INSERT INTO payments (payment_id, student_id, amount, payment_date, method, status, "
                            + "receipt_file_name) VALUES (?, ?, ?, CAST(? AS DATE), ?, ?, ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], Double.parseDouble(p[2]), p[3], p[4], p[5], receipt);
            importFile("payments", receipt);
        }
        done("payments", added, total);
    }

    private void importFeedbacks() {
        int added = 0, total = 0;
        for (String[] p : read("feedbacks.txt", 7)) {
            total++;
            if (!students.contains(p[1]) || !courses.contains(p[2])) {
                warn("feedbacks.txt: " + p[0] + " points to a deleted student or course"); continue;
            }
            int n = p.length;
            // The comment is everything between the rating and the last two fields (date, submitted),
            // so comments that contain commas are kept whole.
            String comment = join(p, 4, n - 2);
            added += jdbc.update("INSERT INTO feedbacks (feedback_id, student_id, course_id, rating, comment, "
                            + "feedback_date, submitted) VALUES (?, ?, ?, ?, ?, CAST(? AS DATE), ?) ON CONFLICT DO NOTHING",
                    p[0], p[1], p[2], Integer.parseInt(p[3]), comment, p[n - 2], Boolean.parseBoolean(p[n - 1]));
        }
        done("feedbacks", added, total);
    }

    private void importMaterials() {
        int added = 0, total = 0;
        for (String[] p : read("courseMaterials.txt", 5)) {
            total++;
            if (!courses.contains(p[3])) { warn("courseMaterials.txt: " + p[0] + " points to a deleted course"); continue; }
            String fileName = join(p, 4, p.length);
            added += jdbc.update("INSERT INTO course_materials (material_id, title, file_type, course_id, file_name) "
                    + "VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING", p[0], p[1], p[2], p[3], fileName);
            importFile("materials", nullIfEmpty(fileName));
        }
        done("course_materials", added, total);
    }

    /** The old app never re-read this number, so count the approved registrations instead. */
    private void recalculateEnrolledCounts() {
        jdbc.update("UPDATE courses c SET enrolled_students = (SELECT COUNT(*) FROM registrations r "
                + "WHERE r.course_id = c.course_id AND r.status = 'APPROVED')");
        report.add("courses.enrolled_students recalculated from approved registrations");
    }

    // ------------------------------------------------------------------ files

    private int filesImported = 0;

    private void importFile(String folder, String fileName) {
        if (fileName == null) return;
        Path file = uploadsDir.resolve(folder).resolve(fileName);
        if (!Files.isRegularFile(file)) {
            warn("file not found: " + file + " (the row was imported, but the file can't be opened)");
            return;
        }
        try {
            String type = Files.probeContentType(file);
            int added = jdbc.update("INSERT INTO uploaded_files (folder, file_name, content_type, data) "
                    + "VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING", folder, fileName, type, Files.readAllBytes(file));
            filesImported += added;
            if (added > 0) report.add("uploaded_files: + " + folder + "/" + fileName);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
    }

    // ------------------------------------------------------------------ helpers

    /** Reads a .txt file and splits each line on commas, skipping blank/short lines. */
    private List<String[]> read(String fileName, int minFields) {
        Path file = dataDir.resolve(fileName);
        List<String[]> rows = new ArrayList<>();
        if (!Files.exists(file)) {
            warn(fileName + " not found, skipped");
            return rows;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < minFields) {
                    warn(fileName + ": line has too few fields: " + line);
                    continue;
                }
                rows.add(Arrays.stream(parts).map(String::trim).toArray(String[]::new));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + file, e);
        }
        return rows;
    }

    private static String join(String[] parts, int from, int to) {
        return String.join(",", Arrays.copyOfRange(parts, from, Math.max(from, to)));
    }

    private static String nullIfEmpty(String s) {
        return (s == null || s.isEmpty() || s.equals("null")) ? null : s;
    }

    private void done(String table, int added, int total) {
        report.add(String.format("%-18s %d new of %d in file", table + ":", added, total));
    }

    private void warn(String message) {
        warnings.add(message);
    }
}
