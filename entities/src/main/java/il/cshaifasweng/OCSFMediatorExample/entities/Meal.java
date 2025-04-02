package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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


    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB") // Or LONGBLOB if needed
    private byte[] image;

    @ManyToMany(mappedBy = "meals", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Restaurant> restaurants;


//    // Configure cascade deletion for the relationship
//    @OneToMany(
//            mappedBy = "meal",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    private List<RestaurantMeal> restaurantAssociations = new ArrayList<>();

//    @ManyToMany
//    @JoinTable(
//            name = "meal_customizations",
//            joinColumns = {@JoinColumn(
//                    name = "meal_id"
//            )},
//            inverseJoinColumns = {@JoinColumn(
//                    name = "customization_id"
//            )}
//    )
//    private List<Customization> customizations;
//
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UpdatePriceRequest> priceRequests = new ArrayList<>();


    @ManyToMany
    @JoinTable(
            name = "meal_customizations",
            joinColumns = @JoinColumn(name = "meal_id"),
            inverseJoinColumns = @JoinColumn(name = "customization_id")
    )
    private List<Customization> customizations = new ArrayList<>(); // Initialized

        // Relationship with Restaurant (separate)
        @ManyToMany(mappedBy = "meals", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        private List<Restaurant> restaurantAssociations = new ArrayList<>();

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

    public List<Customization> getCustomizations() {
        return customizations;
    }

    public void setCustomizations(List<Customization> customizations) {
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

    public List<Restaurant> getRestaurantAssociations() {
        return restaurantAssociations;
    }

    public void setRestaurantAssociations(List<Restaurant> restaurantAssociations) {
        this.restaurantAssociations = restaurantAssociations;
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

}
