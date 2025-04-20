package main.entity;

public class Enquiry {
    private String userName;
    private String userID;
    private String projectName;
    private String question;
    private String answer;

    public Enquiry(String userName, String userID, String projectName, String question) {
        this.userName = userName;
        this.userID = userID;
        this.projectName = projectName;
        this.question = question;
    }

    public String getUserName() {
        return userName;
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

    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
