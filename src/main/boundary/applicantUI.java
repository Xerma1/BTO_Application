package main.boundary;

import java.util.Scanner;

public class applicantUI implements IusergroupUI {
    
    @Override
    public void printUI(Scanner scanner, String username) {
        //TODO: create instance of applicant class
            //
            //
            
        // UI, edit accordingly
        System.out.println("<< Viewing as applicant: " + username + " >>");
        System.out.println(" ");
        System.out.println("1. ");
        System.out.println("2. ");
        System.out.println("3. ");
        System.out.println(" ");
        System.out.print("Input: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        //Input switch statment 
    }

}
