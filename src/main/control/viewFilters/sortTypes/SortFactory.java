package main.control.viewFilters.sortTypes;

import main.control.viewFilters.ISortProjects;

public class SortFactory {
    public static ISortProjects getSortType(String category) {
        return switch (category) {
            case "name" -> new SortByName();
            case "location" -> new SortByLocation();
            case "2-room flat availability" -> new SortByTwoRoom();
            case "3-room flat availability" -> new SortByThreeRoom();
            case "price of 2-room flat" -> new SortByTwoRoomPrice();
            case "price of 3-room flat" -> new SortByThreeRoomPrice();
            default -> throw new IllegalArgumentException("Invalid filterType: " + category);
        };
    }
}

