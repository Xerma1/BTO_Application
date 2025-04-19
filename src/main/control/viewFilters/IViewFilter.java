package main.control.viewFilters;

import java.util.List;
import main.entity.Project;

public interface IViewFilter {

    public void view();
    public default void view(List<Project> projects) {};
    public List<Project> getValidProjects();
       
}
