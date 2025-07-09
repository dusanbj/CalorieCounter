package rs.fon.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.fon.demo.model.FoodItem;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    FoodItem findByName(String name);
}
