package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FinalReservationEvent implements Serializable {
    private String restaurantName;
    private LocalDateTime reservationDateTime;
    private int seats;
    private boolean isInside; // New attribute for inside/outside reservation

    private String fullName; // New field for user's full name

    private String phoneNumber; // New field for user's phone number

    private String email; // New field for user's email
    private String creditCard_num;
    private boolean isWorker = false;

    // Constructor with all fields
    public FinalReservationEvent(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside,
                                 String fullName, String phoneNumber, String email) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
    public FinalReservationEvent(boolean isWorker, String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside,
                                 String fullName, String phoneNumber, String email) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.isWorker = isWorker;
    }

    // Getters and Setters
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

    public void setInside(boolean isInside) {
        this.isInside = isInside;
    }

    public boolean isWorker() {
        return isWorker;
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

    public String getCreditCard_num() {
        return creditCard_num;
    }

    public void setCreditCard_num(String creditCard_num) {
        this.creditCard_num = creditCard_num;
    }

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "restaurantName='" + restaurantName + '\'' +
                ", reservationDateTime=" + reservationDateTime +
                ", seats=" + seats +
                ", isInside=" + isInside +
                '}';
    }
}