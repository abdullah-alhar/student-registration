package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Admin;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.model.Lecturer;
import com.example.Student_Course_Registration_System.service.AdminService;
import com.example.Student_Course_Registration_System.service.StudentService;
import com.example.Student_Course_Registration_System.service.LecturerService;
import com.example.Student_Course_Registration_System.service.CourseService;
import com.example.Student_Course_Registration_System.service.RegistrationService;
import com.example.Student_Course_Registration_System.service.PaymentService;
import com.example.Student_Course_Registration_System.service.FeedbackService;
import com.example.Student_Course_Registration_System.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private StudentService studentService;
    @Autowired private LecturerService lecturerService;
    @Autowired private CourseService courseService;
    @Autowired private RegistrationService registrationService;
    @Autowired private PaymentService paymentService;
    @Autowired private FeedbackService feedbackService;
    @Autowired private ScheduleService scheduleService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        // Check Admin first with password
        Admin admin = adminService.login(email, password);
        if (admin != null) {
            session.setAttribute("userRole", "ADMIN");
            session.setAttribute("userName", admin.getName());
            session.setAttribute("userId", admin.getAdminId());
            return "redirect:/dashboard";
        }

        // Check Student by email and password
        Student student = studentService.login(email, password);
        if (student != null) {
            session.setAttribute("userRole", "STUDENT");
            session.setAttribute("userName", student.getName());
            session.setAttribute("userId", student.getStudentId());
            return "redirect:/dashboard";
        }

        // Check Lecturer by email and password
        Lecturer lecturer = lecturerService.login(email, password);
        if (lecturer != null) {
            session.setAttribute("userRole", "LECTURER");
            session.setAttribute("userName", lecturer.getName());
            session.setAttribute("userId", lecturer.getLecturerId());
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));

        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");

        if ("ADMIN".equals(role)) {
            model.addAttribute("profile", adminService.getAdminById(userId));
        } else if ("STUDENT".equals(role)) {
            model.addAttribute("profile", studentService.getStudentById(userId));
        } else if ("LECTURER".equals(role)) {
            model.addAttribute("profile", lecturerService.getLecturerById(userId));
        }
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false, defaultValue = "0") int semester,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");
        String error = null;

        if ("ADMIN".equals(role)) {
            Admin existingAdmin = adminService.getAdminById(userId);
            String finalPassword = (password != null && !password.trim().isEmpty()) ? password : existingAdmin.getPassword();
            Admin updated = new Admin(name, email, phone, address, finalPassword, userId, existingAdmin.getAccessLevel());
            error = adminService.updateAdmin(updated);
        } else if ("STUDENT".equals(role)) {
            Student existingStudent = studentService.getStudentById(userId);
            String finalPassword = (password != null && !password.trim().isEmpty()) ? password : existingStudent.getPassword();
            Student updated = new Student(name, email, phone, address, finalPassword, userId,
                    existingStudent.getEnrollmentDate(), semester > 0 ? semester : existingStudent.getSemester());
            updated.setStatus(existingStudent.getStatus());
            error = studentService.updateStudent(updated);
        } else if ("LECTURER".equals(role)) {
            Lecturer existingLecturer = lecturerService.getLecturerById(userId);
            String finalPassword = (password != null && !password.trim().isEmpty()) ? password : existingLecturer.getPassword();
            Lecturer updated = new Lecturer(name, email, phone, address, finalPassword, userId,
                    department != null ? department : existingLecturer.getDepartment(),
                    specialization != null ? specialization : existingLecturer.getSpecialization());
            error = lecturerService.updateLecturer(updated);
        }

        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            session.setAttribute("userName", name);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        }
        return "redirect:/profile";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));

        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");

        if ("ADMIN".equals(role)) {
            model.addAttribute("totalStudents", studentService.getTotalStudents());
            model.addAttribute("totalLecturers", lecturerService.getTotalLecturers());
            model.addAttribute("totalCourses", courseService.getTotalCourses());
            model.addAttribute("totalRegistrations", registrationService.getTotalRegistrations());
            model.addAttribute("pendingRegistrations", registrationService.getPendingRegistrations());
            model.addAttribute("pendingPayments", paymentService.getPendingPayments());
            model.addAttribute("totalFeedbacks", feedbackService.getTotalFeedbacks());
        } else if ("STUDENT".equals(role)) {
            model.addAttribute("myRegistrations", registrationService.getRegistrationsByStudentId(userId));
            model.addAttribute("myPayments", paymentService.getPaymentsByStudentId(userId));
        } else if ("LECTURER".equals(role)) {
            model.addAttribute("mySchedules", scheduleService.getSchedulesByLecturerId(userId));
        }

        return "dashboard";
    }

    @GetMapping("/admins")
    public String getAllAdmins(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin";
    }

    @PostMapping("/admins/add")
    public String addAdmin(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String password,
            @RequestParam int accessLevel,
            RedirectAttributes redirectAttributes) {

        String adminId = "ADM" + System.currentTimeMillis();
        Admin admin = new Admin(name, email, phone, address, password, adminId, accessLevel);
        String error = adminService.addAdmin(admin);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Admin added successfully");
        }
        return "redirect:/admins";
    }

    @GetMapping("/admins/view/{adminId}")
    public String viewAdmin(@PathVariable String adminId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("viewAdmin", adminService.getAdminById(adminId));
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin";
    }

    @GetMapping("/admins/edit/{adminId}")
    public String showEditForm(@PathVariable String adminId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("editAdmin", adminService.getAdminById(adminId));
        model.addAttribute("admins", adminService.getAllAdmins());
        return "admin";
    }

    @PostMapping("/admins/edit/{adminId}")
    public String updateAdmin(
            @PathVariable String adminId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam(required = false) String password,
            @RequestParam int accessLevel,
            RedirectAttributes redirectAttributes) {

        Admin existingAdmin = adminService.getAdminById(adminId);
        String finalPassword = (password != null && !password.trim().isEmpty()) ? password : existingAdmin.getPassword();
        Admin admin = new Admin(name, email, phone, address, finalPassword, adminId, accessLevel);
        String error = adminService.updateAdmin(admin);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "Admin updated successfully");
        }
        return "redirect:/admins";
    }

    @GetMapping("/admins/delete/{adminId}")
    public String deleteAdmin(@PathVariable String adminId) {
        adminService.deleteAdmin(adminId);
        return "redirect:/admins";
    }

    @GetMapping("/admins/search")
    public String searchAdmin(@RequestParam String name, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("admins", adminService.searchByName(name));
        return "admin";
    }
}