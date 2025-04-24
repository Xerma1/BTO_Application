package main.boundary;

import java.util.Scanner;

import main.entity.User;

/**
 * IUserGroupUI interface to define the user group menu functionality.
 * This interface provides a method to run the user group menu for different user types (Single, married, all).
 */
public interface IUserGroupUI {
    public void runMenu(Scanner scanner, User user); //abstract method to be implemented
}
