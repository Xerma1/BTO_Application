package main.control.viewFilters;

import java.util.List;

import main.entity.Project;

public interface IFilterProjectsByUserGroup {
    public List<Project> getValidProjects();
}
