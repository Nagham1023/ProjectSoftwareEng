package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FinalReservationEvent implements Serializable {
    private String restaurantName;
    private LocalDateTime reservationDateTime;
    private int seats;
    private boolean isInside; // New attribute for inside/outside reservation

    // Constructor
    public FinalReservationEvent(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
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