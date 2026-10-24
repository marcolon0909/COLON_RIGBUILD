package ui;

import dao.UserDao;
import model.User;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== RIGBUILD SYSTEM AUTHENTICATION ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegistration();
                case "3" -> {
                    System.out.println("Exiting RigBuild. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private void handleRegistration() {
        System.out.println("\n--- ACCOUNT REGISTRATION ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("Select Role:");
        System.out.println("1. Admin");
        System.out.println("2. Sales Agent");
        System.out.println("3. Technician");
        System.out.print("Choice: ");

        String roleChoice = scanner.nextLine();
        String role = switch (roleChoice) {
            case "1" -> "ADMIN";
            case "2" -> "SALES_AGENT";
            case "3" -> "TECHNICIAN";
            default -> "SALES_AGENT";
        };

        boolean success = userDao.registerUser(username, password, role);
        if (success) {
            System.out.println("Registration successful! Account status is PENDING approval.");
        } else {
            System.out.println("Registration failed. Username may already exist.");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = userDao.authenticate(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }

        if ("PENDING".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is currently PENDING approval by an Administrator.");
            return;
        }

        routeToRoleMenu(user);
    }

    private void routeToRoleMenu(User user) {
        System.out.println("\nWelcome, " + user.username() + "! [Role: " + user.roles() + "]");
        switch (user.roles().toUpperCase()) {
            case "ADMIN" -> {
                AdminMenu adminMenu = new AdminMenu(userDao, scanner);
                adminMenu.displayMenu();
            }
            case "SALES_AGENT" -> System.out.println(">>> Redirected to BUILD ORDER MANAGEMENT...");
            case "TECHNICIAN" -> System.out.println(">>> Redirected to ASSEMBLY & TESTING MODULE...");
            default -> System.out.println("Unknown role menu.");
        }
    }
}