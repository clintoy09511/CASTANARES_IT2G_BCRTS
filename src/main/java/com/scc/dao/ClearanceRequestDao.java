package com.scc.dao;

import com.scc.model.ClearanceRequest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClearanceRequestDao {

    public boolean createRequest(ClearanceRequest request) {
        String sql = "INSERT INTO Clearance_Requests (resident_id, purpose, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, request.getResidentId());
            pstmt.setString(2, request.getPurpose());
            pstmt.setString(3, request.getStatus() != null ? request.getStatus() : "Pending");

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        request.setClearanceId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating clearance request: " + e.getMessage());
        }
        return false;
    }

    public List<ClearanceRequest> getAllRequests() {
        List<ClearanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Clearance_Requests ORDER BY clearance_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching clearance requests: " + e.getMessage());
        }
        return list;
    }

    public List<ClearanceRequest> getRequestsByResidentId(int residentId) {
        List<ClearanceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM Clearance_Requests WHERE resident_id = ? ORDER BY clearance_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, residentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching resident clearance requests: " + e.getMessage());
        }
        return list;
    }

    public ClearanceRequest getRequestById(int clearanceId) {
        String sql = "SELECT * FROM Clearance_Requests WHERE clearance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clearanceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching clearance request by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateRequestStatus(int clearanceId, String status, int processedBy) {
        String sql = "UPDATE Clearance_Requests SET status = ?, processed_by = ? WHERE clearance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, processedBy);
            pstmt.setInt(3, clearanceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating clearance request: " + e.getMessage());
        }
        return false;
    }

    private ClearanceRequest mapRow(ResultSet rs) throws SQLException {
        String reqDateStr = rs.getString("request_date");
        LocalDateTime reqDate = (reqDateStr != null && !reqDateStr.isBlank())
                ? LocalDateTime.parse(reqDateStr.replace(" ", "T"))
                : null;

        int procBy = rs.getInt("processed_by");
        Integer processedBy = rs.wasNull() ? null : procBy;

        return new ClearanceRequest(
                rs.getInt("clearance_id"),
                rs.getInt("resident_id"),
                reqDate,
                rs.getString("purpose"),
                rs.getString("status"),
                processedBy
        );
    }
}