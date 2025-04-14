package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "updatepricerequest")
public class UpdatePriceRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "meal_id") // Maps to the meal_id column in the requests table
    private Meal meal; // Reference to the Meal entity
    private double oldPrice;
    private double newPrice;
    private double oldDiscount;
    private double newDiscount;


    public double getOldDiscount(){
        return oldDiscount;
    }
    public void setOldDiscount(double oldDiscount){
        this.oldDiscount = oldDiscount;
    }
    public double getNewDiscount(){
        return newDiscount;
    }
    public void setNewDiscount(double newDiscount){
        this.newDiscount = newDiscount;
    }
    public Meal getMeal() {
        return meal;
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(double newPrice) {
        this.newPrice = newPrice;
    }

    public int getId() {
        return id;
    }
}
