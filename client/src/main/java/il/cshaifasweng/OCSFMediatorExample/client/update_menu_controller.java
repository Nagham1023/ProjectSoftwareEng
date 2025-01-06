package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class update_menu_controller {
    @FXML
    private Label mealNameLabel;

    @FXML
    private Label currentPriceLabel;

    @FXML
    private TextField priceField;

    private Label priceLabel; // Reference to the price label in the main menu
    private String mealId;

    // Set meal details and bind priceLabel for dynamic updates
    public void setMealDetails(String mealName, Label priceLabel,String mealid) {
        this.mealNameLabel.setText("Change Price for: " + mealName);
        this.currentPriceLabel.setText("Current Price: " + priceLabel.getText());
        this.priceLabel = priceLabel;
        this.mealId = mealid;
    }

    @FXML
    private void updatePrice() {
        String newPrice = priceField.getText();
        if (newPrice != null && !newPrice.trim().isEmpty()) {
            SimpleClient client;
            client = SimpleClient.getClient();
            updatePrice uPrice = new updatePrice(Double.parseDouble(newPrice),Integer.parseInt(mealId));
            try {
                client.sendToServer(uPrice);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //priceLabel.setText(newPrice + "₪");
            priceField.getScene().getWindow().hide(); // Close the window
        }
    }
}