package main.control.viewFilters;

import java.util.List;

import main.entity.Project;

/**
 * IFilterProjectsByUserGroup interface to filter projects based on user group.
 * This interface provides a method to retrieve valid projects for a specific user group (Single, married, all).
 */
public interface IFilterProjectsByUserGroup {
    public List<Project> getValidProjects();
}
