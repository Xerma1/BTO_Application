package main.entity;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a BTO project in the application system.
 */
public class Project {
    private String projectName;
    private String neighbourhood;
    private List<String[]> flatTypes; // Map of flat types and their respective counts
    private String openDate;
    private String closeDate;
    private String manager;
    private int officerSlots;
    private String[] officers;
    private boolean visibility; // True if project is open for application, false otherwise 

    // Constructor
    public Project(String projectName, String neighbourhood, List<String[]> flatTypes, 
                   String openDate, String closeDate, String manager, int officerSlots, String[] officers, boolean visibility) {
        this.projectName = projectName;
        this.neighbourhood = neighbourhood;
        this.flatTypes = flatTypes;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.manager = manager;
        this.officerSlots = officerSlots;
        this.officers = officers;
        this.visibility = visibility;
    }

    // Getter methods
    public String getProjectName() {
        return projectName;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public List<String[]> getFlatTypes() {
        return flatTypes;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public String getManager() {
        return manager;
    }

    public int getOfficerSlots() {
        return officerSlots;
    }

    public String[] getOfficers() {
        List<String> officerList = new ArrayList<>();
        for (String officer : officers) {
            if (officer != null && !officer.isEmpty()) {
                officerList.add(officer);
            }
        }
        return officerList.toArray(new String[0]);
    }

    public boolean isVisibility() {
        return visibility;
    }

    // Setter methods
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public void setFlatTypes(List<String[]> flatTypes) {
        this.flatTypes = flatTypes;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
    }

    public void setOfficers(String[] officers) {
        this.officers = officers;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}