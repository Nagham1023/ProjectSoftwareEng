package il.cshaifasweng.OCSFMediatorExample.entities;

public class CustomizationWithBoolean {
    Customization customization;
    Boolean value;
    public CustomizationWithBoolean(Customization customization, Boolean value) {
        this.customization = customization;
        this.value = value;
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
