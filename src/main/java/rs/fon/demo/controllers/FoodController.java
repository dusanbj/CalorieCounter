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

    @PostMapping("/food-entries")
    public ResponseEntity<DailyEntryResponse> createEntry(@RequestBody FoodEntryRequest foodEntryRequest) {
        return new ResponseEntity<>(foodService.createDailyEntry(foodEntryRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/food-items")
    public ResponseEntity<FoodItem> createFoodItem(@RequestBody FoodItem foodItem) {
        return new ResponseEntity<>(foodService.createFoodItem(foodItem), HttpStatus.CREATED);
    }

    //u params staviti datum u formatu "yyyy-MM-dd"
    //?date=2025-07-24
    @GetMapping("/daily-entries")
    public ResponseEntity<?> readDailyEntries(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate date) {

        if (date == null) {
            // nema ?date u URL‑u => vrati listu
            List<DailyEntryResponse> allEntries = foodService.readDailyEntries();
            return ResponseEntity.ok(allEntries);
        } else {
            // ima ?date=… => vrati samo za taj datum
            DailyEntryResponse single = foodService.readDailyEntry(date);
            return ResponseEntity.ok(single);
        }
    }

    @GetMapping("/food-items")
    public ResponseEntity<?> readFoodItems(@RequestParam(value = "name", required = false) String name) {
        if (name != null && !name.isBlank()) {
            // ako je ?name= prosleđen
            FoodItem foodItem = foodService.readFoodItem(name);
            return ResponseEntity.ok(foodItem);
        } else {
        // ako name NIJE prosleđen
        List<FoodItem> foodItems = foodService.readFoodItems();
        return ResponseEntity.ok(foodItems);
            }
    }

    //u params staviti datum u formatu "yyyy-MM-dd"
    //?date=2025-07-21
    @DeleteMapping("/daily-entries")
    public ResponseEntity<Long> deleteDailyEntry(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long id = foodService.deleteDailyEntry(date);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/food-items/{id}")
    public ResponseEntity<FoodItemResponse> updateFoodItem(
            @PathVariable Long id,
            @Valid @RequestBody FoodItemRequest requestBody) {

        FoodItemResponse updated = foodService.updateFoodItem(id, requestBody);

        return ResponseEntity.ok(updated);
    }

}