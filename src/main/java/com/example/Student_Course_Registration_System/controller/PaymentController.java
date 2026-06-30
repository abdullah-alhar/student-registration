package com.example.Student_Course_Registration_System.controller;

import com.example.Student_Course_Registration_System.model.Payment;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.service.PaymentService;
import com.example.Student_Course_Registration_System.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private com.example.Student_Course_Registration_System.service.LecturerService lecturerService;

    private static final String UPLOAD_PATH = "uploads/payments/";

    @GetMapping("/payments")
    public String getAllPayments(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));

        String role = (String) session.getAttribute("userRole");
        String userId = (String) session.getAttribute("userId");

        if ("LECTURER".equals(role)) {
            return "redirect:/dashboard";
        } else if ("STUDENT".equals(role)) {
            model.addAttribute("payments", paymentService.getPaymentsByStudentId(userId));
        } else {
            model.addAttribute("payments", paymentService.getAllPayments());
        }
        model.addAttribute("students", studentService.getAllStudents());
        return "payment";
    }

    @PostMapping("/payments/add")
    public String addPayment(
            @RequestParam String studentId,
            @RequestParam double amount,
            @RequestParam String method,
            @RequestParam("receiptFile") MultipartFile receiptFile) {
        Student student = studentService.getStudentById(studentId);
        paymentService.addPayment(student, amount, method, receiptFile);
        return "redirect:/payments";
    }

    @GetMapping("/payments/pending")
    public String getPendingPayments(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("payments", paymentService.getPendingPayments());
        model.addAttribute("students", studentService.getAllStudents());
        return "payment";
    }

    @GetMapping("/payments/completed")
    public String getCompletedPayments(Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("payments", paymentService.getCompletedPayments());
        model.addAttribute("students", studentService.getAllStudents());
        return "payment";
    }

    @GetMapping("/payments/student/{studentId}")
    public String getPaymentsByStudent(@PathVariable String studentId, Model model, HttpSession session) {
        model.addAttribute("userRole", session.getAttribute("userRole"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("payments", paymentService.getPaymentsByStudentId(studentId));
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("students", studentService.getAllStudents());
        return "payment";
    }

    @GetMapping("/payments/approve/{paymentId}")
    public String approvePayment(@PathVariable String paymentId) {
        paymentService.approvePayment(paymentId);
        return "redirect:/payments";
    }

    @GetMapping("/payments/cancel/{paymentId}")
    public String cancelPayment(@PathVariable String paymentId) {
        paymentService.cancelPayment(paymentId);
        return "redirect:/payments";
    }

    @GetMapping("/payments/delete/{paymentId}")
    public String deletePayment(@PathVariable String paymentId) {
        paymentService.deletePayment(paymentId);
        return "redirect:/payments";
    }

    // ✅ FIXED: Added Content-Type header so browser renders PDF correctly instead of raw text
    @GetMapping("/payments/receipt/{paymentId}")
    public ResponseEntity<Resource> viewReceipt(@PathVariable String paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null || payment.getReceiptFileName() == null
                || payment.getReceiptFileName().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = Paths.get(UPLOAD_PATH).toAbsolutePath().resolve(payment.getReceiptFileName());
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detect actual content type from the file (e.g. application/pdf)
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/pdf"; // safe fallback since only PDFs are accepted
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + payment.getReceiptFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}