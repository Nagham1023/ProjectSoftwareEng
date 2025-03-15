package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "CreditCard")
public class CreditCard implements Serializable {

    @Id
    @Column(nullable = false, length = 9, unique = true)
    private String cardholdersID;

    @Column(nullable = false, length = 19)
    private String cardNumber;

    @Column(nullable = false, length = 100)
    private String cardholderName;

    @Column(nullable = false, length = 3)
    private String cvv;

//    @Column(nullable = false)               //should change
//    private LocalDate expiryDate;

    @Column(nullable = false, length = 7)  // "MM/yyyy"
    private String expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_email", referencedColumnName = "email")
    private PersonalDetails personalDetails;


    // Getters and setters

    public String getCardholdersID() {
        return cardholdersID;
    }

    public void setCardholdersID(String cardholdersID) {
        this.cardholdersID = cardholdersID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
        /*if (personalDetails != null) {
            // Ensuring that the personalDetails instance knows about this credit card
            personalDetails.addCreditCard(this);
        }*/
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "cardholdersID='" + cardholdersID + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", cvv='" + cvv + '\'' +
                ", expiryDate=" + expiryDate +
                ", personalEmail='" + (personalDetails != null ? personalDetails.getEmail() : null) +
                '}';
    }
}
