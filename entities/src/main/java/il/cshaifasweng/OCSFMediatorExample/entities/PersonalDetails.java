package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personal_details")
public class PersonalDetails implements Serializable {

    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "personalDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreditCard> creditCardDetails = new ArrayList<>();

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<CreditCard> getCreditCardDetails() {
        return creditCardDetails;
    }

    public void setCreditCardDetails(List<CreditCard> creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }

    public void addCreditCard(CreditCard creditCard) {
        creditCardDetails.add(creditCard);
        creditCard.setPersonalDetails(this);  // Ensure bidirectional synchronization
    }

    @Override
    public String toString() {
        return "PersonalDetails{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
