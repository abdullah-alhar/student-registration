package com.example.Student_Course_Registration_System.model;

import com.example.Student_Course_Registration_System.enums.Role;

public class Student extends Person {

    private String studentId;
    private String enrollmentDate;
    private int semester;
    private String status;

    public Student(String name, String email, String phone, String address, String password, String studentId, String enrollmentDate, int semester) {
        super(name, email, phone, address, password, Role.STUDENT);
        this.studentId = studentId;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.status = "ACTIVE";
    }

    public String getStudentId() { return studentId; }
    public String getEnrollmentDate() { return enrollmentDate; }
    public int getSemester() { return semester; }
    public String getStatus() { return status; }

    // Setters only for changeable variable
    public void setSemester(int semester) { this.semester = semester; }
    public void setStatus(String status) { this.status = status; }

    public void updateProfile(String phone, String address) {
        setPhone(phone);
        setAddress(address);
        System.out.println("Profile updated successfully");
    }

    @Override
    public void getDetails() {
        System.out.println("Student ID: " + studentId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Semester: " + semester);
        System.out.println("Status: " + status);
    }
}
