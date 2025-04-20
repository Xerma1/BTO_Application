package main.entity;

public class Applicant extends User {
    //private applicantApplication application;

    private static final String FILTER_ALL = "married";
    private static final String FILTER_SINGLE = "single";

    //constructor
    public Applicant(String name, String userID, int age, String married, String password, String accessLevel, String filterType) {

        super(name, userID, age, married, password, accessLevel, filterType);
    }

}
