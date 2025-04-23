package main.control;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Handles date and time formatting
import java.time.format.DateTimeParseException; // Handles exceptions that may occur during date parsing


// Uses local timezone for simplicity
public class TimeManager {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static String timeNow() {
        LocalDateTime date = LocalDateTime.now(); // Get current date and time
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // Defining format of date and time
        String formattedDate = date.format(format); // Applying format to date
        return formattedDate;
    }

    // Checks if the current date now is after closeDate
    public static boolean isAfter(String closeDate) {
        LocalDate date = LocalDate.now(); 
        DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy"); // Defining format of date and time
        try {
            LocalDate close = LocalDate.parse(closeDate, format);
            return (date.isAfter(close));

        } catch (DateTimeParseException e) { // Handles exceptions that may occur during date parsing
            System.out.println("Invalid date format. Please use 'M/d/yyyy'.");
            return false;
        }
    }

    // Checks if the current date now is within openDate and closeDate
    public static boolean isValidDate(String openDate, String closeDate) {
        LocalDate date = LocalDate.now(); 
        DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy"); // Defining format of date and time
        try {
            LocalDate open = LocalDate.parse(openDate, format); // Changing openDate from String to LocalDate
            LocalDate close = LocalDate.parse(closeDate, format); // Changing endDate from String to LocalDate

            return !(date.isBefore(open) || date.isAfter(close)); // Returns true if current date is within range inclusive

        } catch (DateTimeParseException e) { // Handles exceptions that may occur during date parsing
            System.out.println("Invalid date format. Please use 'M/d/yyyy'.");
            return false;
        }
    }

    // Method to check if two date ranges overlap
    public static boolean isDateRangeOverlapping(String start1, String end1, String start2, String end2) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy"); // Updated to handle single-digit months and days
        LocalDate startDate1 = LocalDate.parse(start1, format);
        LocalDate endDate1 = LocalDate.parse(end1, format);
        LocalDate startDate2 = LocalDate.parse(start2, format);
        LocalDate endDate2 = LocalDate.parse(end2, format);
    
        // Check if the date ranges overlap
        return !(endDate1.isBefore(startDate2) || endDate2.isBefore(startDate1));
    }

}
