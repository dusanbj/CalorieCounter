package rs.fon.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.fon.demo.model.FoodEntry;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
}
