package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Lecturer;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LecturerRepository {

    private static final String FILE_PATH = "src/main/resources/data/lecturers.txt";

    // Auto generate lecturer ID
    public String generateId() {
        List<Lecturer> lecturers = findAll();
        int max = 0;
        for (Lecturer l : lecturers) {
            try {
                int num = Integer.parseInt(l.getLecturerId().replace("L", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("L%03d", max + 1);
    }

    // Save lecturer to txt file
    public void save(Lecturer lecturer) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(lecturer.getLecturerId() + "," +
                    lecturer.getName() + "," +
                    lecturer.getEmail() + "," +
                    lecturer.getPhone() + "," +
                    lecturer.getAddress() + "," +
                    lecturer.getPassword() + "," +
                    lecturer.getDepartment() + "," +
                    lecturer.getSpecialization());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving lecturer: " + e.getMessage());
        }
    }

    // Find all lecturers from txt file
    public List<Lecturer> findAll() {
        List<Lecturer> lecturers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    Lecturer lecturer = new Lecturer(
                            parts[1], // name
                            parts[2], // email
                            parts[3], // phone
                            parts[4], // address
                            parts[5], // password
                            parts[0], // lecturerId
                            parts[6], // department
                            parts[7]  // specialization
                    );
                    lecturers.add(lecturer);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading lecturers: " + e.getMessage());
        }
        return lecturers;
    }

    // Find lecturer by ID
    public Lecturer findById(String lecturerId) {
        for (Lecturer lecturer : findAll()) {
            if (lecturer.getLecturerId().equals(lecturerId)) {
                return lecturer;
            }
        }
        return null;
    }

    // Update lecturer in txt file
    public void update(Lecturer updatedLecturer) {
        List<Lecturer> lecturers = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Lecturer lecturer : lecturers) {
                if (lecturer.getLecturerId().equals(updatedLecturer.getLecturerId())) {
                    writer.write(updatedLecturer.getLecturerId() + "," +
                            updatedLecturer.getName() + "," +
                            updatedLecturer.getEmail() + "," +
                            updatedLecturer.getPhone() + "," +
                            updatedLecturer.getAddress() + "," +
                            updatedLecturer.getPassword() + "," +
                            updatedLecturer.getDepartment() + "," +
                            updatedLecturer.getSpecialization());
                } else {
                    writer.write(lecturer.getLecturerId() + "," +
                            lecturer.getName() + "," +
                            lecturer.getEmail() + "," +
                            lecturer.getPhone() + "," +
                            lecturer.getAddress() + "," +
                            lecturer.getPassword() + "," +
                            lecturer.getDepartment() + "," +
                            lecturer.getSpecialization());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating lecturer: " + e.getMessage());
        }
    }

    // Delete lecturer from txt file
    public void delete(String lecturerId) {
        List<Lecturer> lecturers = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Lecturer lecturer : lecturers) {
                if (!lecturer.getLecturerId().equals(lecturerId)) {
                    writer.write(lecturer.getLecturerId() + "," +
                            lecturer.getName() + "," +
                            lecturer.getEmail() + "," +
                            lecturer.getPhone() + "," +
                            lecturer.getAddress() + "," +
                            lecturer.getPassword() + "," +
                            lecturer.getDepartment() + "," +
                            lecturer.getSpecialization());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting lecturer: " + e.getMessage());
        }
    }
}