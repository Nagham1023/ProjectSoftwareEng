package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;

public class CreditCardCheck implements Serializable {
    private String cardNumber;
    private String cardholderName;
    private String cardholdersID;
    private String cvv;
    private String personalEmail;
    private String expiryDate;
    private boolean isNewCard;  // Indicator whether this is a new card or validation request
    private boolean isValid;
    private String respond;

    // Getters and setters for all fields
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardholderName() { return cardholderName; }
    public void setCardholderName(String cardholderName) { this.cardholderName = cardholderName; }
    public String getCardholdersID() { return cardholdersID; }
    public void setCardholdersID(String cardholdersID) { this.cardholdersID = cardholdersID; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }
    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public boolean isNewCard() { return isNewCard; }
    public void setNewCard(boolean newCard) { isNewCard = newCard; }
    public boolean isValid() { return isValid; }
    public void setValid(boolean valid) { isValid = valid; }
    public String getRespond() { return respond; }
    public void setRespond(String respond) { this.respond = respond; }
}
