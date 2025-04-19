package main.control.viewFilters;

public class ViewFilterFactory {
    public static IViewFilter getViewFilter(String filterType) {
        return switch (filterType) {
            case "single" -> new ViewSingle();
            case "married" -> new ViewMarried();
            case "all" -> new ViewAll();
            default -> throw new IllegalArgumentException("Invalid filterType: " + filterType);
        };
    }
}
