package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class CancelReservationEvent implements Serializable {
    private final String ReservationId;
    private String CustomerEmail;
    private ReservationSave reservation;

    public CancelReservationEvent(ReservationSave reservation) {
        this.reservation = reservation;
    }

    public CancelReservationEvent(String reservationId) {
        ReservationId = reservationId;
    }

    public CancelReservationEvent(String reservationId, String customerEmail) {
        ReservationId = reservationId;
        CustomerEmail = customerEmail;
    }

    public String getReservationId() {
        return ReservationId;
    }

    public ReservationSave getReservation() {
        return reservation;
    }

    public String getCustomerEmail() {
        return CustomerEmail;
    }
}
