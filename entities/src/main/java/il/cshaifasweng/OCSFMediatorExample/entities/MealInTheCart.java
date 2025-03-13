package il.cshaifasweng.OCSFMediatorExample.entities;

public class MealInTheCart {
    private personal_Meal meal;
    private int quantity;

    // Constructor
    public MealInTheCart(personal_Meal meal, int quantity) {
        this.meal = meal;
        this.quantity = quantity;
    }

    // Getters and Setters
    public personal_Meal getMeal() {
        return meal;
    }

    public void setMeal(personal_Meal meal) {
        this.meal = meal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
