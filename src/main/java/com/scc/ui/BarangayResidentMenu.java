package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.ClearanceRequestDao;
import com.scc.dao.PaymentDao;
import com.scc.dao.ResidentDao;
import com.scc.model.ClearanceRequest;
import com.scc.model.Payment;
import com.scc.model.Resident;
import com.scc.model.User;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class BarangayResidentMenu {
    private final ResidentDao residentDao = new ResidentDao();
    private final ClearanceRequestDao clearanceDao = new ClearanceRequestDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public void show(User currentUser, Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== BARANGAY RESIDENT MENU ===");
            System.out.println("1. Request Clearance");
            System.out.println("2. View Request & Payment Status");
            System.out.println("3. Register / View Resident Profile");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> requestClearance(currentUser, scanner);
                case "2" -> viewRequestAndPaymentStatus(scanner);
                case "3" -> manageResidentProfile(currentUser, scanner);
                case "4" -> {
                    activityLogDao.logActivity(currentUser.getUserId(), "LOGOUT",
                            "Resident logged out: " + currentUser.getUsername());
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void requestClearance(User currentUser, Scanner scanner) {
        System.out.println("\n--- REQUEST BARANGAY CLEARANCE ---");
        System.out.print("Enter your Resident ID (or enter 0 if you haven't created a resident profile yet): ");
        int residentId = parseIntInput(scanner.nextLine().trim());

        if (residentId <= 0) {
            System.out.println("Please create a Resident Profile first under Option 3 to get your Resident ID.");
            return;
        }

        Resident resident = residentDao.getResidentById(residentId);
        if (resident == null) {
            System.out.println("Resident ID " + residentId + " not found. Please check your ID or register your profile.");
            return;
        }

        if (!"Active".equalsIgnoreCase(resident.getStatus())) {
            System.out.println("Your resident record is currently inactive. Please contact barangay staff.");
            return;
        }

        System.out.println("Requesting for: " + resident.getFirstName() + " " + resident.getLastName());
        System.out.print("Purpose of Clearance (e.g., Employment, Business Permit, Scholarship): ");
        String purpose = scanner.nextLine().trim();

        if (purpose.isEmpty()) {
            System.out.println("Purpose cannot be empty.");
            return;
        }

        ClearanceRequest request = new ClearanceRequest();
        request.setResidentId(residentId);
        request.setPurpose(purpose);
        request.setStatus("Pending");

        if (clearanceDao.createRequest(request)) {
            activityLogDao.logActivity(currentUser.getUserId(), "REQUEST_CLEARANCE",
                    "Resident ID " + residentId + " submitted Clearance ID " + request.getClearanceId());
            System.out.println("Clearance request submitted successfully! Your Clearance ID is: " + request.getClearanceId());
        } else {
            System.out.println("Failed to submit clearance request.");
        }
    }

    private void viewRequestAndPaymentStatus(Scanner scanner) {
        System.out.println("\n--- VIEW REQUEST & PAYMENT STATUS ---");
        System.out.print("Enter your Resident ID: ");
        int residentId = parseIntInput(scanner.nextLine().trim());

        if (residentId <= 0 || residentDao.getResidentById(residentId) == null) {
            System.out.println("Invalid or non-existent Resident ID.");
            return;
        }

        List<ClearanceRequest> requests = clearanceDao.getRequestsByResidentId(residentId);
        if (requests.isEmpty()) {
            System.out.println("No clearance requests found for Resident ID " + residentId + ".");
            return;
        }

        System.out.printf("%-12s | %-20s | %-25s | %-12s | %-15s%n",
                "Clearance ID", "Request Date", "Purpose", "Req. Status", "Payment Status");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (ClearanceRequest cr : requests) {
            List<Payment> payments = paymentDao.getPaymentsByClearanceId(cr.getClearanceId());
            String paymentInfo = "Unpaid";
            if (!payments.isEmpty()) {
                Payment latest = payments.get(0);
                paymentInfo = String.format("%s (%.2f)", latest.getPaymentStatus(), latest.getAmount());
            }

            System.out.printf("%-12d | %-20s | %-25s | %-12s | %-15s%n",
                    cr.getClearanceId(),
                    cr.getRequestDate() != null ? cr.getRequestDate().toString() : "N/A",
                    cr.getPurpose(),
                    cr.getStatus(),
                    paymentInfo);
        }
    }

    private void manageResidentProfile(User currentUser, Scanner scanner) {
        System.out.println("\n--- RESIDENT PROFILE ---");
        System.out.println("1. Create My Resident Profile");
        System.out.println("2. Look Up My Resident Profile by ID");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
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
                activityLogDao.logActivity(currentUser.getUserId(), "REGISTER_RESIDENT_PROFILE",
                        "User " + currentUser.getUsername() + " created Resident ID " + resident.getResidentId());
                System.out.println("Resident profile created! Please save your Resident ID: " + resident.getResidentId());
            } else {
                System.out.println("Failed to create resident profile.");
            }

        } else if ("2".equals(choice)) {
            System.out.print("Enter Resident ID: ");
            int residentId = parseIntInput(scanner.nextLine().trim());
            Resident r = residentDao.getResidentById(residentId);
            if (r == null) {
                System.out.println("Resident profile not found.");
            } else {
                System.out.println("\nResident ID   : " + r.getResidentId());
                System.out.println("Full Name     : " + r.getFirstName() + " " +
                        (r.getMiddleName() != null ? r.getMiddleName() + " " : "") + r.getLastName());
                System.out.println("Birth Date    : " + (r.getBirthDate() != null ? r.getBirthDate() : "N/A"));
                System.out.println("Gender        : " + r.getGender());
                System.out.println("Address       : " + r.getAddress());
                System.out.println("Contact No.   : " + r.getContactNumber());
                System.out.println("Civil Status  : " + r.getCivilStatus());
                System.out.println("Status        : " + r.getStatus());
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

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