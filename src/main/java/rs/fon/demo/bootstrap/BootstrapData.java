package rs.fon.demo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.fon.demo.model.FoodItem;
import rs.fon.demo.model.Role;
import rs.fon.demo.model.User;
import rs.fon.demo.repositories.FoodItemRepository;
import rs.fon.demo.repositories.UserRepository;

import java.util.Random;

@Component
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;

    private final FoodItemRepository foodItemRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BootstrapData(UserRepository userRepository, PasswordEncoder passwordEncoder, FoodItemRepository foodItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Loading Data...");

        Random random = new Random();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRole(Role.ROLE_ADMIN);
        this.userRepository.save(admin);

        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setPassword(this.passwordEncoder.encode("user" + i));
            user.setRole(Role.ROLE_USER);
            this.userRepository.save(user);
        }

        FoodItem foodItem1 = new FoodItem("banana", 89);
        FoodItem foodItem2 = new FoodItem("chicken", 165);
        FoodItem foodItem3 = new FoodItem("rice", 112);
        FoodItem foodItem4 = new FoodItem("potato", 77);
        FoodItem foodItem5 = new FoodItem("chocolate", 500);
        foodItemRepository.save(foodItem1);
        foodItemRepository.save(foodItem2);
        foodItemRepository.save(foodItem3);
        foodItemRepository.save(foodItem4);
        foodItemRepository.save(foodItem5);


        System.out.println("Data loaded!");
    }
}
