package rs.fon.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.demo.dto.DailyEntryResponseDTO;
import rs.fon.demo.dto.FoodEntryRequestDTO;
import rs.fon.demo.model.DailyEntry;
import rs.fon.demo.model.FoodEntry;
import rs.fon.demo.model.FoodItem;
import rs.fon.demo.services.FoodService;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/createFoodItem")
    public ResponseEntity<FoodItem> createFoodItem(@RequestBody FoodItem foodItem) {
        return new ResponseEntity<>(foodService.createFoodItem(foodItem), HttpStatus.CREATED);
    }

    //u params staviti datum u formatu "dd.MM.yyyy"
    //?date=12.07.2025
    @GetMapping("/readDailyEntry")
    public ResponseEntity<DailyEntryResponseDTO> readDailyEntry(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        DailyEntryResponseDTO dailyEntryResponseDTO = foodService.readDailyEntry(date);
        return new ResponseEntity<>(dailyEntryResponseDTO, HttpStatus.OK);
    }
    @GetMapping("/readDailyEntries")
    public ResponseEntity<List<DailyEntryResponseDTO>> readDailyEntries() {
        List<DailyEntryResponseDTO> dailyEntries = foodService.readDailyEntries();
        return new ResponseEntity<>(dailyEntries, HttpStatus.OK);
    }
    @GetMapping("/readFoodItem")
    public ResponseEntity<FoodItem> readFoodItem(@RequestParam("name") String name) {
        FoodItem foodItem = foodService.readFoodItem(name);
        return new ResponseEntity<>(foodItem, HttpStatus.OK);
    }
    @GetMapping("/readFoodItems")
    public ResponseEntity<List<FoodItem>> readFoodItems() {
        List<FoodItem> foodItems = foodService.readFoodItems();
        return new ResponseEntity<>(foodItems, HttpStatus.OK);
    }
    //u params staviti datum u formatu "dd.MM.yyyy"
    //?date=12.07.2025
    @DeleteMapping("/deleteDailyEntry")
    public ResponseEntity<Long> deleteDailyEntry(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        Long id = foodService.deleteDailyEntry(date);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}