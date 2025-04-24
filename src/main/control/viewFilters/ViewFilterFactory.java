package main.control.viewFilters;

/**
 * Factory class to create instances of view filters.
 * This class is responsible for creating the appropriate filter based on the user's marital status.
 */
public class ViewFilterFactory {
    public static IFilterProjectsByUserGroup getProjectByMartialStatus(String martialStatus) { // This gets the appropriate filter type for getValidProjects()
        return switch (martialStatus.toLowerCase()) {
            case "single" -> new ViewSingle();
            case "married" -> new ViewMarried();
            case "all" -> new ViewAll();
            default -> throw new IllegalArgumentException("Invalid filterType: " + martialStatus);
        };
    }

    public static IViewFilter getViewFilterType(String martialStatus) { // This is basically just the viewing method for view()
        return switch (martialStatus.toLowerCase()) {
            case "single" -> new ViewSingle();
            case "married" -> new ViewMarried();
            case "all" -> new ViewAll();
            default -> throw new IllegalArgumentException("Invalid filterType: " + martialStatus);
        };
    }
}
