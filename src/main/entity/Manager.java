package main.entity;


public class Manager extends User {
    public String filterType = "married";
    //constructor
    public Manager(String name, String userID, int age, String married, String password, String accessLevel, String filterType) {
        super(name, userID, age, married, password, accessLevel, filterType);
    }
}
