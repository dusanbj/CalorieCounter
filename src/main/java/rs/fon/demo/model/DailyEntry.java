package rs.fon.demo.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "daily_entry",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_user_id", "date"})
        }
)
public class DailyEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(optional = false)
    private User user;

    @OneToMany(mappedBy = "dailyEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodEntry> foodEntries;

    public double totalCalories;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<FoodEntry> getFoodEntries() {
        return foodEntries;
    }

    public void setFoodEntries(List<FoodEntry> foodEntries) {
        this.foodEntries = foodEntries;
    }

    public void setTotalCalories() {
        totalCalories = this.getTotalCalories();
    }

    public double getTotalCalories() {
        return foodEntries != null
                ? foodEntries.stream().mapToDouble(FoodEntry::getTotalCalories).sum()
                : 0;
    }
}