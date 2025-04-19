package main.entity;

public class Enquiry {
    private String userID;
    private String projectName;
    private String question;
    private String answer;

    public Enquiry(String userID, String projectName, String question) {
        this.userID = userID;
        this.projectName = projectName;
        this.question = question;
    }

    public String getUserID() {
        return userID;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    // viewEnquiries()
    // deleteEnquiry()
    // edit()
}
