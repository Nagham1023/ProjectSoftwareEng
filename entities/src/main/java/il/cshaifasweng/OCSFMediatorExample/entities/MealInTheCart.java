package il.cshaifasweng.OCSFMediatorExample.entities;
import javax.persistence.*;
import java.io.Serializable;


@Entity
public class MealInTheCart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Add primary key

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "personal_meal_id", referencedColumnName = "id")
    private personal_Meal meal;
    private int quantity;
    private String RestaurantName;
    private double discount_percentage = 0;
    private double price; //price before discount

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)  // Maps to the 'Order' entity
    private Order order;

    // Constructor
    public MealInTheCart(personal_Meal meal, int quantity) {
        this.meal = meal;
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public MealInTheCart() {

    }

    public String getRestaurantName() {
        return RestaurantName;
    }
    public void setRestaurantName(String RestaurantName) {
        this.RestaurantName = RestaurantName;
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

    @Override
    public String toString() {
        return "MealInTheCart{" +
                "id=" + id +
                ", Meal=" + (meal != null ? meal.toString() : "null") +
                ", Quantity=" + quantity +
                ", Restaurant Name='" + RestaurantName + '\'' +
                '}';
    }

    public double getDiscount_percentage() {
        return discount_percentage;
    }
    public void setDiscount_percentage(double discount_percentage) {
        this.discount_percentage = discount_percentage;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}