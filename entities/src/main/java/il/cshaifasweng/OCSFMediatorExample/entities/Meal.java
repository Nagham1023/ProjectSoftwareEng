package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "meals")
public class Meal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String mealName;
    private String description;
    private double price;
    private boolean isCompany;
    private boolean isDelivery;
    private double discount_percentage = 0;



    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<personal_Meal> personalMeals = new ArrayList<>();

    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB") // Or LONGBLOB if needed
    private byte[] image;

    @ManyToMany(mappedBy = "meals", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Restaurant> restaurants;


    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UpdatePriceRequest> priceRequests = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "meal_customizations",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "customization_id")
    )
    private Set<Customization> customizations = new HashSet<>();

//    // Relationship with Restaurant (separate)
//    @ManyToMany(mappedBy = "meals", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    private List<Restaurant> restaurantAssociations = new ArrayList<>();


    public List<personal_Meal> getPersonalMeals() {
        return personalMeals;
    }
    public void setPersonalMeals(List<personal_Meal> personalMeals) {
        this.personalMeals = personalMeals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mealName;
    }

    public void setName(String mealName) {
        this.mealName = mealName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public void setCompany(boolean company) {
        isCompany = company;
    }

    public boolean isDelivery() {
        return isDelivery;
    }

    public void setDelivery(boolean delivery) {
        isDelivery = delivery;
    }

    public Set<Customization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(Set<Customization> customizations) {
        this.customizations = customizations;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }


    public List<UpdatePriceRequest> getPriceRequests() {
        return priceRequests;
    }

    public void setPriceRequests(List<UpdatePriceRequest> priceRequests) {
        this.priceRequests = priceRequests;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }
    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", mealName='" + mealName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", isCompany=" + isCompany +
                ", isDelivery=" + isDelivery +
                ", imageSize=" + (image != null ? image.length : 0) + " bytes" +
                '}';
    }

    public double getDiscount_percentage() {
        return discount_percentage;
    }
    public void setDiscount_percentage(double discount_percentage) {
        this.discount_percentage = discount_percentage;
    }

}
