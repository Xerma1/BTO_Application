package main.control;

import java.io.BufferedReader; //Used to read text from a file line by line
import java.io.FileReader; //Used to open and read the file
import java.io.IOException; //Handles exceptions that may occur during file operations

public class loginManager {
    
    // Login function that checks whether the given username and password matches
    public static boolean login(String userID, String password, String usergroup) throws InvalidLoginException {
        String filePath = "data/processed/" + usergroup + ".csv"; // Path to the CSV file based on usergroup parameter

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) { 
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Splits current line into an array of strings eg."admin,password" -> [admin, password] 
                String storedUserID = values[1].trim(); // Second column: userID, trimmed to remove leading and trailing whitespace
                String storedPassword = values[4].trim(); // Fifth column: password

                // Check if userID and password match
                if (storedUserID.equals(userID) && storedPassword.equals(password)) {
                    return true; // Login successful
                }
            }
        } catch (IOException e) { //catches any IOException that might occur while reading the file
            System.err.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        // Login failed, throw custom exception InvalidLoginException
        throw new InvalidLoginException("Invalid userID or password");
        
    }
}
