package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListOfCC implements Serializable {
    private List<CreditCard> creditCards = new ArrayList<>();

    public List<CreditCard> getCreditCards() {
        return creditCards;
    }
    public void setCreditCards(List<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }
    public ListOfCC(List<CreditCard> creditCards) {
        this.creditCards = creditCards;

    }

}