package com.scc;

import com.scc.dao.DatabaseInitializer;
import com.scc.model.User;
import com.scc.ui.AdminMenu;
import com.scc.ui.Authentication;
import com.scc.ui.BarangayResidentMenu;
import com.scc.ui.StaffMenu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        Scanner scanner = new Scanner(System.in);
        Authentication auth = new Authentication();

        while (true) {
            // Displays Login / Sign Up / Exit first
            User loggedInUser = auth.start(scanner);

            // Exit option selected
            if (loggedInUser == null) {
                break;
            }

            String role = loggedInUser.getRole() != null ? loggedInUser.getRole().toUpperCase() : "";
            switch (role) {
                case "ADMIN" -> new AdminMenu().show(loggedInUser, scanner);
                case "STAFF" -> new StaffMenu().show(loggedInUser, scanner);
                case "RESIDENT" -> new BarangayResidentMenu().show(loggedInUser, scanner);
                default -> System.out.println("Unrecognized role: " + loggedInUser.getRole());
            }
        }

        scanner.close();
    }
}