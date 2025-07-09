package rs.fon.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double caloriesPer100g;

    public FoodItem(String name, double caloriesPer100g) {
        this.name = name;
        this.caloriesPer100g = caloriesPer100g;
    }

    public FoodItem() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCaloriesPer100g() {
        return caloriesPer100g;
    }

    public void setCaloriesPer100g(double caloriesPer100g) {
        this.caloriesPer100g = caloriesPer100g;
    }
}
