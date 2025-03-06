package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MealsList implements Serializable {
    private static final long serialVersionUID = -8224097662914849956L;
    private List<Meal> meals;
    public MealsList(List<Meal> mealsList) {
        this.meals = mealsList;
    }
    public List<Meal> getMeals() {
        return meals;
    }
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
    public void addMeal(Meal meal) {
        this.meals.add(meal);
    }
}
