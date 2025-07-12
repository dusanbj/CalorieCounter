package rs.fon.demo.services;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.demo.dto.DailyEntryResponseDTO;
import rs.fon.demo.dto.FoodEntryRequestDTO;
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
import java.util.Optional;

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
    public DailyEntryResponseDTO createDailyEntry(FoodEntryRequestDTO foodEntryRequestDTO) {
        //hvata usera
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        if(dailyEntryRepository.findByUserAndDate(user, LocalDate.now()) != null) {
            return this.updateDailyEntry(foodEntryRequestDTO);
        } else {
            DailyEntry dailyEntry = new DailyEntry();
            dailyEntry.setDate(LocalDate.now());
            dailyEntry.setUser(user);

            FoodEntry foodEntry = new FoodEntry();
            foodEntry.setFoodItem(foodItemRepository.findByName(foodEntryRequestDTO.getFoodName()));
            foodEntry.setGrams(foodEntryRequestDTO.getGrams());
            foodEntry.setDailyEntry(dailyEntry);
            List<FoodEntry> foodEntries = new ArrayList<>();
            foodEntries.add(foodEntry);
            dailyEntry.setFoodEntries(foodEntries);
            dailyEntry.setTotalCalories();
            dailyEntryRepository.save(dailyEntry);
            foodEntryRepository.save(foodEntry);
            //mapiranje u DTO
            DailyEntryResponseDTO dailyEntryResponseDTO = new DailyEntryResponseDTO();
            List<FoodEntryRequestDTO> entriesDTO = new ArrayList<>();

            for(int i=0; i<foodEntries.size(); i++) {
                FoodEntryRequestDTO feDTO = new FoodEntryRequestDTO();
                feDTO.setFoodName(foodEntries.get(i).getFoodItem().getName());
                feDTO.setGrams(foodEntries.get(i).getGrams());
                entriesDTO.add(feDTO);
            }
            dailyEntryResponseDTO.setEntries(entriesDTO);
            dailyEntryResponseDTO.setId(dailyEntry.getId());
            dailyEntryResponseDTO.setDate(dailyEntry.getDate());
            dailyEntryResponseDTO.setTotalCalories(dailyEntry.getTotalCalories());
            return dailyEntryResponseDTO;
        }
    }

    public DailyEntryResponseDTO updateDailyEntry(FoodEntryRequestDTO foodEntryRequestDTO) {
        //hvata usera
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        DailyEntry dailyEntry = dailyEntryRepository.findByUserAndDate(user, LocalDate.now());
        FoodEntry foodEntry = new FoodEntry();
        foodEntry.setFoodItem(foodItemRepository.findByName(foodEntryRequestDTO.getFoodName()));
        foodEntry.setGrams(foodEntryRequestDTO.getGrams());
        foodEntry.setDailyEntry(dailyEntry);
        List<FoodEntry> entries = dailyEntry.getFoodEntries();
        entries.add(foodEntry);
        dailyEntry.setFoodEntries(entries);
        dailyEntry.setTotalCalories();
        foodEntryRepository.save(foodEntry);
        dailyEntryRepository.save(dailyEntry);
        //mapiranje u DTO
        DailyEntryResponseDTO dailyEntryResponseDTO = new DailyEntryResponseDTO();
        List<FoodEntryRequestDTO> entriesDTO = new ArrayList<>();
        for(int i=0; i<entries.size(); i++) {
            FoodEntryRequestDTO feDTO = new FoodEntryRequestDTO();
            feDTO.setFoodName(entries.get(i).getFoodItem().getName());
            feDTO.setGrams(entries.get(i).getGrams());
            entriesDTO.add(feDTO);
        }
        dailyEntryResponseDTO.setEntries(entriesDTO);
        dailyEntryResponseDTO.setId(dailyEntry.getId());
        dailyEntryResponseDTO.setDate(dailyEntry.getDate());
        dailyEntryResponseDTO.setTotalCalories(dailyEntry.getTotalCalories());
        return dailyEntryResponseDTO;
    }

    public DailyEntryResponseDTO readDailyEntry(LocalDate date) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        DailyEntry dailyEntry = dailyEntryRepository.findByUserAndDate(user, date);
        List<FoodEntry> entries = dailyEntry.getFoodEntries();

        DailyEntryResponseDTO dailyEntryResponseDTO = new DailyEntryResponseDTO();
        List<FoodEntryRequestDTO> entriesDTO = new ArrayList<>();
        for(int i=0; i<entries.size(); i++) {
            FoodEntryRequestDTO feDTO = new FoodEntryRequestDTO();
            feDTO.setFoodName(entries.get(i).getFoodItem().getName());
            feDTO.setGrams(entries.get(i).getGrams());
            entriesDTO.add(feDTO);
        }
        dailyEntryResponseDTO.setEntries(entriesDTO);
        dailyEntryResponseDTO.setId(dailyEntry.getId());
        dailyEntryResponseDTO.setDate(dailyEntry.getDate());
        dailyEntryResponseDTO.setTotalCalories(dailyEntry.getTotalCalories());
        return dailyEntryResponseDTO;
    }

    public List<DailyEntryResponseDTO> readDailyEntries() {
        // Dobavi trenutno ulogovanog korisnika
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        // Dobavi sve DailyEntry zapise za korisnika
        List<DailyEntry> dailyEntries = dailyEntryRepository.findAllByUser(user);

        // Pripremi listu DTO-ova za povratak
        List<DailyEntryResponseDTO> responseList = new ArrayList<>();

        for (DailyEntry dailyEntry : dailyEntries) {
            List<FoodEntry> entries = dailyEntry.getFoodEntries();
            List<FoodEntryRequestDTO> entriesDTO = new ArrayList<>();

            for (FoodEntry entry : entries) {
                FoodEntryRequestDTO feDTO = new FoodEntryRequestDTO();
                feDTO.setFoodName(entry.getFoodItem().getName());
                feDTO.setGrams(entry.getGrams());
                entriesDTO.add(feDTO);
            }

            DailyEntryResponseDTO dto = new DailyEntryResponseDTO();
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
}