package com.scc.dao;

import com.scc.model.Resident;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResidentDao {

    public boolean createResident(Resident resident) {
        String sql = """
                INSERT INTO Residents (first_name, last_name, middle_name, birth_date, gender, address, contact_number, civil_status, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, resident.getFirstName());
            pstmt.setString(2, resident.getLastName());
            pstmt.setString(3, resident.getMiddleName());
            pstmt.setString(4, resident.getBirthDate() != null ? resident.getBirthDate().toString() : null);
            pstmt.setString(5, resident.getGender());
            pstmt.setString(6, resident.getAddress());
            pstmt.setString(7, resident.getContactNumber());
            pstmt.setString(8, resident.getCivilStatus());
            pstmt.setString(9, resident.getStatus() != null ? resident.getStatus() : "Active");

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        resident.setResidentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating resident: " + e.getMessage());
        }
        return false;
    }

    public List<Resident> getAllResidents() {
        List<Resident> residents = new ArrayList<>();
        String sql = "SELECT * FROM Residents ORDER BY resident_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                residents.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching residents: " + e.getMessage());
        }
        return residents;
    }

    public Resident getResidentById(int residentId) {
        String sql = "SELECT * FROM Residents WHERE resident_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, residentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching resident by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateResident(Resident resident) {
        String sql = """
                UPDATE Residents
                SET first_name = ?, last_name = ?, middle_name = ?, birth_date = ?,
                    gender = ?, address = ?, contact_number = ?, civil_status = ?, status = ?
                WHERE resident_id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, resident.getFirstName());
            pstmt.setString(2, resident.getLastName());
            pstmt.setString(3, resident.getMiddleName());
            pstmt.setString(4, resident.getBirthDate() != null ? resident.getBirthDate().toString() : null);
            pstmt.setString(5, resident.getGender());
            pstmt.setString(6, resident.getAddress());
            pstmt.setString(7, resident.getContactNumber());
            pstmt.setString(8, resident.getCivilStatus());
            pstmt.setString(9, resident.getStatus());
            pstmt.setInt(10, resident.getResidentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating resident: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteResident(int residentId) {
        String sql = "DELETE FROM Residents WHERE resident_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, residentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting resident: " + e.getMessage());
        }
        return false;
    }

    private Resident mapRow(ResultSet rs) throws SQLException {
        String birthStr = rs.getString("birth_date");
        LocalDate birthDate = (birthStr != null && !birthStr.isBlank()) ? LocalDate.parse(birthStr) : null;

        return new Resident(
                rs.getInt("resident_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("middle_name"),
                birthDate,
                rs.getString("gender"),
                rs.getString("address"),
                rs.getString("contact_number"),
                rs.getString("civil_status"),
                rs.getString("status")
        );
    }
}