package com.example.Student_Course_Registration_System.repository;

import com.example.Student_Course_Registration_System.enums.PaymentMethod;
import com.example.Student_Course_Registration_System.model.Payment;
import com.example.Student_Course_Registration_System.model.Student;
import com.example.Student_Course_Registration_System.repository.UploadedFileRepository.StoredFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class PaymentRepository {

    private static final String SELECT =
            "SELECT payment_id, student_id, amount, payment_date, method, status, receipt_file_name FROM payments";

    private final JdbcTemplate jdbc;
    private final StudentRepository studentRepository;
    private final UploadedFileRepository fileRepository;

    public PaymentRepository(JdbcTemplate jdbc, StudentRepository studentRepository,
                             UploadedFileRepository fileRepository) {
        this.jdbc = jdbc;
        this.studentRepository = studentRepository;
        this.fileRepository = fileRepository;
    }

    // Save uploaded receipt (stored in the database). Returns the saved file name.
    public String saveUploadedFile(MultipartFile file) {
        return fileRepository.save(UploadedFileRepository.PAYMENTS, file);
    }

    // Load an uploaded receipt, or null if it does not exist
    public StoredFile loadUploadedFile(String fileName) {
        return fileRepository.find(UploadedFileRepository.PAYMENTS, fileName);
    }

    // Delete uploaded receipt
    public void deleteUploadedFile(String fileName) {
        fileRepository.delete(UploadedFileRepository.PAYMENTS, fileName);
    }

    // Save new payment
    public void save(Payment p) {
        jdbc.update("INSERT INTO payments (payment_id, student_id, amount, payment_date, method, status, "
                        + "receipt_file_name) VALUES (?, ?, ?, CAST(? AS DATE), ?, ?, ?)",
                p.getPaymentId(), p.getStudent().getStudentId(), p.getAmountForReceipt(),
                p.getPaymentDate(), p.getMethod().name(), p.getStatus().name(), p.getReceiptFileName());
    }

    // Find all payments
    public List<Payment> findAll() {
        return query(" ORDER BY created_at, payment_id");
    }

    // Find payment by ID
    public Payment findById(String paymentId) {
        return query(" WHERE payment_id = ?", paymentId).stream().findFirst().orElse(null);
    }

    // Find all payments by student ID
    public List<Payment> findByStudentId(String studentId) {
        return query(" WHERE student_id = ? ORDER BY created_at, payment_id", studentId);
    }

    // Find all pending payments
    public List<Payment> findPendingPayments() {
        return query(" WHERE status = 'PENDING' ORDER BY created_at, payment_id");
    }

    // Update payment
    public void update(Payment p) {
        jdbc.update("UPDATE payments SET student_id = ?, amount = ?, payment_date = CAST(? AS DATE), method = ?, "
                        + "status = ?, receipt_file_name = ? WHERE payment_id = ?",
                p.getStudent().getStudentId(), p.getAmountForReceipt(), p.getPaymentDate(),
                p.getMethod().name(), p.getStatus().name(), p.getReceiptFileName(), p.getPaymentId());
    }

    // Delete payment
    public void delete(String paymentId) {
        jdbc.update("DELETE FROM payments WHERE payment_id = ?", paymentId);
    }

    // Runs one SELECT and attaches the Student object to each row.
    private List<Payment> query(String whereAndOrder, Object... args) {
        Map<String, Student> students = RepositoryHelper.byId(studentRepository.findAll(), Student::getStudentId);
        return jdbc.query(SELECT + whereAndOrder, (rs, rowNum) -> {
            Student student = students.get(rs.getString("student_id"));
            if (student == null) return null;   // cannot happen thanks to foreign keys
            Payment payment = new Payment(
                    rs.getString("payment_id"),
                    student,
                    rs.getDouble("amount"),
                    rs.getString("payment_date"),
                    PaymentMethod.valueOf(rs.getString("method")),
                    rs.getString("receipt_file_name"));
            // Restore status (same as the old .txt code)
            String status = rs.getString("status");
            if (status.equals("COMPLETED")) {
                payment.processPayment();
            } else if (status.equals("FAILED")) {
                payment.cancelPayment();
            }
            return payment;
        }, args).stream().filter(Objects::nonNull).toList();
    }
}
