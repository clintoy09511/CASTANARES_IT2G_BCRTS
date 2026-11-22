package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.ClearanceRequestDao;
import com.scc.dao.PaymentDao;
import com.scc.dao.ResidentDao;
import com.scc.model.ClearanceRequest;
import com.scc.model.Payment;
import com.scc.model.Resident;
import com.scc.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class StaffMenu {
    private final ResidentDao residentDao = new ResidentDao();
    private final ClearanceRequestDao clearanceDao = new ClearanceRequestDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public void show(User currentUser, Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== STAFF MENU ===");
            System.out.println("1. Manage Residents (CRUD)");
            System.out.println("2. Process Clearance Requests");
            System.out.println("3. Record & View Payments");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> manageResidents(currentUser, scanner);
                case "2" -> processClearanceRequests(currentUser, scanner);
                case "3" -> managePayments(currentUser, scanner);
                case "4" -> {
                    activityLogDao.logActivity(currentUser.getUserId(), "LOGOUT",
                            "Staff logged out: " + currentUser.getUsername());
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ==================== 1. RESIDENT CRUD ====================
    private void manageResidents(User currentUser, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== RESIDENT MANAGEMENT (CRUD) ===");
            System.out.println("1. Add Resident");
            System.out.println("2. View All Residents");
            System.out.println("3. Update Resident");
            System.out.println("4. Delete Resident");
            System.out.println("5. Back to Staff Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addResident(currentUser, scanner);
                case "2" -> viewAllResidents();
                case "3" -> updateResident(currentUser, scanner);
                case "4" -> deleteResident(currentUser, scanner);
                case "5" -> managing = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void addResident(User currentUser, Scanner scanner) {
        System.out.println("\n--- ADD RESIDENT ---");
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            System.out.println("First name and last name are required.");
            return;
        }

        System.out.print("Middle Name (optional): ");
        String middleName = scanner.nextLine().trim();

        System.out.print("Birth Date (YYYY-MM-DD): ");
        LocalDate birthDate = parseDateInput(scanner.nextLine().trim());

        System.out.print("Gender (Male/Female/Other): ");
        String gender = scanner.nextLine().trim();

        System.out.print("Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Contact Number: ");
        String contactNumber = scanner.nextLine().trim();

        System.out.print("Civil Status (Single/Married/Widowed/Separated): ");
        String civilStatus = scanner.nextLine().trim();

        Resident resident = new Resident();
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setMiddleName(middleName);
        resident.setBirthDate(birthDate);
        resident.setGender(gender);
        resident.setAddress(address);
        resident.setContactNumber(contactNumber);
        resident.setCivilStatus(civilStatus);
        resident.setStatus("Active");

        if (residentDao.createResident(resident)) {
            activityLogDao.logActivity(currentUser.getUserId(), "CREATE_RESIDENT",
                    "Added resident: " + firstName + " " + lastName + " (ID: " + resident.getResidentId() + ")");
            System.out.println("Resident added successfully (ID: " + resident.getResidentId() + ").");
        } else {
            System.out.println("Failed to add resident.");
        }
    }

    private void viewAllResidents() {
        List<Resident> residents = residentDao.getAllResidents();
        System.out.println("\n--- ALL RESIDENTS ---");
        if (residents.isEmpty()) {
            System.out.println("No residents found.");
            return;
        }
        System.out.printf("%-5s | %-25s | %-12s | %-8s | %-15s | %-10s%n",
                "ID", "Full Name", "Birth Date", "Gender", "Contact", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        for (Resident r : residents) {
            String fullName = r.getLastName() + ", " + r.getFirstName() +
                    (r.getMiddleName() != null && !r.getMiddleName().isEmpty() ? " " + r.getMiddleName() : "");
            System.out.printf("%-5d | %-25s | %-12s | %-8s | %-15s | %-10s%n",
                    r.getResidentId(),
                    fullName,
                    r.getBirthDate() != null ? r.getBirthDate().toString() : "N/A",
                    r.getGender() != null ? r.getGender() : "",
                    r.getContactNumber() != null ? r.getContactNumber() : "",
                    r.getStatus());
        }
    }

    private void updateResident(User currentUser, Scanner scanner) {
        System.out.println("\n--- UPDATE RESIDENT ---");
        viewAllResidents();
        System.out.print("Enter Resident ID to update: ");
        int residentId = parseIntInput(scanner.nextLine().trim());
        Resident existing = residentDao.getResidentById(residentId);

        if (existing == null) {
            System.out.println("Resident not found.");
            return;
        }

        System.out.print("First Name (blank to keep '" + existing.getFirstName() + "'): ");
        String firstName = scanner.nextLine().trim();
        if (!firstName.isEmpty()) existing.setFirstName(firstName);

        System.out.print("Last Name (blank to keep '" + existing.getLastName() + "'): ");
        String lastName = scanner.nextLine().trim();
        if (!lastName.isEmpty()) existing.setLastName(lastName);

        System.out.print("Address (blank to keep '" + existing.getAddress() + "'): ");
        String address = scanner.nextLine().trim();
        if (!address.isEmpty()) existing.setAddress(address);

        System.out.print("Contact Number (blank to keep '" + existing.getContactNumber() + "'): ");
        String contact = scanner.nextLine().trim();
        if (!contact.isEmpty()) existing.setContactNumber(contact);

        System.out.print("Civil Status (blank to keep '" + existing.getCivilStatus() + "'): ");
        String civilStatus = scanner.nextLine().trim();
        if (!civilStatus.isEmpty()) existing.setCivilStatus(civilStatus);

        System.out.print("Status [Active/Inactive] (blank to keep '" + existing.getStatus() + "'): ");
        String status = scanner.nextLine().trim();
        if (!status.isEmpty()) existing.setStatus(status);

        if (residentDao.updateResident(existing)) {
            activityLogDao.logActivity(currentUser.getUserId(), "UPDATE_RESIDENT",
                    "Updated resident ID " + residentId);
            System.out.println("Resident updated successfully.");
        } else {
            System.out.println("Failed to update resident.");
        }
    }

    private void deleteResident(User currentUser, Scanner scanner) {
        System.out.println("\n--- DELETE RESIDENT ---");
        viewAllResidents();
        System.out.print("Enter Resident ID to delete: ");
        int residentId = parseIntInput(scanner.nextLine().trim());
        Resident existing = residentDao.getResidentById(residentId);

        if (existing == null) {
            System.out.println("Resident not found.");
            return;
        }

        System.out.print("Confirm delete resident '" + existing.getFirstName() + " " + existing.getLastName() + "'? (Y/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        if (residentDao.deleteResident(residentId)) {
            activityLogDao.logActivity(currentUser.getUserId(), "DELETE_RESIDENT",
                    "Deleted resident ID " + residentId);
            System.out.println("Resident deleted successfully.");
        } else {
            System.out.println("Failed to delete resident (resident may have existing clearance requests).");
        }
    }

    // ==================== 2. CLEARANCE REQUESTS ====================
    private void processClearanceRequests(User currentUser, Scanner scanner) {
        boolean processing = true;
        while (processing) {
            System.out.println("\n=== CLEARANCE REQUESTS ===");
            System.out.println("1. View All Clearance Requests");
            System.out.println("2. Approve / Reject Request");
            System.out.println("3. Back to Staff Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewAllClearanceRequests();
                case "2" -> updateClearanceStatus(currentUser, scanner);
                case "3" -> processing = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllClearanceRequests() {
        List<ClearanceRequest> list = clearanceDao.getAllRequests();
        System.out.println("\n--- CLEARANCE REQUESTS ---");
        if (list.isEmpty()) {
            System.out.println("No clearance requests found.");
            return;
        }
        System.out.printf("%-5s | %-11s | %-20s | %-25s | %-12s | %-12s%n",
                "ID", "Resident ID", "Request Date", "Purpose", "Status", "Processed By");
        System.out.println("------------------------------------------------------------------------------------------------");
        for (ClearanceRequest cr : list) {
            System.out.printf("%-5d | %-11d | %-20s | %-25s | %-12s | %-12s%n",
                    cr.getClearanceId(),
                    cr.getResidentId(),
                    cr.getRequestDate() != null ? cr.getRequestDate().toString() : "N/A",
                    cr.getPurpose(),
                    cr.getStatus(),
                    cr.getProcessedBy() != null ? String.valueOf(cr.getProcessedBy()) : "Unassigned");
        }
    }

    private void updateClearanceStatus(User currentUser, Scanner scanner) {
        viewAllClearanceRequests();
        System.out.print("Enter Clearance ID to process: ");
        int clearanceId = parseIntInput(scanner.nextLine().trim());
        ClearanceRequest req = clearanceDao.getRequestById(clearanceId);

        if (req == null) {
            System.out.println("Clearance request not found.");
            return;
        }

        System.out.print("Set new status (Approved / Rejected / Released): ");
        String newStatus = scanner.nextLine().trim();
        if (newStatus.isEmpty()) {
            System.out.println("Status cannot be empty.");
            return;
        }

        if (clearanceDao.updateRequestStatus(clearanceId, newStatus, currentUser.getUserId())) {
            activityLogDao.logActivity(currentUser.getUserId(), "PROCESS_CLEARANCE",
                    "Updated Clearance ID " + clearanceId + " to '" + newStatus + "'");
            System.out.println("Clearance request updated successfully.");
        } else {
            System.out.println("Failed to update clearance request.");
        }
    }

    // ==================== 3. PAYMENTS ====================
    private void managePayments(User currentUser, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== PAYMENT MANAGEMENT ===");
            System.out.println("1. Record New Payment");
            System.out.println("2. View All Payments");
            System.out.println("3. Back to Staff Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> recordPayment(currentUser, scanner);
                case "2" -> viewAllPayments();
                case "3" -> managing = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void recordPayment(User currentUser, Scanner scanner) {
        System.out.println("\n--- RECORD PAYMENT ---");
        viewAllClearanceRequests();
        System.out.print("Enter Clearance ID: ");
        int clearanceId = parseIntInput(scanner.nextLine().trim());
        if (clearanceDao.getRequestById(clearanceId) == null) {
            System.out.println("Clearance ID does not exist.");
            return;
        }

        System.out.print("Enter Amount: ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        Payment payment = new Payment();
        payment.setClearanceId(clearanceId);
        payment.setAmount(amount);
        payment.setPaymentStatus("Paid");
        payment.setRecordedBy(currentUser.getUserId());

        if (paymentDao.createPayment(payment)) {
            activityLogDao.logActivity(currentUser.getUserId(), "RECORD_PAYMENT",
                    "Recorded payment of " + amount + " for Clearance ID " + clearanceId);
            System.out.println("Payment recorded successfully (Payment ID: " + payment.getPaymentId() + ").");
        } else {
            System.out.println("Failed to record payment.");
        }
    }

    private void viewAllPayments() {
        List<Payment> payments = paymentDao.getAllPayments();
        System.out.println("\n--- ALL PAYMENTS ---");
        if (payments.isEmpty()) {
            System.out.println("No payment records found.");
            return;
        }
        System.out.printf("%-5s | %-12s | %-10s | %-20s | %-10s | %-11s%n",
                "ID", "Clearance ID", "Amount", "Payment Date", "Status", "Recorded By");
        System.out.println("---------------------------------------------------------------------------------");
        for (Payment p : payments) {
            System.out.printf("%-5d | %-12d | %-10.2f | %-20s | %-10s | %-11s%n",
                    p.getPaymentId(),
                    p.getClearanceId(),
                    p.getAmount(),
                    p.getPaymentDate() != null ? p.getPaymentDate().toString() : "N/A",
                    p.getPaymentStatus(),
                    p.getRecordedBy() != null ? String.valueOf(p.getRecordedBy()) : "N/A");
        }
    }

    // ==================== HELPERS ====================
    private int parseIntInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private LocalDate parseDateInput(String input) {
        if (input == null || input.isEmpty()) return null;
        try {
            return LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format (expected YYYY-MM-DD). Saving birth date as null.");
            return null;
        }
    }
}