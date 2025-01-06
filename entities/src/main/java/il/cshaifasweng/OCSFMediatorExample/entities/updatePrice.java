package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

public class updatePrice implements Serializable {
    private double newPrice;
    private int idMeal;
    private static final long serialVersionUID = -8224097662914849957L;

    public updatePrice(double newPrice, int idMeal) {
        this.newPrice = newPrice;
        this.idMeal = idMeal;
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
}
