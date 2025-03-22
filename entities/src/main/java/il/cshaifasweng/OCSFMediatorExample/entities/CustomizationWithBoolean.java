package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.io.Serializable;

@Entity
public class CustomizationWithBoolean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne // If one CustomizationWithBoolean is associated with one Customization
    private Customization customization;


    Boolean value;
    public CustomizationWithBoolean(Customization customization, Boolean value) {
        this.customization = customization;
        this.value = value;
    }

    public CustomizationWithBoolean() {

    }

    public Customization getCustomization() {
        return customization;
    }
    public void setCustomization(Customization customization) {
        this.customization = customization;
    }
    public Boolean getValue() {
        return value;
    }
    public void setValue(Boolean value) {
        this.value = value;
    }
}
