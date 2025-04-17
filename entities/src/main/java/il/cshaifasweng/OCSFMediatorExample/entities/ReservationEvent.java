package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationEvent implements Serializable {
    private String restaurantName;
    private LocalDateTime reservationDateTime; // Single reservation time
    private int seats;
    private boolean isInside;
    private List<LocalDateTime> availableTimeSlots; // New field for multiple time slots
    private boolean isWorker = false;
    // Constructor for single reservation
    public ReservationEvent(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
    }
    public ReservationEvent(boolean isWorker, String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = reservationDateTime;
        this.seats = seats;
        this.isInside = isInside;
        this.isWorker = isWorker;
    }
    // Constructor for single reservation
    public ReservationEvent(String restaurantName, int seats, boolean isInside) {
        this.restaurantName = restaurantName;
        this.reservationDateTime = null;
        this.seats = seats;
        this.isInside = isInside;
        this.availableTimeSlots = new ArrayList<LocalDateTime>();
    }

    // Constructor for multiple time slots
    public ReservationEvent(String restaurantName, int seats, boolean isInside, List<LocalDateTime> availableTimeSlots) {
        this.restaurantName = restaurantName;
        this.seats = seats;
        this.isInside = isInside;
        this.availableTimeSlots = availableTimeSlots;
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
        this.seats = seats;
    }

    public boolean isInside() {
        return isInside;
    }

    public boolean isWorker() {
        return isWorker;
    }

    public void setInside(boolean isInside) {
        this.isInside = isInside;
    }

    public List<LocalDateTime> getAvailableTimeSlots() {
        return availableTimeSlots;
    }

    public void setAvailableTimeSlots(List<LocalDateTime> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots;
    }

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "restaurantName='" + restaurantName + '\'' +
                ", reservationDateTime=" + reservationDateTime +
                ", seats=" + seats +
                ", isInside=" + isInside +
                ", availableTimeSlots=" + availableTimeSlots +
                '}';
    }
}