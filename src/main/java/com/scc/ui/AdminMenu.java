package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final ActivityLogViewer logViewer = new ActivityLogViewer();
    private final UserDao userDao = new UserDao();
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public void show(User currentUser, Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. Manage Users (CRUD)");
            System.out.println("2. View Activity Logs");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> manageUsers(currentUser, scanner);
                case "2" -> logViewer.displayLogs();
                case "3" -> {
                    activityLogDao.logActivity(currentUser.getUserId(), "LOGOUT", "Admin logged out: " + currentUser.getUsername());
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void manageUsers(User currentUser, Scanner scanner) {
        boolean managing = true;
        while (managing) {
            System.out.println("\n=== USER MANAGEMENT (CRUD) ===");
            System.out.println("1. Create User");
            System.out.println("2. View All Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> createUser(currentUser, scanner);
                case "2" -> viewAllUsers();
                case "3" -> updateUser(currentUser, scanner);
                case "4" -> deleteUser(currentUser, scanner);
                case "5" -> managing = false;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createUser(User currentUser, Scanner scanner) {
        System.out.println("\n--- CREATE USER ---");
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (fullName.isEmpty() || username.isEmpty()) {
            System.out.println("Full name and username are required.");
            return;
        }

        if (userDao.usernameExists(username)) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.print("Role (ADMIN / STAFF / RESIDENT): ");
        String role = scanner.nextLine().trim().toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("STAFF") && !role.equals("RESIDENT")) {
            System.out.println("Invalid role. Defaulting to RESIDENT.");
            role = "RESIDENT";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus("Active");

        if (userDao.createUser(user)) {
            activityLogDao.logActivity(currentUser.getUserId(), "CREATE_USER",
                    "Created user '" + username + "' with role " + role);
            System.out.println("User created successfully (ID: " + user.getUserId() + ").");
        } else {
            System.out.println("Failed to create user.");
        }
    }

    private void viewAllUsers() {
        List<User> users = userDao.getAllUsers();
        System.out.println("\n--- ALL USERS ---");
        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.printf("%-5s | %-15s | %-25s | %-10s | %-10s%n",
                "ID", "Username", "Full Name", "Role", "Status");
        System.out.println("--------------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-15s | %-25s | %-10s | %-10s%n",
                    u.getUserId(), u.getUsername(), u.getFullName(), u.getRole(), u.getStatus());
        }
    }

    private void updateUser(User currentUser, Scanner scanner) {
        System.out.println("\n--- UPDATE USER ---");
        viewAllUsers();
        System.out.print("Enter User ID to update: ");
        int userId = parseIntInput(scanner.nextLine().trim());
        if (userId <= 0) {
            System.out.println("Invalid User ID.");
            return;
        }

        User existing = userDao.getUserById(userId);
        if (existing == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("New Full Name (leave blank to keep '" + existing.getFullName() + "'): ");
        String fullName = scanner.nextLine().trim();
        if (!fullName.isEmpty()) {
            existing.setFullName(fullName);
        }

        System.out.print("New Role [ADMIN/STAFF/RESIDENT] (leave blank to keep '" + existing.getRole() + "'): ");
        String role = scanner.nextLine().trim().toUpperCase();
        if (!role.isEmpty()) {
            if (role.equals("ADMIN") || role.equals("STAFF") || role.equals("RESIDENT")) {
                existing.setRole(role);
            } else {
                System.out.println("Invalid role entered; keeping existing role.");
            }
        }

        System.out.print("New Status [Active/Inactive] (leave blank to keep '" + existing.getStatus() + "'): ");
        String status = scanner.nextLine().trim();
        if (!status.isEmpty()) {
            existing.setStatus(status);
        }

        System.out.print("New Password (leave blank to keep current password): ");
        String newPassword = scanner.nextLine().trim();

        if (userDao.updateUser(existing, newPassword.isEmpty() ? null : newPassword)) {
            activityLogDao.logActivity(currentUser.getUserId(), "UPDATE_USER",
                    "Updated user ID " + userId + " (" + existing.getUsername() + ")");
            System.out.println("User updated successfully.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private void deleteUser(User currentUser, Scanner scanner) {
        System.out.println("\n--- DELETE USER ---");
        viewAllUsers();
        System.out.print("Enter User ID to delete: ");
        int userId = parseIntInput(scanner.nextLine().trim());
        if (userId <= 0) {
            System.out.println("Invalid User ID.");
            return;
        }

        if (userId == currentUser.getUserId()) {
            System.out.println("You cannot delete your own active admin account.");
            return;
        }

        User target = userDao.getUserById(userId);
        if (target == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Are you sure you want to delete '" + target.getUsername() + "'? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        if (userDao.deleteUser(userId)) {
            activityLogDao.logActivity(currentUser.getUserId(), "DELETE_USER",
                    "Deleted user ID " + userId + " (" + target.getUsername() + ")");
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Failed to delete user (user may be referenced in existing records).");
        }
    }

    private int parseIntInput(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}