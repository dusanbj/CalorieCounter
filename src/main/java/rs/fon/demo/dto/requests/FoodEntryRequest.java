package rs.fon.demo.dto.requests;

public class FoodEntryRequest {

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
