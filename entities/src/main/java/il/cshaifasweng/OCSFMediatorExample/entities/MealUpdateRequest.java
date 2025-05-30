package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class MealUpdateRequest implements Serializable {
    private String mealId;
    private String mealName;
    private String mealDescription;
    private double oldPrice;
    private double newPrice;
    private String status;
    private byte[] image;
    private double oldDiscount;
    private double newDiscount;

    // Constructor, getters, and setters
    public MealUpdateRequest(String mealId, String mealName, String mealDescription, byte[] image,
                             double oldPrice, double newPrice,double oldDiscount, double newDiscount) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealDescription = mealDescription;
        this.image = image;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.oldDiscount = oldDiscount;
        this.newDiscount = newDiscount;
        this.status = "New";
    }

    public MealUpdateRequest() {

    }

    public double getNewDiscount() {

        return newDiscount;
    }
    public void setNewDiscount(double newDiscount) {
        this.newDiscount = newDiscount;
    }

    public double getOldDiscount() {
        return oldDiscount;
    }
    public void setOldDiscount(double oldDiscount) {
        this.oldDiscount = oldDiscount;
    }

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
