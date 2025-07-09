package rs.fon.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class DailyEntryResponseDTO {
    private long id;
    private LocalDate date;
    private List<FoodEntryRequestDTO> entries;
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

    public List<FoodEntryRequestDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<FoodEntryRequestDTO> entries) {
        this.entries = entries;
    }
}
