package rs.fon.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import rs.fon.demo.dto.requests.FoodItemRequest;
import rs.fon.demo.dto.responses.DailyEntryResponse;
import rs.fon.demo.dto.requests.FoodEntryRequest;
import rs.fon.demo.dto.responses.FoodItemResponse;
import rs.fon.demo.model.DailyEntry;
import rs.fon.demo.model.FoodEntry;
import rs.fon.demo.model.FoodItem;
import rs.fon.demo.model.User;
import rs.fon.demo.repositories.DailyEntryRepository;
import rs.fon.demo.repositories.FoodEntryRepository;
import rs.fon.demo.repositories.FoodItemRepository;
import rs.fon.demo.repositories.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class FoodService {

    private final FoodItemRepository foodItemRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final DailyEntryRepository dailyEntryRepository;
    private final UserRepository userRepository;

    @Autowired
    public FoodService(FoodItemRepository foodItemRepository, FoodEntryRepository foodEntryRepository, DailyEntryRepository dailyEntryRepository, UserRepository userRepository) {
        this.foodItemRepository = foodItemRepository;
        this.foodEntryRepository = foodEntryRepository;
        this.dailyEntryRepository = dailyEntryRepository;
        this.userRepository = userRepository;
    }

    public FoodItem createFoodItem(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    //za pravljenje novog dnevnog unosa:
    //  1. unosimo naziv namirnice i njenu kolicinu
    //  2. proverava da li postoji danasnji dnevni unos za tog usera
    //  3. ako postoji poziva update za taj isti dnevni unos
    //  4. ako ne postoji - pravimo novi dnevni unos i unosimo prvu namirnicu za taj dan
    @Transactional
    public DailyEntryResponse createDailyEntry(FoodEntryRequest foodEntryRequest) {
        //hvata usera
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);



        if(dailyEntryRepository.findByUserAndDate(user, LocalDate.now()) != null) {
            return this.updateDailyEntry(foodEntryRequest);
        } else {
            DailyEntry dailyEntry = new DailyEntry();
            dailyEntry.setDate(LocalDate.now());
            dailyEntry.setUser(user);

            FoodEntry foodEntry = new FoodEntry();
            foodEntry.setFoodItem(foodItemRepository.findByName(foodEntryRequest.getFoodName()));
            foodEntry.setGrams(foodEntryRequest.getGrams());
            foodEntry.setDailyEntry(dailyEntry);
            List<FoodEntry> foodEntries = new ArrayList<>();
            foodEntries.add(foodEntry);
            dailyEntry.setFoodEntries(foodEntries);
            dailyEntry.setTotalCalories();
            dailyEntryRepository.save(dailyEntry);
            foodEntryRepository.save(foodEntry);
            //mapiranje u DTO
            DailyEntryResponse dailyEntryResponse = new DailyEntryResponse();
            List<FoodEntryRequest> entriesDTO = new ArrayList<>();

            for(int i=0; i<foodEntries.size(); i++) {
                FoodEntryRequest feDTO = new FoodEntryRequest();
                feDTO.setFoodName(foodEntries.get(i).getFoodItem().getName());
                feDTO.setGrams(foodEntries.get(i).getGrams());
                entriesDTO.add(feDTO);
            }
            dailyEntryResponse.setEntries(entriesDTO);
            dailyEntryResponse.setId(dailyEntry.getId());
            dailyEntryResponse.setDate(dailyEntry.getDate());
            dailyEntryResponse.setTotalCalories(dailyEntry.getTotalCalories());
            return dailyEntryResponse;
        }
    }

    public DailyEntryResponse updateDailyEntry(FoodEntryRequest foodEntryRequest) {
        //hvata usera
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        DailyEntry dailyEntry = dailyEntryRepository.findByUserAndDate(user, LocalDate.now());
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setFoodItem(foodItemRepository.findByName(foodEntryRequest.getFoodName()));
        foodEntry.setGrams(foodEntryRequest.getGrams());
        foodEntry.setDailyEntry(dailyEntry);
        List<FoodEntry> entries = dailyEntry.getFoodEntries();
        entries.add(foodEntry);
        dailyEntry.setFoodEntries(entries);
        dailyEntry.setTotalCalories();
        foodEntryRepository.save(foodEntry);
        dailyEntryRepository.save(dailyEntry);
        //mapiranje u DTO
        DailyEntryResponse dailyEntryResponse = new DailyEntryResponse();
        List<FoodEntryRequest> entriesDTO = new ArrayList<>();
        for(int i=0; i<entries.size(); i++) {
            FoodEntryRequest feDTO = new FoodEntryRequest();
            feDTO.setFoodName(entries.get(i).getFoodItem().getName());
            feDTO.setGrams(entries.get(i).getGrams());
            entriesDTO.add(feDTO);
        }
        dailyEntryResponse.setEntries(entriesDTO);
        dailyEntryResponse.setId(dailyEntry.getId());
        dailyEntryResponse.setDate(dailyEntry.getDate());
        dailyEntryResponse.setTotalCalories(dailyEntry.getTotalCalories());
        return dailyEntryResponse;
    }

    public DailyEntryResponse readDailyEntry(LocalDate date) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        DailyEntry dailyEntry = dailyEntryRepository.findByUserAndDate(user, date);
        List<FoodEntry> entries = dailyEntry.getFoodEntries();

        DailyEntryResponse dailyEntryResponse = new DailyEntryResponse();
        List<FoodEntryRequest> entriesDTO = new ArrayList<>();
        for(int i=0; i<entries.size(); i++) {
            FoodEntryRequest feDTO = new FoodEntryRequest();
            feDTO.setFoodName(entries.get(i).getFoodItem().getName());
            feDTO.setGrams(entries.get(i).getGrams());
            entriesDTO.add(feDTO);
        }
        dailyEntryResponse.setEntries(entriesDTO);
        dailyEntryResponse.setId(dailyEntry.getId());
        dailyEntryResponse.setDate(dailyEntry.getDate());
        dailyEntryResponse.setTotalCalories(dailyEntry.getTotalCalories());
        return dailyEntryResponse;
    }

    public List<DailyEntryResponse> readDailyEntries() {
        // Dobavi trenutno ulogovanog korisnika
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        // Dobavi sve DailyEntry zapise za korisnika
        List<DailyEntry> dailyEntries = dailyEntryRepository.findAllByUser(user);

        // Pripremi listu DTO-ova za povratak
        List<DailyEntryResponse> responseList = new ArrayList<>();

        for (DailyEntry dailyEntry : dailyEntries) {
            List<FoodEntry> entries = dailyEntry.getFoodEntries();
            List<FoodEntryRequest> entriesDTO = new ArrayList<>();

            for (FoodEntry entry : entries) {
                FoodEntryRequest feDTO = new FoodEntryRequest();
                feDTO.setFoodName(entry.getFoodItem().getName());
                feDTO.setGrams(entry.getGrams());
                entriesDTO.add(feDTO);
            }

            DailyEntryResponse dto = new DailyEntryResponse();
            dto.setId(dailyEntry.getId());
            dto.setDate(dailyEntry.getDate());
            dto.setTotalCalories(dailyEntry.getTotalCalories());
            dto.setEntries(entriesDTO);

            responseList.add(dto);
        }

        return responseList;
    }

    public FoodItem readFoodItem(String name) {
        return foodItemRepository.findByName(name);
    }

    public List<FoodItem> readFoodItems() {
        return foodItemRepository.findAll();
    }

    @Transactional
    public Long deleteDailyEntry(LocalDate date) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        DailyEntry dailyEntry = dailyEntryRepository.findByUserAndDate(user, date);
        Long id = dailyEntry.getId();
        dailyEntryRepository.deleteByUserAndDate(user, date);
        return id;
    }

    @Transactional
    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest dto) {

        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Food item " + id + " not found"));

        item.setName(dto.getName());
        item.setCaloriesPer100g(dto.getCaloriesPer100g());

        foodItemRepository.save(item);

        FoodItemResponse resp = new FoodItemResponse();
        resp.setId(item.getId());
        resp.setName(item.getName());
        resp.setCaloriesPer100g(item.getCaloriesPer100g());

        return resp;
    }
}