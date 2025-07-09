package rs.fon.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.fon.demo.model.DailyEntry;
import rs.fon.demo.model.User;

import java.time.LocalDate;

@Repository
public interface DailyEntryRepository extends JpaRepository<DailyEntry, Long> {
    DailyEntry findByUserAndDate(User user, LocalDate date);
}
