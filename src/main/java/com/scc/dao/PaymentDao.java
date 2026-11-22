package com.scc.dao;

import com.scc.model.Payment;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDao {

    public boolean createPayment(Payment payment) {
        String sql = "INSERT INTO Payments (clearance_id, amount, payment_status, recorded_by) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, payment.getClearanceId());
            pstmt.setDouble(2, payment.getAmount().doubleValue());
            pstmt.setString(3, payment.getPaymentStatus() != null ? payment.getPaymentStatus() : "Paid");
            if (payment.getRecordedBy() != null) {
                pstmt.setInt(4, payment.getRecordedBy());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error recording payment: " + e.getMessage());
        }
        return false;
    }

    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payments ORDER BY payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payments: " + e.getMessage());
        }
        return payments;
    }

    public List<Payment> getPaymentsByClearanceId(int clearanceId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payments WHERE clearance_id = ? ORDER BY payment_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clearanceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payments by clearance ID: " + e.getMessage());
        }
        return payments;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        String dateStr = rs.getString("payment_date");
        LocalDateTime paymentDate = (dateStr != null && !dateStr.isBlank())
                ? LocalDateTime.parse(dateStr.replace(" ", "T"))
                : null;

        int recBy = rs.getInt("recorded_by");
        Integer recordedBy = rs.wasNull() ? null : recBy;

        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("clearance_id"),
                BigDecimal.valueOf(rs.getDouble("amount")),
                paymentDate,
                rs.getString("payment_status"),
                recordedBy
        );
    }
}