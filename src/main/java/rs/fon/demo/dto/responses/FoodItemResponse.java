package rs.fon.demo.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodItemResponse {
    private Long id;
    private String name;
    private double caloriesPer100g;
}
