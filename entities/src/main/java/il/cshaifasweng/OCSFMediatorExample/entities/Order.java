package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int restaurantId;
    private String restaurantName;
    private LocalDate date;
    private String orderType;
    private int total_price;
    private String orderStatus;
    private String customerEmail;
    private LocalDateTime orderTime;
    private String creditCard_num;


    /***yousef***/
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<MealInTheCart> meals = new ArrayList<>();




    public Order() {}

    public Order(Order order) {
        this.id = order.getId();
        this.restaurantId = order.getRestaurantId();
        this.restaurantName = order.getRestaurantName();
        this.date = order.getDate();
        this.orderType = order.getOrderType();
        this.total_price = order.getTotal_price();
        this.orderStatus = order.getOrderStatus();
        this.customerEmail = order.getCustomerEmail();
        this.orderTime = order.getOrderTime();
        this.meals = order.getMeals();
        this.creditCard_num =order.getCreditCard_num();
    }

    public String getCreditCard_num() {

        return creditCard_num;
    }
    public void setCreditCard_num(String creditCard_num) {
        this.creditCard_num = creditCard_num;
    }

    public List<MealInTheCart> getMeals() {
        return meals;
    }
    public void setMeals(List<MealInTheCart> meals) {
        this.meals = meals;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getOrderStatus() {return orderStatus;}

    public void setOrderStatus(String orderStatus) {this.orderStatus = orderStatus;}

    public String getCustomerEmail() {return customerEmail;}

    public void setCustomerEmail(String email) {this.customerEmail = email;}

    public LocalDateTime getOrderTime() {return orderTime;}

    public void setOrderTime(LocalDateTime orderTime) {this.orderTime=orderTime;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order Details:\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Restaurant ID: ").append(restaurantId).append("\n");
        sb.append("Restaurant Name: ").append(restaurantName).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Order Type: ").append(orderType).append("\n");
        sb.append("Total Price: ").append(total_price).append("\n");
        sb.append("Order Status: ").append(orderStatus).append("\n");
        sb.append("Customer Email: ").append(customerEmail).append("\n");
        sb.append("Order Time: ").append(orderTime).append("\n");

        sb.append("Meals in the Order:\n");
        for (MealInTheCart meal : meals) {
            sb.append(meal.toString()).append("\n");
        }

        return sb.toString();
    }

}
