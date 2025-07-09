package rs.fon.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.demo.dto.DailyEntryResponseDTO;
import rs.fon.demo.dto.FoodEntryRequestDTO;
import rs.fon.demo.model.DailyEntry;
import rs.fon.demo.model.FoodEntry;
import rs.fon.demo.services.FoodService;

@RestController
@RequestMapping("/food")
public class FoodController {
    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping("/createEntry")
    public ResponseEntity<DailyEntryResponseDTO> createEntry(@RequestBody FoodEntryRequestDTO foodEntryRequestDTO) {
        return new ResponseEntity<>(foodService.createDailyEntry(foodEntryRequestDTO), HttpStatus.CREATED);
    }
}