package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class UpdateMealEvent implements Serializable {
    private String mealId;
    private Meal meal;
    private String Status;

    public UpdateMealEvent(String mealId) {
        this.mealId = mealId;
    }

    public UpdateMealEvent(Meal meal, String mealId) {
        this.meal = meal;
        this.mealId = mealId;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
