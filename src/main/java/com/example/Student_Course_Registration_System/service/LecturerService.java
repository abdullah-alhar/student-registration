package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.model.Admin;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.model.Schedule;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.AdminRepository;
import com.example.Student_Course_Registration_System.repository.LecturerRepository;
import com.example.Student_Course_Registration_System.repository.ScheduleRepository;
import com.example.Student_Course_Registration_System.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    // Generate lecturer ID
    public String generateLecturerId() {
        return lecturerRepository.generateId();
    }

    // Add new lecturer - returns error message or null on success
    public String addLecturer(Lecturer lecturer) {
        Lecturer existing = lecturerRepository.findById(lecturer.getLecturerId());
        if (existing != null) {
            return "Lecturer ID already exists";
        }
        List<Lecturer> lecturers = lecturerRepository.findAll();
        for (Lecturer l : lecturers) {
            if (l.getEmail().equalsIgnoreCase(lecturer.getEmail())) {
                return "A lecturer with this email already exists";
            }
            if (l.getPhone().equals(lecturer.getPhone())) {
                return "A lecturer with this phone number already exists";
            }
        }
        // Cross-entity email check
        String crossError = checkCrossEntityEmail(lecturer.getEmail());
        if (crossError != null) return crossError;
        lecturerRepository.save(lecturer);
        return null;
    }

    // Get all lecturers
    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    // Get lecturer by ID
    public Lecturer getLecturerById(String lecturerId) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId);
        if (lecturer == null) {
            System.out.println("Lecturer not found");
        }
        return lecturer;
    }

    // Update lecturer - returns error message or null on success
    public String updateLecturer(Lecturer lecturer) {
        Lecturer existing = lecturerRepository.findById(lecturer.getLecturerId());
        if (existing == null) {
            return "Lecturer not found";
        }
        // Check duplicate email (exclude self)
        List<Lecturer> lecturers = lecturerRepository.findAll();
        for (Lecturer l : lecturers) {
            if (!l.getLecturerId().equals(lecturer.getLecturerId())) {
                if (l.getEmail().equalsIgnoreCase(lecturer.getEmail())) {
                    return "A lecturer with this email already exists";
                }
                if (l.getPhone().equals(lecturer.getPhone())) {
                    return "A lecturer with this phone number already exists";
                }
            }
        }
        // Cross-entity email check
        String crossError = checkCrossEntityEmail(lecturer.getEmail());
        if (crossError != null) return crossError;
        lecturerRepository.update(lecturer);
        return null;
    }

    // Delete lecturer
    public void deleteLecturer(String lecturerId) {
        Lecturer existing = lecturerRepository.findById(lecturerId);
        if (existing == null) {
            System.out.println("Lecturer not found");
            return;
        }
        // Cascade delete: remove all schedules that reference this lecturer
        List<Schedule> schedules = scheduleRepository.findByLecturerId(lecturerId);
        for (Schedule schedule : schedules) {
            scheduleRepository.delete(schedule.getScheduleId());
        }
        lecturerRepository.delete(lecturerId);
        System.out.println("Lecturer deleted successfully");
    }

    // Search lecturer by name
    public List<Lecturer> searchByName(String name) {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<Lecturer> result = new ArrayList<>();
        for (Lecturer lecturer : lecturers) {
            if (lecturer.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(lecturer);
            }
        }
        return result;
    }

    // Search lecturer by department
    public List<Lecturer> searchByDepartment(String department) {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<Lecturer> result = new ArrayList<>();
        for (Lecturer lecturer : lecturers) {
            if (lecturer.getDepartment().toLowerCase().contains(department.toLowerCase())) {
                result.add(lecturer);
            }
        }
        return result;
    }

    // Login lecturer
    public Lecturer login(String email, String password) {
        List<Lecturer> lecturers = lecturerRepository.findAll();
        for (Lecturer l : lecturers) {
            if (l.getEmail().equals(email) && l.getPassword().equals(password)) {
                return l;
            }
        }
        return null;
    }

    // Get total lecturer count
    public int getTotalLecturers() {
        return lecturerRepository.findAll().size();
    }

    // Check if email is used by a student or admin
    private String checkCrossEntityEmail(String email) {
        for (Student s : studentRepository.findAll()) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                return "This email is already used by a student";
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