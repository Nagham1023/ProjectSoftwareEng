package il.cshaifasweng.OCSFMediatorExample.entities;

public class MealInTheCart {
    private Meal meal;
    private int quantity;

    // Constructor
    public MealInTheCart(Meal meal, int quantity) {
        this.meal = meal;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
