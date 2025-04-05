package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class updatePrice implements Serializable {
    private int newPrice;
    private int idMeal;
    private String purpose;
    private static final long serialVersionUID = -8224097662914849957L;

    public updatePrice(int newPrice, int idMeal,String purpose) {
        this.newPrice = newPrice;
        this.idMeal = idMeal;
        this.purpose = purpose;
    }
    public int getNewPrice() {
        return newPrice;
    }
    public int getIdMeal() {
        return idMeal;
    }
    public void setNewPrice(int newPrice) {
        this.newPrice = newPrice;
    }
    public void setIdMeal(int idMeal) {
        this.idMeal = idMeal;
    }
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return ("the purpose was" + purpose +" the new price is" + newPrice+" the meal ID is"+ idMeal);
    }
}
