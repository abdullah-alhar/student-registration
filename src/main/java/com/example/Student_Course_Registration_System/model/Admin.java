package com.example.Student_Course_Registration_System.model;

import com.example.Student_Course_Registration_System.enums.Role;

public class Admin extends Person {

    private String adminId;
    private int accessLevel;

    public Admin(String name, String email, String phone, String address, String password, String adminId, int accessLevel) {
        super(name, email, phone, address, password, Role.ADMIN);
        this.adminId = adminId;
        this.accessLevel = accessLevel;
    }

    public String getAdminId() { return adminId; }
    public int getAccessLevel() { return accessLevel; }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public void manageUsers() {
        System.out.println("Admin " + name + " is managing users");
    }

    @Override
    public void getDetails() {
        System.out.println("Admin ID: " + adminId);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Access Level: " + accessLevel);
    }
}