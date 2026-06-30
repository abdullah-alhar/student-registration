package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepository {

    private static final String FILE_PATH = "src/main/resources/data/students.txt";

    // Auto generate student ID
    public String generateId() {
        List<Student> students = findAll();
        int max = 0;
        for (Student s : students) {
            try {
                int num = Integer.parseInt(s.getStudentId().replace("S", ""));
                if (num > max) max = num;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("S%03d", max + 1);
    }

    // Save student to txt file
    public void save(Student student) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(student.getStudentId() + "," +
                    student.getName() + "," +
                    student.getEmail() + "," +
                    student.getPhone() + "," +
                    student.getAddress() + "," +
                    student.getPassword() + "," +
                    student.getSemester() + "," +
                    student.getEnrollmentDate() + "," +
                    student.getStatus());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }

    // Find all students from txt file
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    Student student = new Student(
                            parts[1], // name
                            parts[2], // email
                            parts[3], // phone
                            parts[4], // address
                            parts[5], // password
                            parts[0], // studentId
                            parts[7], // enrollmentDate
                            Integer.parseInt(parts[6]) // semester
                    );
                    student.setStatus(parts[8]);
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading students: " + e.getMessage());
        }
        return students;
    }

    // Find student by ID
    public Student findById(String studentId) {
        for (Student student : findAll()) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    // Update student in txt file
    public void update(Student updatedStudent) {
        List<Student> students = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Student student : students) {
                if (student.getStudentId().equals(updatedStudent.getStudentId())) {
                    writer.write(updatedStudent.getStudentId() + "," +
                            updatedStudent.getName() + "," +
                            updatedStudent.getEmail() + "," +
                            updatedStudent.getPhone() + "," +
                            updatedStudent.getAddress() + "," +
                            updatedStudent.getPassword() + "," +
                            updatedStudent.getSemester() + "," +
                            updatedStudent.getEnrollmentDate() + "," +
                            updatedStudent.getStatus());
                } else {
                    writer.write(student.getStudentId() + "," +
                            student.getName() + "," +
                            student.getEmail() + "," +
                            student.getPhone() + "," +
                            student.getAddress() + "," +
                            student.getPassword() + "," +
                            student.getSemester() + "," +
                            student.getEnrollmentDate() + "," +
                            student.getStatus());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    // Delete student from txt file
    public void delete(String studentId) {
        List<Student> students = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Student student : students) {
                if (!student.getStudentId().equals(studentId)) {
                    writer.write(student.getStudentId() + "," +
                            student.getName() + "," +
                            student.getEmail() + "," +
                            student.getPhone() + "," +
                            student.getAddress() + "," +
                            student.getPassword() + "," +
                            student.getSemester() + "," +
                            student.getEnrollmentDate() + "," +
                            student.getStatus());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }
}