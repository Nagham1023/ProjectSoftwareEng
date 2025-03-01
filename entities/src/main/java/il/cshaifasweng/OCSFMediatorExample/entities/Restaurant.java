package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;

import java.io.Serializable;

import java.util.List;

@Entity
@Table(name = "restaurants")

public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    private String restaurantName;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TableNode> tables;

    @ManyToMany  // Many-to-many relationship with Meal
    @JoinTable(
            name = "restaurant_meals",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> meals;

    // Getters and Setters


    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "phone_number")
    private String phoneNumber;


    @OneToMany(mappedBy = "restaurant")
    private List<Complain> complains;


    //@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //private List<Order> orders;

    // Default constructor
    public Restaurant() {
    }

    // Constructor with parameters
    public Restaurant(int id, String restaurantName, String imagePath, String phoneNumber) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.imagePath = imagePath;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }


    public List<TableNode> getTables() {
        return tables;
    }

    public void setTables(List<TableNode> tables) {
        this.tables = tables;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Meal> getMeals() {

        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    public List<Complain> getComplains() {
        return complains;
    }

    public void setComplains(List<Complain> complains) {
        this.complains = complains;
    }

    @Override
    public String toString() {
        return "Restaurant" +
                "ID='" + getId() + '\'' +
                ", RestaurantName='" + getRestaurantName() + '\'' +
                ", IMG='" + getImagePath() + '\'' +
                ", PhoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}


