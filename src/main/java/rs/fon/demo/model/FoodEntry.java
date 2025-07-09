package rs.fon.demo.model;

import javax.persistence.*;

@Entity
public class FoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private FoodItem foodItem;

    private double grams;

    @ManyToOne(optional = false)
    private DailyEntry dailyEntry;

    public double getTotalCalories() {
        return (foodItem.getCaloriesPer100g() / 100.0) * grams;
    }

    public DailyEntry getDailyEntry() {
        return dailyEntry;
    }

    public void setDailyEntry(DailyEntry dailyEntry) {
        this.dailyEntry = dailyEntry;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public double getGrams() { return grams; }
    public void setGrams(double grams) { this.grams = grams; }
}

