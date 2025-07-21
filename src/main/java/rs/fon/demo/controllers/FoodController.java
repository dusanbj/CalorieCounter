package rs.fon.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.fon.demo.dto.requests.FoodItemRequest;
import rs.fon.demo.dto.responses.DailyEntryResponse;
import rs.fon.demo.dto.requests.FoodEntryRequest;
import rs.fon.demo.dto.responses.FoodItemResponse;
import rs.fon.demo.model.FoodItem;
import rs.fon.demo.services.FoodService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200",
        allowedHeaders = "*",
        allowCredentials = "true",
        methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.OPTIONS})
@RestController
@RequestMapping("/food")
public class FoodController {
    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @PostMapping("/food-entry")
    public ResponseEntity<DailyEntryResponse> createEntry(@RequestBody FoodEntryRequest foodEntryRequest) {
        return new ResponseEntity<>(foodService.createDailyEntry(foodEntryRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/food-item")
    public ResponseEntity<FoodItem> createFoodItem(@RequestBody FoodItem foodItem) {
        return new ResponseEntity<>(foodService.createFoodItem(foodItem), HttpStatus.CREATED);
    }

    //u params staviti datum u formatu "dd.MM.yyyy"
    //?date=12.07.2025
    @GetMapping("/daily-entry")
    public ResponseEntity<DailyEntryResponse> readDailyEntry(@RequestParam("date") @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate date) {
        DailyEntryResponse dailyEntryResponse = foodService.readDailyEntry(date);
        return new ResponseEntity<>(dailyEntryResponse, HttpStatus.OK);
    }
    @GetMapping("/daily-entries")
    public ResponseEntity<List<DailyEntryResponse>> readDailyEntries() {
        List<DailyEntryResponse> dailyEntries = foodService.readDailyEntries();
        return new ResponseEntity<>(dailyEntries, HttpStatus.OK);
    }
    @GetMapping("/food-item")
    public ResponseEntity<FoodItem> readFoodItem(@RequestParam("name") String name) {
        FoodItem foodItem = foodService.readFoodItem(name);
        return new ResponseEntity<>(foodItem, HttpStatus.OK);
    }
    @GetMapping("/food-items")
    public ResponseEntity<List<FoodItem>> readFoodItems() {
        List<FoodItem> foodItems = foodService.readFoodItems();
        return new ResponseEntity<>(foodItems, HttpStatus.OK);
    }
    //u params staviti datum u formatu "dd.MM.yyyy"
    //?date=12.07.2025
    //prebaceno na ?date=2025-07-21

    @DeleteMapping("/daily-entry")
    public ResponseEntity<Long> deleteDailyEntry(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long id = foodService.deleteDailyEntry(date);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/food-item/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest requestBody) {

        FoodItemResponse updated = foodService.updateFoodItem(id, requestBody);

        return ResponseEntity.ok(updated);
    }

}