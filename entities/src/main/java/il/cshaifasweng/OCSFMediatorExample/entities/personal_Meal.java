package il.cshaifasweng.OCSFMediatorExample.entities;



import java.util.ArrayList;
import java.util.List;


public class personal_Meal {

    private Meal meal;
    private List<CustomizationWithBoolean> customizationsList = new ArrayList<>();

    public personal_Meal(Meal meal, List<CustomizationWithBoolean> customizationsList) {
        this.meal = meal;
        this.customizationsList = customizationsList;
    }


    public List<CustomizationWithBoolean> getCustomizationsList() {
        return customizationsList;
    }

    public void setCustomizationsList(List<CustomizationWithBoolean> customizationsList) {
        this.customizationsList = customizationsList;
    }
    public Meal getMeal() {
        return meal;
    }
    public void setMeal(Meal meal) {
        this.meal = meal;
    }
}
