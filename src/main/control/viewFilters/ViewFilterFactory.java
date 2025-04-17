package main.control.viewFilters;

public class ViewFilterFactory {
    public static IviewFilter getViewFilter(String filterType) {
        return switch (filterType) {
            case "single" -> new ViewSingle();
            case "all" -> new ViewAll();
            default -> throw new IllegalArgumentException("Invalid filterType: " + filterType);
        };
    }
}
