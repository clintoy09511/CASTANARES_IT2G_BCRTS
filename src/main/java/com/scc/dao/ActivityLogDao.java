package com.scc.dao;

import com.scc.model.ActivityLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    public void logActivity(int userId, String action, String description) {
        String sql = "INSERT INTO Activity_Logs (user_id, action, description) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.setString(3, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording activity log: " + e.getMessage());
        }
    }

    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM Activity_Logs ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("timestamp");
                logs.add(new ActivityLog(
                        rs.getInt("log_id"),
                        rs.getInt("user_id"),
                        rs.getString("action"),
                        rs.getString("description"),
                        ts != null ? ts.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving activity logs: " + e.getMessage());
        }
        return logs;
    }
}