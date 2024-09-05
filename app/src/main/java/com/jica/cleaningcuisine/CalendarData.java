package com.jica.cleaningcuisine;

public class CalendarData {
    private int year;
    private int month;
    private int day;
    private String selectedItems;

    public CalendarData(int year, int month, int day, String selectedItems) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.selectedItems = selectedItems;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getSelectedItems() {
        return selectedItems;
    }
}
