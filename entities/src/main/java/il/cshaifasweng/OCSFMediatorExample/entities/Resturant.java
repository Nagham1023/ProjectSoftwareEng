package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "resturants")
public class Resturant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String resturant_Name;

    @OneToMany(mappedBy = "resturant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getResturant_Name() {
        return resturant_Name;
    }

    public void setResturant_Name(String resturant_Name) {
        this.resturant_Name = resturant_Name;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
}
