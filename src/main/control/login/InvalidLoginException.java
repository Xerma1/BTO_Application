package main.control.login;

public class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) {
        super(message); // Pass the custom mesage to the Exception class
    }

}
