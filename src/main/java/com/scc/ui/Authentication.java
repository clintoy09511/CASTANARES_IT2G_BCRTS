package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.dao.UserDao;
import com.scc.model.User;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao = new UserDao();
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public User start(Scanner scanner) {
        while (true) {
            System.out.println("\n=== BARANGAY VIRMS ===");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    User loggedInUser = login(scanner);
                    if (loggedInUser != null) {
                        return loggedInUser;
                    }
                }
                case "2" -> signUp(scanner);
                case "3" -> {
                    System.out.println("Exiting application. Goodbye!");
                    return null;
                }
                default -> System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }
        }
    }

    public User login(Scanner scanner) {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.authenticate(username, password);
        if (user != null) {
            activityLogDao.logActivity(user.getUserId(), "LOGIN", "User logged in: " + user.getUsername());
            System.out.println("Welcome, " + user.getFullName() + " (" + user.getRole() + ")!");
        } else {
            System.out.println("Invalid credentials or inactive account.");
        }
        return user;
    }

    public void signUp(Scanner scanner) {
        System.out.println("\n=== SIGN UP ===");

        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();

        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        if (fullName.isEmpty() || username.isEmpty()) {
            System.out.println("Full name and username are required.");
            return;
        }

        if (userDao.usernameExists(username)) {
            System.out.println("Username is already taken. Please choose another.");
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setRole("RESIDENT");
        newUser.setStatus("Active");

        if (userDao.createUser(newUser)) {
            activityLogDao.logActivity(newUser.getUserId(), "SIGN_UP", "New user signed up: " + newUser.getUsername());
            System.out.println("Sign up successful! You can now log in.");
        } else {
            System.out.println("Sign up failed. Please try again.");
        }
    }
}