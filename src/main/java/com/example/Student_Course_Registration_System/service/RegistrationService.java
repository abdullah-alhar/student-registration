package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.enums.RegistrationStatus;
import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.CourseRepository;
import com.example.Student_Course_Registration_System.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {//logic

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Add new registration
    public void addRegistration(Student student, Course course) {
        // Check if student is already registered for this course
        List<Registration> existing = registrationRepository.findByStudentId(student.getStudentId());
        for (Registration r : existing) {
            if (r.getCourse().getCourseId().equals(course.getCourseId())) {
                System.out.println("Student already registered for this course");
                return;
            }
        }
        // Check if course is available
        if (!course.isAvailable()) {
            System.out.println("Course is full");
            return;
        }
        // Create registration ID
        String registrationId = "REG" + System.currentTimeMillis();
        String date = LocalDate.now().toString();
        Registration registration = new Registration(registrationId, student, course, date);
        registrationRepository.save(registration);
        System.out.println("Registration added successfully");
    }

    // Get all registrations
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    // Get registration by ID
    public Registration getRegistrationById(String registrationId) {
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            System.out.println("Registration not found");
        }
        return registration;
    }

    // Get all registrations by student ID
    public List<Registration> getRegistrationsByStudentId(String studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    // Approve registration
    public void approveRegistration(String registrationId) {
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            System.out.println("Registration not found");
            return;
        }
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            System.out.println("Registration is not pending");
            return;
        }
        registration.approve();
        registrationRepository.update(registration);
        // Update course enrolled count
        Course course = registration.getCourse();
        courseRepository.update(course);
        System.out.println("Registration approved successfully");
    }

    // Reject registration
    public void rejectRegistration(String registrationId) {
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            System.out.println("Registration not found");
            return;
        }
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            System.out.println("Registration is not pending");
            return;
        }
        registration.reject();
        registrationRepository.update(registration);
        System.out.println("Registration rejected successfully");
    }

    // Cancel registration
    public void cancelRegistration(String registrationId) {
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            System.out.println("Registration not found");
            return;
        }
        registration.cancel();
        registrationRepository.update(registration);
        // Update course enrolled count
        Course course = registration.getCourse();
        courseRepository.update(course);
        System.out.println("Registration cancelled successfully");
    }

    // Delete registration
    public void deleteRegistration(String registrationId) {
        Registration registration = registrationRepository.findById(registrationId);
        if (registration == null) {
            System.out.println("Registration not found");
            return;
        }
        registrationRepository.delete(registrationId);
        System.out.println("Registration deleted successfully");
    }

    // Get pending registrations
    public List<Registration> getPendingRegistrations() {
        List<Registration> registrations = registrationRepository.findAll();
        List<Registration> pending = new ArrayList<>();
        for (Registration registration : registrations) {
            if (registration.getStatus() == RegistrationStatus.PENDING) {
                pending.add(registration);
            }
        }
        return pending;
    }

    // Get total registration count
    public int getTotalRegistrations() {
        return registrationRepository.findAll().size();
    }
}