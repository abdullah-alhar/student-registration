package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.Admin;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.AdminRepository;
import com.example.Student_Course_Registration_System.repository.LecturerRepository;
import com.example.Student_Course_Registration_System.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private AdminRepository adminRepository;

    // Generate student ID
    public String generateStudentId() {
        return studentRepository.generateId();
    }

    // Add new student - returns error message or null on success
    public String addStudent(Student student) {
        Student existing = studentRepository.findById(student.getStudentId());
        if (existing != null) {
            return "Student ID already exists";
        }
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            if (s.getEmail().equalsIgnoreCase(student.getEmail())) {
                return "A student with this email already exists";
            }
            if (s.getPhone().equals(student.getPhone())) {
                return "A student with this phone number already exists";
            }
        }
        // Cross-entity email check
        String crossError = checkCrossEntityEmail(student.getEmail(), null);
        if (crossError != null) return crossError;
        studentRepository.save(student);
        return null;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get student by ID
    public Student getStudentById(String studentId) {
        Student student = studentRepository.findById(studentId);
        if (student == null) {
            System.out.println("Student not found");
        }
        return student;
    }

    // Update student - returns error message or null on success
    public String updateStudent(Student student) {
        Student existing = studentRepository.findById(student.getStudentId());
        if (existing == null) {
            return "Student not found";
        }
        // Check duplicate email (exclude self)
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            if (!s.getStudentId().equals(student.getStudentId())) {
                if (s.getEmail().equalsIgnoreCase(student.getEmail())) {
                    return "A student with this email already exists";
                }
                if (s.getPhone().equals(student.getPhone())) {
                    return "A student with this phone number already exists";
                }
            }
        }
        // Cross-entity email check
        String crossError = checkCrossEntityEmail(student.getEmail(), null);
        if (crossError != null) return crossError;
        studentRepository.update(student);
        return null;
    }

    // Delete student
    public void deleteStudent(String studentId) {
        Student existing = studentRepository.findById(studentId);
        if (existing == null) {
            System.out.println("Student not found");
            return;
        }
        studentRepository.delete(studentId);
        System.out.println("Student deleted successfully");
    }

    // Search student by name
    public List<Student> searchByName(String name) {
        List<Student> students = studentRepository.findAll();
        List<Student> result = new java.util.ArrayList<>();
        for (Student student : students) {
            if (student.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(student);
            }
        }
        return result;
    }

    // Login student
    public Student login(String email, String password) {
        List<Student> students = studentRepository.findAll();
        for (Student s : students) {
            if (s.getEmail().equals(email) && s.getPassword().equals(password)) {
                return s;
            }
        }
        return null;
    }

    // Get total student count
    public int getTotalStudents() {
        return studentRepository.findAll().size();
    }

    // Check if email is used by a lecturer or admin
    private String checkCrossEntityEmail(String email, String excludeStudentId) {
        for (Lecturer l : lecturerRepository.findAll()) {
            if (l.getEmail().equalsIgnoreCase(email)) {
                return "This email is already used by a lecturer";
            }
        }
        for (Admin a : adminRepository.findAll()) {
            if (a.getEmail().equalsIgnoreCase(email)) {
                return "This email is already used by an admin";
            }
        }
        return null;
    }
}