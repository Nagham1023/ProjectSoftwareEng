package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.List;

public class FaildPayRes implements Serializable {
    private List<ReservationEvent> reservationList;
    private boolean hasBeenHereBefore;
    private PersonalDetails personalDetails;

    public FaildPayRes(List<ReservationEvent> reservationList) {
        this.reservationList = reservationList;
    }

    public List<ReservationEvent> getReservationList() {
        return reservationList;
    }

    public void setReservationList(List<ReservationEvent> reservationList) {
        this.reservationList = reservationList;
    }

    public boolean getHasBeenHereBefore() {
        return hasBeenHereBefore;
    }

    public void setHasBeenHereBefore(boolean hasBeenHereBefore) {
        this.hasBeenHereBefore = hasBeenHereBefore;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }
}
