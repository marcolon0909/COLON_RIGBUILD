package app;

import database.DatabaseInitializer;
import dao.UserDao;
import ui.Authentication;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Step 1: Initialize Database & Tables automatically
        DatabaseInitializer.initialize();

        // Step 2: Instantiate DAOs & Shared Resources
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        // Step 3: Delegate execution to Authentication UI handler
        Authentication authHandler = new Authentication(userDao, scanner);
        authHandler.start();
    }
}