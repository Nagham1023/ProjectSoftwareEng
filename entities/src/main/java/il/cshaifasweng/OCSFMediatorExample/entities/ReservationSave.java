package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reservation_saves")
public class ReservationSave implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int reservationSaveID; // Unique ID for the reservation save

    private String restaurantName;
    private LocalDateTime reservationDateTime;
    private int seats;
    private boolean isInside;

    private String fullName;
    private String phoneNumber;
    private String email;
    private String creditCard_num;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reservation_save_tables", // Join table to map reservations to tables
            joinColumns = @JoinColumn(name = "reservation_save_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<TableNode> tables; // List of tables associated with this reservation

    // Constructors
    public ReservationSave() {}

    public ReservationSave(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside,
                           String fullName, String phoneNumber, String email, List<TableNode> tables) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.tables = tables;
    }

    // Getters and Setters
    public int getReservationSaveID() {
        return reservationSaveID;
    }

    public void setReservationSaveID(int reservationSaveID) {
        this.reservationSaveID = reservationSaveID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        if (seats > 0) {
            this.seats = seats;
        } else {
            throw new IllegalArgumentException("Seats must be greater than zero.");
        }
    }

    public boolean isInside() {
        return isInside;
    }

    public void setInside(boolean inside) {
        isInside = inside;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<TableNode> getTables() {
        return tables;
    }

    public void setTables(List<TableNode> tables) {
        this.tables = tables;
    }
    public String getCreditCard_num() {
        return creditCard_num;
    }

    public void setCreditCard_num(String creditCard_num) {
        this.creditCard_num = creditCard_num;
    }


    @Override
    public String toString() {
        return "ReservationSave{" +
                "reservationSaveID=" + reservationSaveID +
                ", restaurantName='" + restaurantName + '\'' +
                ", reservationDateTime=" + reservationDateTime +
                ", seats=" + seats +
                ", isInside=" + isInside +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", tables=" + tables +
                '}';
    }
}