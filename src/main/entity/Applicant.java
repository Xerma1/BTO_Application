package main.entity;

/**
 * Represents an applicant in the BTO application system.
 * An applicant is a user who can apply for flats, view projects, and manage their bookings.
 */
public class Applicant extends User {

    private static final String FILTER_ALL = "married";
    private static final String FILTER_SINGLE = "single";

    //constructor
    public Applicant(String name, String userID, int age, String married, String password, String accessLevel, String filterType) {

        super(name, userID, age, married, password, accessLevel, filterType);
    }

}
