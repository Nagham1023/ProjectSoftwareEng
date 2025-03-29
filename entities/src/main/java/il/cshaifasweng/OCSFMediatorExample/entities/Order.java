package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    //@ManyToOne
    // @JoinColumn(name = "restaurant_id")
    //private Restaurant restaurant;

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
}
