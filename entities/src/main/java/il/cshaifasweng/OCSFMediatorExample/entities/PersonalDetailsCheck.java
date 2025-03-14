package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class PersonalDetailsCheck implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean emailVerified; // To track if the email has been verified
    private boolean detailsComplete; // To track if all details are provided
    private String respond; // To store response messages

    // Constructors
    public PersonalDetailsCheck() {
    }

    public PersonalDetailsCheck(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.emailVerified = false;
        this.detailsComplete = false;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isDetailsComplete() {
        return detailsComplete;
    }

    public void setDetailsComplete(boolean detailsComplete) {
        this.detailsComplete = detailsComplete;
    }

    public String getRespond() {
        return respond;
    }

    public void setRespond(String respond) {
        this.respond = respond;
    }

    // To string method for easy logging and debugging
    @Override
    public String toString() {
        return "PersonalDetailsCheck{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailVerified=" + emailVerified +
                ", detailsComplete=" + detailsComplete +
                ", respond='" + respond + '\'' +
                '}';
    }
}
