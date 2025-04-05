    package il.cshaifasweng.OCSFMediatorExample.entities;

    import org.hibernate.annotations.BatchSize;

    import javax.persistence.*;
    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;

    @Entity
    @Table(name = "personal_details")
    public class PersonalDetails implements Serializable {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String email;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(nullable = false, length = 20)
        private String phoneNumber;

        // Correct @ManyToMany mapping with the "mappedBy" attribute indicating the owning side
        @ManyToMany(mappedBy = "personalDetails", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private List<CreditCard> creditCardDetails = new ArrayList<>();




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
            //creditCard.getPersonalDetails().add(this);  // Ensure bidirectional synchronization
        }
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
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
