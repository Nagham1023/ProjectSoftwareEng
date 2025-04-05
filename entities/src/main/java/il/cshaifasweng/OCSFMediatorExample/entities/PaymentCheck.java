package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class PaymentCheck implements Serializable {
    private CreditCard creditCard;
    private PersonalDetails personalDetails;
    private String response;
    private Order order;
    private ReservationSave reservationEvent;
    private String mode;

    public PaymentCheck(CreditCard creditCard, PersonalDetails personalDetails,Order order, String mode) {
        this.creditCard = creditCard;
        this.personalDetails = personalDetails;
        this.order = order;
        this.mode = mode;
    }

    public PaymentCheck(CreditCard creditCard, PersonalDetails personalDetails, ReservationSave reservationEvent, String mode) {
        this.creditCard = creditCard;
        this.personalDetails = personalDetails;
        this.reservationEvent = reservationEvent;
        this.mode = mode;
    }

    public PaymentCheck(CreditCard creditCard, PersonalDetails personalDetails, String mode) {
        this.creditCard = creditCard;
        this.personalDetails = personalDetails;
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }
    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }
    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response = response;
    }

    public ReservationSave getReservationEvent() {
        return reservationEvent;
    }

    public void setReservationEvent(ReservationSave reservationEvent) {
        this.reservationEvent = reservationEvent;
    }
}
