package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class PaymentCheck implements Serializable {
    private CreditCard creditCard;
    private PersonalDetails personalDetails;
    private String response;
    private Order order;

    public PaymentCheck(CreditCard creditCard, PersonalDetails personalDetails,Order order) {
        this.creditCard = creditCard;
        this.personalDetails = personalDetails;
        this.order = order;
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
}
