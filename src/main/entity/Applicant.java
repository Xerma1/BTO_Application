package main.entity;

public class Applicant extends User {
    //private applicantApplication application;
    public String filterType;
    private static final String FILTER_ALL = "married";
    private static final String FILTER_SINGLE = "single";

    //constructor
    public Applicant(String name, String userID, int age, boolean married, String password, String accessLevel) {
        super(name, userID, age, married, password, accessLevel);
        this.filterType = married ? FILTER_ALL : FILTER_SINGLE;
    }

    public String getFilterType() {
        return filterType;
    }

}
