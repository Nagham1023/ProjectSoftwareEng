package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class updatePrice implements Serializable {
    private double newPrice;
    private int idMeal;
    private String purpose;
    private double discount;
    private static final long serialVersionUID = -8224097662914849957L;

    public updatePrice(double newPrice, int idMeal,double discount,String purpose) {
        this.newPrice = newPrice;
        this.idMeal = idMeal;
        this.purpose = purpose;
        this.discount = discount;
    }
    public double getNewPrice() {
        return newPrice;
    }
    public int getIdMeal() {
        return idMeal;
    }
    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }
    public void setIdMeal(int idMeal) {
        this.idMeal = idMeal;
    }
    public String getPurpose() {
        return purpose;
    }
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Override
    public String toString() {
        return ("the purpose was" + purpose +" the new price is" + newPrice+" the meal ID is"+ idMeal);
    }
}
