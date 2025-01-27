package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Meal> meals;

    @OneToMany(mappedBy = "complainsForRes")
    private List<Complain> complains;



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

    public List<Complain> getComplains() {return complains;}
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
