package main.control.login;

/**
 * InvalidLoginException class to handle invalid login attempts.
 */
public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message); // Pass the custom mesage to the Exception class
    }

}
