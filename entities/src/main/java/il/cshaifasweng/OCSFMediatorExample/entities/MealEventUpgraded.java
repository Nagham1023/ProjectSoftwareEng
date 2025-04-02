package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class MealEventUpgraded implements Serializable {
    private static final long serialVersionUID = -8224097662914849956L;

    private String mealName;
    private String mealDisc;
    private String price;
    private String Id;
    private byte[] image;
    private boolean isCompany;
    private List<String> customizationList;
    private List<String> branch;
    private Meal meal;

    public MealEventUpgraded(String mealName, String mealDisc, String price, String Id, byte[] image) {
        this.mealName = mealName;
        this.mealDisc = mealDisc;
        this.price = price;
        this.Id = Id;
        this.image = image;

    }
    public MealEventUpgraded(String mealName, String mealDisc, String price, byte[] image) {
        this.mealName = mealName;
        this.mealDisc = mealDisc;
        this.price = price;
        this.image = image;

    }

    public MealEventUpgraded(String mealName, String mealDisc, String price, byte[] image, boolean isCompany, List<String> customizationList, List<String> branch) {
        this.mealName = mealName;
        this.mealDisc = mealDisc;
        this.price = price;
        this.image = image;
        this.isCompany = isCompany;
        this.customizationList = customizationList;
        this.branch = branch;
    }

    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    public String getMealName() {
        return mealName;
    }
    public String getMealDisc() {
        return mealDisc;
    }
    public String getPrice() {
        return price;
    }
    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    public void setMealDisc(String mealDisc) {
        this.mealDisc = mealDisc;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getId() {
        return Id;
    }
    public void setId(String Id) {
        this.Id = Id;
    }

    public List<String> getCustomizationList() {
        return customizationList;
    }

    public void setCustomizationList(List<String> customizationList) {
        this.customizationList = customizationList;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public void setCompany(boolean company) {
        isCompany = company;
    }


    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public List<String> getBranch() {
        return branch;
    }

    public void setBranch(List<String> branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "mealEvent{" +
                "mealName='" + mealName + '\'' +
                ", mealDisc='" + mealDisc + '\'' +
                ", price='" + price + '\'' +
                ", Id='" + Id + '\'' +
                '}';
    }
}
