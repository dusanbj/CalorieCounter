package rs.fon.demo.dto.responses;

import rs.fon.demo.dto.requests.FoodEntryRequest;

import java.time.LocalDate;
import java.util.List;

public class DailyEntryResponse {
    private long id;
    private LocalDate date;
    private List<FoodEntryRequest> entries;
    private double totalCalories;

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<FoodEntryRequest> getEntries() {
        return entries;
    }

    public void setEntries(List<FoodEntryRequest> entries) {
        this.entries = entries;
    }
}
