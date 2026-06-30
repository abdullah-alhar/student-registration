package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.enums.PaymentMethod;
import com.example.Student_Course_Registration_System.enums.PaymentStatus;
import com.example.Student_Course_Registration_System.model.Payment;
import com.example.Student_Course_Registration_System.model.Student;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {

    private static final String FILE_PATH = "src/main/resources/data/payments.txt";
    private static final String UPLOAD_PATH = "uploads/payments/";
    private final StudentRepository studentRepository = new StudentRepository();

    // Save uploaded PDF to uploads/payments/ folder
    public String saveUploadedFile(MultipartFile file) {
        try {
            // Create uploads/payments/ folder if not exists
            File uploadDir = new File(UPLOAD_PATH).getAbsoluteFile();
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file with timestamp to avoid duplicates
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir, fileName);
            file.transferTo(destination);
            return fileName;

        } catch (IOException e) {
            System.out.println("Error uploading payment receipt: " + e.getMessage());
            return null;
        }
    }

    // Save payment to txt file
    public void save(Payment payment) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(payment.getPaymentId() + "," +
                    payment.getStudent().getStudentId() + "," +
                    payment.getAmountForReceipt() + "," +
                    payment.getPaymentDate() + "," +
                    payment.getMethod() + "," +
                    payment.getStatus() + "," +
                    payment.getReceiptFileName());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving payment: " + e.getMessage());
        }
    }

    // Find all payments from txt file
    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Student student = studentRepository.findById(parts[1]);
                    // Skip payments whose student has been deleted
                    if (student == null) {
                        continue;
                    }
                    Payment payment = new Payment(
                            parts[0],                              // paymentId
                            student,                               // student object
                            Double.parseDouble(parts[2]),          // amount
                            parts[3],                              // paymentDate
                            PaymentMethod.valueOf(parts[4]),       // method
                            parts[6]                               // receiptFileName
                    );
                    // Restore status
                    if (parts[5].equals("COMPLETED")) {
                        payment.processPayment();
                    } else if (parts[5].equals("FAILED")) {
                        payment.cancelPayment();
                    }
                    payments.add(payment);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading payments: " + e.getMessage());
        }
        return payments;
    }

    // Find payment by ID
    public Payment findById(String paymentId) {
        List<Payment> payments = findAll();
        for (Payment payment : payments) {
            if (payment.getPaymentId().equals(paymentId)) {
                return payment;
            }
        }
        return null;
    }

    // Find all payments by student ID
    public List<Payment> findByStudentId(String studentId) {
        List<Payment> payments = findAll();
        List<Payment> studentPayments = new ArrayList<>();
        for (Payment payment : payments) {
            if (payment.getStudent().getStudentId().equals(studentId)) {
                studentPayments.add(payment);
            }
        }
        return studentPayments;
    }

    // Find all pending payments
    public List<Payment> findPendingPayments() {
        List<Payment> payments = findAll();
        List<Payment> pendingPayments = new ArrayList<>();
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.PENDING) {
                pendingPayments.add(payment);
            }
        }
        return pendingPayments;
    }

    // Update payment in txt file
    public void update(Payment updatedPayment) {
        List<Payment> payments = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment payment : payments) {
                if (payment.getPaymentId().equals(updatedPayment.getPaymentId())) {
                    writer.write(updatedPayment.getPaymentId() + "," +
                            updatedPayment.getStudent().getStudentId() + "," +
                            updatedPayment.getAmountForReceipt() + "," +
                            updatedPayment.getPaymentDate() + "," +
                            updatedPayment.getMethod() + "," +
                            updatedPayment.getStatus() + "," +
                            updatedPayment.getReceiptFileName());
                } else {
                    writer.write(payment.getPaymentId() + "," +
                            payment.getStudent().getStudentId() + "," +
                            payment.getAmountForReceipt() + "," +
                            payment.getPaymentDate() + "," +
                            payment.getMethod() + "," +
                            payment.getStatus() + "," +
                            payment.getReceiptFileName());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating payment: " + e.getMessage());
        }
    }

    // Delete payment from txt file
    public void delete(String paymentId) {
        List<Payment> payments = findAll();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Payment payment : payments) {
                if (!payment.getPaymentId().equals(paymentId)) {
                    writer.write(payment.getPaymentId() + "," +
                            payment.getStudent().getStudentId() + "," +
                            payment.getAmountForReceipt() + "," +
                            payment.getPaymentDate() + "," +
                            payment.getMethod() + "," +
                            payment.getStatus() + "," +
                            payment.getReceiptFileName());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error deleting payment: " + e.getMessage());
        }
    }

    // Delete uploaded receipt from folder
    public void deleteUploadedFile(String fileName) {
        File file = new File(UPLOAD_PATH + fileName);
        if (file.exists()) {
            file.delete();
            System.out.println("Receipt deleted successfully: " + fileName);
        }
    }
}