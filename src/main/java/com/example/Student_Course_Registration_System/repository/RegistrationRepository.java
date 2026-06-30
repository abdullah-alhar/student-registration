package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.enums.RegistrationStatus;
import com.example.Student_Course_Registration_System.model.Course;
import com.example.Student_Course_Registration_System.model.Registration;
import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RegistrationRepository {//cr

    private static final String FILE_PATH = "src/main/resources/data/registrations.txt";
    private final StudentRepository studentRepository = new StudentRepository();
    private final CourseRepository courseRepository = new CourseRepository();

    // create registration to txt file
    public void save(Registration registration) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(registration.getRegistrationId() + "," +
                    registration.getStudent().getStudentId() + "," +
                    registration.getCourse().getCourseId() + "," +
                    registration.getRegistrationDate() + "," +
                    registration.getStatus());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving registration: " + e.getMessage());
        }
    }

    // Find all registrations from txt file
    public List<Registration> findAll() {
        List<Registration> registrations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Student student = studentRepository.findById(parts[1]);
                    Course course = courseRepository.findById(parts[2]);

                    // Skip corrupted records where student or course no longer exists
                    if (student == null || course == null) {
                        System.out.println("Skipping registration " + parts[0] +
                                " — student or course not found (studentId=" + parts[1] +
                                ", courseId=" + parts[2] + ")");
                        continue;
                    }

                    Registration registration = new Registration(
                            parts[0], // registrationId
                            student,  // student object
                            course,   // course object
                            parts[3]  // registrationDate
                    );
                    // Set status back
                    if (parts[4].equals("APPROVED")) {
                        registration.approve();
                    } else if (parts[4].equals("REJECTED")) {
                        registration.reject();
                    }
                    registrations.add(registration);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading registrations: " + e.getMessage());
        }
        return registrations;
    }

    // Find registration by ID
    public Registration findById(String registrationId) {
        List<Registration> registrations = findAll();
        for (Registration registration : registrations) {
            if (registration.getRegistrationId().equals(registrationId)) {
                return registration;
            }
        }
        return null;
    }

    // Find all registrations by student ID
    public List<Registration> findByStudentId(String studentId) {
        List<Registration> registrations = findAll();
        List<Registration> studentRegistrations = new ArrayList<>();
        for (Registration registration : registrations) {
            // Student is guaranteed non-null here because findAll() skips null records
            if (registration.getStudent() != null &&
                    registration.getStudent().getStudentId().equals(studentId)) {
                studentRegistrations.add(registration);
            }
        }
        return studentRegistrations;
    }

    // Update registration in txt file
    public void update(Registration updatedRegistration) {
        List<Registration> registrations = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Registration registration : registrations) {
                if (registration.getRegistrationId().equals(updatedRegistration.getRegistrationId())) {
                    writer.write(updatedRegistration.getRegistrationId() + "," +
                            updatedRegistration.getStudent().getStudentId() + "," +
                            updatedRegistration.getCourse().getCourseId() + "," +
                            updatedRegistration.getRegistrationDate() + "," +
                            updatedRegistration.getStatus());
                } else {
                    writer.write(registration.getRegistrationId() + "," +
                            registration.getStudent().getStudentId() + "," +
                            registration.getCourse().getCourseId() + "," +
                            registration.getRegistrationDate() + "," +
                            registration.getStatus());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating registration: " + e.getMessage());
        }
    }

    // Delete registration from txt file
    public void delete(String registrationId) {
        List<Registration> registrations = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Registration registration : registrations) {
                if (!registration.getRegistrationId().equals(registrationId)) {
                    writer.write(registration.getRegistrationId() + "," +
                            registration.getStudent().getStudentId() + "," +
                            registration.getCourse().getCourseId() + "," +
                            registration.getRegistrationDate() + "," +
                            registration.getStatus());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting registration: " + e.getMessage());
        }
    }
}