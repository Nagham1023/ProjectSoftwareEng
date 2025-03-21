package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class PaymentCheck implements Serializable {
    private CreditCard creditCard;
    private PersonalDetails personalDetails;
    private String response;

    public PaymentCheck(CreditCard creditCard, PersonalDetails personalDetails) {
        this.creditCard = creditCard;
        this.personalDetails = personalDetails;
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
