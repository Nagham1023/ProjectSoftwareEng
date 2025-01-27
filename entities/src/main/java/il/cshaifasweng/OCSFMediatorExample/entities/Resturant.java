package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "resturants")
public class Resturant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String resturant_Name;

    @OneToMany(mappedBy = "resturant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    @OneToMany(mappedBy = "complainsForRes")
    private List<Complain> complains;



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
    public List<Complain> getComplains() {return complains;}
    public void setComplains(List<Complain> complains) {}

}
