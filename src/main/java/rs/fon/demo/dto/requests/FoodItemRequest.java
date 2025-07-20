package rs.fon.demo.dto.requests;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class FoodItemRequest {
    @NotBlank
    private String name;

    @Positive
    private int caloriesPer100g;
}
