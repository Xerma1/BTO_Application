package main.control;

import java.util.Scanner;

/**
 * InputManager class to handle user input and validation.
 * This class provides methods to prompt the user for input and validate it.
 */
public class InputManager {
    public static int promptUserChoice(Scanner scanner, int min, int max) {
    int choice = -1;
    while (true) {
        System.out.print("Input: ");
        if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            scanner.nextLine(); // Clear newline
            if (choice >= min && choice <= max) break;
            System.out.println("Invalid choice. Please type a number from " + min + " to " + max + ".");
        } else {
            System.out.println("Invalid input. Please type a number.");
            scanner.next(); // Discard invalid input
        }
    }
    return choice;
}
}
