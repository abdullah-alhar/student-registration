# 🎓 Student Course Registration System

> A full-stack web application for managing student course registrations, built with **Spring Boot**, **Thymeleaf**, and **file-based data persistence**.



## 📋 Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [OOP Concepts Used](#-oop-concepts-used)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Usage](#-usage)
- [User Roles](#-user-roles)
- [Data Models](#-data-models)

---

## 📖 About the Project

The **Student Course Registration System** is a web-based platform that streamlines the process of managing students, lecturers, courses, and registrations within an academic institution. It provides a role-based interface where **Admins**, **Students**, and **Lecturers** each have dedicated access and capabilities.

Data is stored using **flat `.txt` files** (no database required), making it lightweight and easy to run locally without any extra setup.

---

## ✨ Features

### 👨‍💼 Admin
- Manage students, lecturers, and admins
- Approve or reject course registrations
- Verify and process student payments
- Manage course categories, rooms, and schedules
- Upload and manage course materials

### 👨‍🎓 Student
- Register for available courses
- View registration status (Pending / Approved / Rejected)
- Make payments and upload payment receipts
- View course schedules and materials
- Update personal profile

### 👨‍🏫 Lecturer
- View assigned courses and student lists
- Access course schedules and rooms
- Upload course materials
- View student feedback

### 🔐 Authentication
- Session-based login system with role-based access control
- Route protection via a custom `AuthInterceptor`
- Secure logout with session invalidation

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 4.0.5 |
| Frontend | Thymeleaf, HTML5, CSS3, JavaScript |
| Build Tool | Apache Maven |
| Data Storage | Flat file (`.txt`) – no database required |
| File Handling | Apache Commons IO |
| Dev Tools | Spring Boot DevTools |

---

## 🧠 OOP Concepts Used

This project is designed to demonstrate core **Object-Oriented Programming** principles:

| Concept | Implementation |
|---|---|
| **Abstraction** | `Person` is an abstract class with `getDetails()` as an abstract method |
| **Inheritance** | `Student`, `Lecturer`, and `Admin` extend `Person` |
| **Encapsulation** | All fields are `private`; exposed only through controlled getters/setters |
| **Polymorphism** | `getDetails()` is overridden in each subclass differently |
| **Composition** | `Course` owns a `CourseCategory`; `Registration` links `Student` and `Course` |

---

## 📁 Project Structure

```
Student-Course-Registration-System/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/Student_Course_Registration_System/
│   │   │   │
│   │   │   ├── StudentCourseRegistrationSystemApplication.java   # Entry point
│   │   │   │
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java                               # MVC & interceptor config
│   │   │   │
│   │   │   ├── interceptor/
│   │   │   │   └── AuthInterceptor.java                         # Session-based auth guard
│   │   │   │
│   │   │   ├── model/                                           # Domain models (OOP)
│   │   │   │   ├── Person.java          (abstract base class)
│   │   │   │   ├── Student.java
│   │   │   │   ├── Lecturer.java
│   │   │   │   ├── Admin.java
│   │   │   │   ├── Course.java
│   │   │   │   ├── CourseCategory.java
│   │   │   │   ├── CourseMaterial.java
│   │   │   │   ├── Registration.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Schedule.java
│   │   │   │   ├── Room.java
│   │   │   │   └── Feedback.java
│   │   │   │
│   │   │   ├── enums/                                           # Status enumerations
│   │   │   │   ├── Role.java            (STUDENT, LECTURER, ADMIN)
│   │   │   │   ├── RegistrationStatus.java (PENDING, APPROVED, REJECTED)
│   │   │   │   ├── PaymentStatus.java   (PENDING, COMPLETED, FAILED)
│   │   │   │   └── PaymentMethod.java
│   │   │   │
│   │   │   ├── repository/                                      # File-based data access layer
│   │   │   │   ├── StudentRepository.java
│   │   │   │   ├── CourseRepository.java
│   │   │   │   ├── LecturerRepository.java
│   │   │   │   ├── AdminRepository.java
│   │   │   │   ├── RegistrationRepository.java
│   │   │   │   ├── PaymentRepository.java
│   │   │   │   ├── ScheduleRepository.java
│   │   │   │   ├── RoomRepository.java
│   │   │   │   ├── FeedbackRepository.java
│   │   │   │   ├── CourseCategoryRepository.java
│   │   │   │   └── CourseMaterialRepository.java
│   │   │   │
│   │   │   ├── service/                                         # Business logic layer
│   │   │   │   ├── StudentService.java
│   │   │   │   ├── CourseService.java
│   │   │   │   ├── LecturerService.java
│   │   │   │   ├── AdminService.java
│   │   │   │   ├── RegistrationService.java
│   │   │   │   ├── PaymentService.java
│   │   │   │   ├── ScheduleService.java
│   │   │   │   ├── RoomService.java
│   │   │   │   ├── FeedbackService.java
│   │   │   │   ├── CourseCategoryService.java
│   │   │   │   └── CourseMaterialService.java
│   │   │   │
│   │   │   └── controller/                                      # HTTP request handlers
│   │   │       ├── StudentController.java
│   │   │       ├── CourseController.java
│   │   │       ├── LecturerController.java
│   │   │       ├── AdminController.java
│   │   │       ├── RegistrationController.java
│   │   │       ├── PaymentController.java
│   │   │       ├── ScheduleController.java
│   │   │       ├── RoomController.java
│   │   │       ├── FeedbackController.java
│   │   │       ├── CourseCategoryController.java
│   │   │       └── CourseMaterialController.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties                           # App configuration
│   │       ├── data/                                            # Flat file storage (.txt)
│   │       │   ├── students.txt
│   │       │   ├── courses.txt
│   │       │   ├── lecturers.txt
│   │       │   ├── admins.txt
│   │       │   ├── registrations.txt
│   │       │   ├── payments.txt
│   │       │   ├── schedules.txt
│   │       │   ├── rooms.txt
│   │       │   ├── feedbacks.txt
│   │       │   ├── courseCategories.txt
│   │       │   └── courseMaterials.txt
│   │       ├── static/
│   │       │   ├── css/style.css
│   │       │   └── js/main.js
│   │       └── templates/                                       # Thymeleaf HTML views
│   │           ├── login.html
│   │           ├── dashboard.html
│   │           ├── student.html
│   │           ├── course.html
│   │           ├── lecturer.html
│   │           ├── admin.html
│   │           ├── registration.html
│   │           ├── payment.html
│   │           ├── schedule.html
│   │           ├── room.html
│   │           ├── feedback.html
│   │           ├── category.html
│   │           ├── material.html
│   │           └── profile.html
│   │
│   └── test/
│       └── java/.../StudentCourseRegistrationSystemApplicationTests.java
│
├── uploads/
│   ├── materials/      # Uploaded course material files
│   └── payments/       # Uploaded payment receipt images
│
├── pom.xml             # Maven project configuration
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- **Java 21** or higher — [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.x** — [Download](https://maven.apache.org/download.cgi) *(or use the included `mvnw` wrapper)*

### Installation & Run

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Student-Course-Registration-System.git
   cd Student-Course-Registration-System
   ```

2. **Build and run the application**
   ```bash
   # Using Maven Wrapper (no Maven installation needed)
   ./mvnw spring-boot:run

   # OR using Maven directly
   mvn spring-boot:run
   ```

3. **Open your browser and visit**
   ```
   http://localhost:8080
   ```

> **Note:** No database setup is needed. Data is stored automatically in `.txt` files under `src/main/resources/data/`.

---

## 💻 Usage

1. Navigate to `http://localhost:8080/login`
2. Log in using your credentials (Admin, Student, or Lecturer)
3. You will be redirected to the **Dashboard** based on your role
4. Use the navigation menu to access the relevant modules

---

## 👥 User Roles

| Role | Access |
|---|---|
| **Admin** | Full access — manage all entities, approve registrations, verify payments |
| **Lecturer** | View assigned courses, schedules, upload materials, view feedback |
| **Student** | Register for courses, make payments, view schedule, update profile |

> Access is enforced via session-based authentication. Unauthenticated users are automatically redirected to the login page.

---

## 🗂 Data Models

| Model | Key Fields |
|---|---|
| `Student` | studentId, name, email, semester, status |
| `Lecturer` | lecturerId, name, email, specialization |
| `Admin` | adminId, name, email, role |
| `Course` | courseId, courseName, credits, maxStudents, enrolledStudents |
| `CourseCategory` | categoryId, categoryName |
| `Registration` | registrationId, student, course, status (PENDING/APPROVED/REJECTED) |
| `Payment` | paymentId, student, amount, method, status (PENDING/COMPLETED/FAILED) |
| `Schedule` | scheduleId, course, lecturer, room, day, time |
| `Room` | roomId, roomName, capacity |
| `CourseMaterial` | materialId, course, fileName, uploadDate |
| `Feedback` | feedbackId, student, course, comment, rating |

---

## 📄 License

This project was developed for academic purposes as part of a university assignment.

---


