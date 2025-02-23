package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tables")
public class TableNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tableID;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private boolean isInside;
    private int capacity;
    private String status; // "reserved", "available", "occupied"

    @ElementCollection
    private List<LocalDateTime> reservationStartTimes;

    @ElementCollection
    private List<LocalDateTime> reservationEndTimes;

    @Transient
    private TableNode next; // This is not stored in the database

    // Constructors
    public TableNode() {}

    public TableNode(Restaurant restaurant, boolean isInside, int capacity, String status) {
        this.restaurant = restaurant;
        this.isInside = isInside;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean inside) {
        isInside = inside;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LocalDateTime> getReservationStartTimes() {
        return reservationStartTimes;
    }

    public void setReservationStartTimes(List<LocalDateTime> reservationStartTimes) {
        this.reservationStartTimes = reservationStartTimes;
    }

    public List<LocalDateTime> getReservationEndTimes() {
        return reservationEndTimes;
    }

    public void setReservationEndTimes(List<LocalDateTime> reservationEndTimes) {
        this.reservationEndTimes = reservationEndTimes;
    }



    public TableNode getNext() {
        return next;
    }

    public void setNext(TableNode next) {
        this.next = next;
    }
}
