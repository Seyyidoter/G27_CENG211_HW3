package slidingpenguins.util;

import java.util.Scanner;
import slidingpenguins.core.Direction;

/**
 * Utility class to handle user inputs securely.
 * Ensures inputs are case-insensitive and valid .
 */
public class InputHelper {
    
    // Using a single static Scanner prevents resource leaks and conflicts.
    // We do NOT close this scanner because closing System.in disables input for the whole app.
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user for a direction (U, D, L, R).
     * Loops until a valid input is received.
     * @param message The prompt message to display to the user.
     * @return The valid Directions enum value selected by the user.
     */
    public static Direction getDirection(String message) {
        while (true) {
            System.out.print(message);
            // Read input, trim spaces, and convert to uppercase to handle "u", "U ", etc.
            String input = scanner.nextLine().trim().toUpperCase(); 

            switch (input) {
                case "U": return Direction.UP;
                case "D": return Direction.DOWN;
                case "L": return Direction.LEFT;
                case "R": return Direction.RIGHT;
                default:
                    // If input is incorrect, show error and loop again 
                    System.out.println("Invalid direction! Please enter U (Up), D (Down), L (Left), or R (Right).");
            }
        }
    }

    /**
     * Prompts the user for a Yes/No answer.
     * Accepts Y/N inputs (case-insensitive).
     * @param message The prompt message to display.
     * @return true if user enters 'Y', false if user enters 'N'.
     */
    public static boolean getYesNo(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            
            System.out.println("Invalid input! Please enter Y (Yes) or N (No).");
        }
    }
}
