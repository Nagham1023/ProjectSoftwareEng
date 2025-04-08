package il.cshaifasweng.OCSFMediatorExample.entities;



import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class personal_Meal implements Serializable {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key
    @OneToOne(mappedBy = "meal", cascade = CascadeType.ALL)
    private MealInTheCart mealInTheCart;



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "meal_id", referencedColumnName = "id")  // This will link to the 'Meal' entity
    private Meal meal;

    @OneToMany( orphanRemoval = true,fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "customization_with_boolean", joinColumns = @JoinColumn(name = "personal_meal_id"))
    private Set<CustomizationWithBoolean> customizationsList = new HashSet<>();



    public personal_Meal(Meal meal, Set<CustomizationWithBoolean> customizationsList) {
        this.meal = meal;
        this.customizationsList = customizationsList;
    }

    public personal_Meal() {

    }


    public Set<CustomizationWithBoolean> getCustomizationsList() {
        return customizationsList;
    }
    public void setCustomizationsList(Set<CustomizationWithBoolean> customizationsList) {
        this.customizationsList = customizationsList;
    }
    public Meal getMeal() {
        return meal;
    }
    public void setMeal(Meal meal) {
        this.meal = meal;
    }
    public String toString() {
        return meal.toString();
    }
}