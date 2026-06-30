package com.example.Student_Course_Registration_System.service;

import com.example.Student_Course_Registration_System.enums.PaymentStatus;
import com.example.Student_Course_Registration_System.model.Payment;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    // Add new payment with receipt upload
    public void addPayment(Student student, double amount,
                           String method, MultipartFile receiptFile) {
        // Check if file is not empty
        if (receiptFile == null || receiptFile.isEmpty()) {
            System.out.println("Please upload payment receipt");
            return;
        }
        // Check file type is PDF only
        String originalFileName = receiptFile.getOriginalFilename();
        if (originalFileName == null || !originalFileName.toLowerCase().endsWith(".pdf")) {
            System.out.println("Only PDF files are allowed for payment receipt");
            return;
        }
        // Check amount is valid
        if (amount <= 0) {
            System.out.println("Invalid payment amount");
            return;
        }
        // Save uploaded PDF
        String savedFileName = paymentRepository.saveUploadedFile(receiptFile);
        if (savedFileName == null) {
            System.out.println("Error uploading receipt");
            return;
        }
        // Create payment object
        String paymentId = "PAY" + System.currentTimeMillis();
        String date = LocalDate.now().toString();
        Payment payment = new Payment(
                paymentId,
                student,
                amount,
                date,
                com.example.Student_Course_Registration_System.enums.PaymentMethod.valueOf(method),
                savedFileName
        );
        paymentRepository.save(payment);
        System.out.println("Payment added successfully");
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Payment getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found");
        }
        return payment;
    }

    // Get payments by student ID
    public List<Payment> getPaymentsByStudentId(String studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    // Get all pending payments
    public List<Payment> getPendingPayments() {
        return paymentRepository.findPendingPayments();
    }

    // Approve payment
    public void approvePayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found");
            return;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            System.out.println("Payment is not pending");
            return;
        }
        payment.processPayment();
        paymentRepository.update(payment);
        System.out.println("Payment approved successfully");
    }

    // Cancel payment
    public void cancelPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found");
            return;
        }
        if (payment.getStatus() != PaymentStatus.PENDING) {
            System.out.println("Payment is not pending");
            return;
        }
        payment.cancelPayment();
        paymentRepository.update(payment);
        System.out.println("Payment cancelled successfully");
    }

    // Delete payment
    public void deletePayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId);
        if (payment == null) {
            System.out.println("Payment not found");
            return;
        }
        // Delete receipt file from folder
        if (payment.getReceiptFileName() != null &&
                !payment.getReceiptFileName().isEmpty()) {
            paymentRepository.deleteUploadedFile(payment.getReceiptFileName());
        }
        paymentRepository.delete(paymentId);
        System.out.println("Payment deleted successfully");
    }

    // Get total payment count
    public int getTotalPayments() {
        return paymentRepository.findAll().size();
    }

    // Get total completed payments count
    public List<Payment> getCompletedPayments() {
        List<Payment> payments = paymentRepository.findAll();
        List<Payment> completed = new ArrayList<>();
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                completed.add(payment);
            }
        }
        return completed;
    }
}