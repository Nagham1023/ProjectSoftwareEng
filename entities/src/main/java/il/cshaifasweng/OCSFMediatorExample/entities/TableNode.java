
package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tables")
public class TableNode implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int tableID;


    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    private boolean isInside;
    private int capacity;
    private String status; // "reserved", "available", "occupied"

    @ElementCollection(fetch = FetchType.LAZY) // Changed to LAZY
    private List<LocalDateTime> reservationStartTimes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY) // Changed to LAZY
    private List<LocalDateTime> reservationEndTimes = new ArrayList<>();

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
