package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToMany;

@Entity
@Table(name = "customizations")
public class Customization implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customizationName;

//    @ManyToMany(
//            mappedBy = "customizations",
//            fetch = FetchType.EAGER
//    )
//
//    private List<Meal> meals;
    @ManyToMany(mappedBy = "customizations", fetch = FetchType.EAGER)
    private List<Meal> meals = new ArrayList<>();

    // Constructor
    public Customization() {
        this.meals = new ArrayList<>(); // Redundant if initialized above, but safe
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return customizationName;
    }

    public void setName(String customizationName) {
        this.customizationName = customizationName;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
}
