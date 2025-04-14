package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    @FXML
    private Label currentDiscountLabel;

    @FXML
    private TextField discountField;

    // Set meal details and bind priceLabel for dynamic updates
    public void setMealDetails(String mealName, Label priceLabel,String mealid,double discount) {
        this.mealNameLabel.setText("Change Price for: " + mealName);
        this.currentPriceLabel.setText("Current Price: " + priceLabel.getText());
        this.currentDiscountLabel.setText("Current Discount: " + discount);
        this.priceLabel = priceLabel;
        System.out.println("Setting meal ID: " + mealid); // Debug log
        this.mealId = mealid;
    }

    @FXML
    private void updatePrice() {
        String newPrice = priceField.getText();
        String newDiscount = discountField.getText();

        // Validate inputs
        if (newPrice == null || newPrice.trim().isEmpty()) {
            showAlert("Error", "Price cannot be empty");
            return;
        }

        if (newDiscount == null || newDiscount.trim().isEmpty()) {
            showAlert("Error", "Discount cannot be empty");
            return;
        }

        try {
            // Parse values
            double price = Double.parseDouble(newPrice);
            double discount = Double.parseDouble(newDiscount);

            // Validate discount range
            if (discount < 0 || discount > 100) {
                showAlert("Invalid Discount", "Discount must be between 0 and 100");
                return;
            }

            // Validate price
            if (price <= 0) {
                showAlert("Invalid Price", "Price must be greater than 0");
                return;
            }

            // If all validations pass, proceed with update
            SimpleClient client = SimpleClient.getClient();
            updatePrice uPrice = new updatePrice(price, Integer.parseInt(mealId),discount, "asking");

            try {
                client.sendToServer(uPrice);
                priceField.getScene().getWindow().hide(); // Close the window
            } catch (IOException e) {
                showAlert("Error", "Failed to communicate with server");
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for price and discount");
        }
    }

    // Helper method to show error alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}