package com.example.Student_Course_Registration_System.model;

import com.example.Student_Course_Registration_System.enums.Role;

public abstract class Person {

    protected String name;
    protected String email;
    protected String phone;
    protected String address;
    protected String password;
    protected Role role;

    public Person(String name, String email, String phone, String address, String password, Role role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }

    // Setters only for changeable variables
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void changePassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            this.password = newPassword;
            System.out.println("Password changed successfully");
        } else {
            System.out.println("Old password is incorrect");
        }
    }

    public void login() {
        System.out.println(name + " logged in successfully");
    }

    public void logout() {
        System.out.println(name + " logged out successfully");
    }

    public abstract void getDetails();
}