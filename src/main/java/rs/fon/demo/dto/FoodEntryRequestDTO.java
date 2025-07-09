package rs.fon.demo.dto;

import rs.fon.demo.model.DailyEntry;
import rs.fon.demo.model.FoodItem;

import javax.persistence.ManyToOne;

public class FoodEntryRequestDTO {

    private double grams;

    private String foodName;

    public double getGrams() {
        return grams;
    }

    public void setGrams(double grams) {
        this.grams = grams;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
