package main.entity;

import java.util.ArrayList;
import java.util.List;

import main.control.dataManagers.ProjectManager;
import main.control.viewFilters.ViewAll;

public class Officer extends Applicant {
    private boolean hasActiveProj;

    //constructor
    public Officer(String name, String userID, int age, boolean married, String password, String accessLevel) {
        super(name, userID, age, married, password, accessLevel);
    }

    public List<Project> getHandling() {
        List<Project> projects = ProjectManager.getFetchAll();

        if (projects == null || projects.isEmpty()) {
            return null; // No handling projects
        }

        List<Project> handlingProjects = new ArrayList<>();
        for (Project project : projects) {
            if (ProjectManager.isRegistered(this, project)) {
                handlingProjects.add(project);
            }
        }
        return handlingProjects;        
    }

    public void viewHandling() {
        List<Project> handling = this.getHandling();
        ViewAll viewInterface = new ViewAll();
        viewInterface.view(handling);
    }
    
}
