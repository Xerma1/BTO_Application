package main.control.dataManagers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OfficerManager extends DataManager {

    private static final String OFFICER_REG_CSV = "data/processed/officer_registrations.csv";

    public static void viewOfficerRegistrations() {
        ensureOfficerFileExists(); // Make sure file exists before reading

        try {
            List<String[]> officers = readCSV(OFFICER_REG_CSV);
            if (officers.isEmpty()) {
                System.out.println("No officer registrations found.");
                return;
            }
            System.out.println("\nOfficer Registrations:");
            for (String[] o : officers) {
                System.out.println("UserID: " + o[0] + " | Status: " + o[1]);
            }
        } catch (IOException e) {
            System.out.println("Error reading officer registrations.");
            e.printStackTrace();
        }
    }

    public static void approveRejectOfficerRegistrations(Scanner scanner) {
        ensureOfficerFileExists(); // Make sure file exists before reading

        try {
            List<String[]> officers = readCSV(OFFICER_REG_CSV);
            if (officers.isEmpty()) {
                System.out.println("No officer registrations available to approve/reject.");
                return;
            }

            System.out.print("Enter officer userID to approve/reject: ");
            String userID = scanner.nextLine();
            boolean found = false;

            for (String[] o : officers) {
                if (o[0].equalsIgnoreCase(userID)) {
                    System.out.print("Approve (A) or Reject (R)?: ");
                    String decision = scanner.nextLine().toUpperCase();
                    if (decision.equals("A")) {
                        o[1] = "APPROVED";
                        System.out.println("Officer approved.");
                    } else if (decision.equals("R")) {
                        o[1] = "REJECTED";
                        System.out.println("Officer rejected.");
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    found = true;
                    break;
                }
            }

            if (found) {
                writeCSV(OFFICER_REG_CSV, officers);
                System.out.println("Updated officer status.");
            } else {
                System.out.println("Officer not found.");
            }
        } catch (IOException e) {
            System.out.println("Error writing officer registrations.");
            e.printStackTrace();
        }
    }

    private static void ensureOfficerFileExists() {
        try {
            File file = new File(OFFICER_REG_CSV);
            if (!file.exists()) {
                // If folder path "data/processed/" does not exist, create it
                file.getParentFile().mkdirs();
                file.createNewFile();

                // Also initialize it with an empty list (headerless if you want) — optional
                writeCSV(OFFICER_REG_CSV, new ArrayList<>());
            }
        } catch (IOException e) {
            System.out.println("Failed to create officer registration file.");
            e.printStackTrace();
        }
    }
}
