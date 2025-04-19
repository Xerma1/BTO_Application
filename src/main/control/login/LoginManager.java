package main.control.login;

import main.control.dataManagers.UserManager;
import main.entity.User;

public class LoginManager {
    
    // Login function that checks whether the given username and password matches
    public static boolean login(String userID, String password, String usergroup) throws InvalidLoginException {
        User user = UserManager.fetch(userID); // Get user

        if (user == null) { // Check if user exists
            throw new InvalidLoginException("Error: Wrong User ID.");
        }

        if (!user.getPassword().equals(password)) { // Check if password matches
            throw new InvalidLoginException("Error: Wrong Password.");
        }

        if (!user.getAccessLevel().equals(usergroup)) { // Check if user group matches
            throw new InvalidLoginException("Error: " + user.getAccessLevel() + " trying to log in as " + usergroup + ". Please return to user group selection.");
        }
        return true; // Login successful
    }
}
