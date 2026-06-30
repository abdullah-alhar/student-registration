package com.example.Student_Course_Registration_System.model;

import com.example.Student_Course_Registration_System.enums.PaymentMethod;
import com.example.Student_Course_Registration_System.enums.PaymentStatus;

public class Payment {

    private String paymentId;
    private Student student;
    private double amount;
    private String paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private String receiptFileName;

    //aggregation
    public Payment(String paymentId, Student student, double amount, String paymentDate, PaymentMethod method, String receiptFileName) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.receiptFileName = receiptFileName;
    }

    // Getters for html visible
    public String getPaymentId() {
        return paymentId;
    }
    public Student getStudent() {
        return student;
    }
    public String getPaymentDate() {
        return paymentDate;
    }
    public PaymentMethod getMethod() {
        return method;
    }
    public PaymentStatus getStatus() {
        return status;
    }
    public String getReceiptFileName() {
        return receiptFileName;
    }


    public double getAmountForReceipt() {
        return amount;
    }

    // Status changed only through controlled methods
    public void processPayment() {
        if (status == PaymentStatus.PENDING) {
            this.status = PaymentStatus.COMPLETED;
            System.out.println("Payment processed successfully for " + student.getName());
        } else {
            System.out.println("Payment cannot be processed");
        }
    }

    public void cancelPayment() {
        if (status == PaymentStatus.PENDING) {
            this.status = PaymentStatus.FAILED;
            System.out.println("Payment cancelled for " + student.getName());
        } else {
            System.out.println("Payment cannot be cancelled");
        }
    }

    // Controlled way to save receipt file name
    public void saveReceipt(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            this.receiptFileName = fileName;
            System.out.println("Receipt saved successfully");
        } else {
            System.out.println("Invalid file name");
        }
    }
}