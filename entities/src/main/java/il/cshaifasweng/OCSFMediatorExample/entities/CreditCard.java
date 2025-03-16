package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CreditCard")
public class CreditCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generated ID
    private Long id;


    @Column(nullable = false, length = 9)
    private String cardholdersID;

    @Column(nullable = false, length = 19)
    private String cardNumber;

    @Column(nullable = false, length = 100)
    private String cardholderName;

    @Column(nullable = false, length = 3)
    private String cvv;

    @Column(nullable = false, length = 7)  // "MM/yyyy"
    private String expiryDate;

    // Correct @ManyToMany mapping with the @JoinTable annotation
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "personal_creditcard", // Join table name
            joinColumns = @JoinColumn(name = "creditcard_id", referencedColumnName = "id"), // Reference CreditCard's id
            inverseJoinColumns = @JoinColumn(name = "personaldetails_id", referencedColumnName = "id") // Reference PersonalDetails's idd
    )
    private List<PersonalDetails> personalDetails = new ArrayList<>();

    // Getters and setters

    public String getCardholdersID() {
        return cardholdersID;
    }

    public Long getId() {

        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public List<PersonalDetails> getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails.add(personalDetails);
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "cardholdersID='" + cardholdersID + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardholderName='" + cardholderName + '\'' +
                ", cvv='" + cvv + '\'' +
                ", expiryDate=" + expiryDate +
                ", personalDetails=" + personalDetails +
                '}';
    }
}
