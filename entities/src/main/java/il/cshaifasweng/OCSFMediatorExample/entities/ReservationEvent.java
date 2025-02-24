package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReservationEvent implements Serializable {
    private String restaurantName;
    private LocalDateTime reservationDateTime;

    // Constructor
    public ReservationEvent(String restaurantName, LocalDateTime reservationDateTime) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
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

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "restaurantName='" + restaurantName + '\'' +
                ", reservationDateTime=" + reservationDateTime +
                '}';
    }
}
