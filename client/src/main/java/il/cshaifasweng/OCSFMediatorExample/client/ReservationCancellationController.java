package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class ReservationCancellationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField reservationIdField;

    @FXML
    private Button cancelButton;

    @FXML
    private Button clearButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleCancelReservation() {
        String name = nameField.getText().trim();
        String reservationIdStr = reservationIdField.getText().trim();

        // Basic validation
        if (name.isEmpty() || reservationIdStr.isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            if (reservationId <= 0) {
                statusLabel.setText("Please enter a valid reservation ID");
                return;
            }

            String requestData = String.format("Cancel Reservation: %s,%d", name, reservationId);
            SimpleClient.getClient().sendToServer(requestData);

            statusLabel.setText("Processing your request...");

        } catch (NumberFormatException e) {
            statusLabel.setText("Reservation ID must be a number");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFields() {
        clearFields();
        statusLabel.setText("");
    }

    private void clearFields() {
        nameField.clear();
        reservationIdField.clear();
    }

    @Subscribe
    public void showCancelStatus(String status) {
        Platform.runLater(() -> {
            if (status.startsWith("Success")) {
                statusLabel.setText("Reservation cancelled successfully!");
                clearFields();
            } else if (status.startsWith("Error")) {
                statusLabel.setText(status);
            } else {
                statusLabel.setText("Server response: " + status);
            }
        });
    }

    // Initialize EventBus subscription
    public void initialize() {
        EventBus.getDefault().register(this);
    }

    @FXML
    void backToHome(ActionEvent event) throws IOException {
        EventBus.getDefault().unregister(this);
        App.setRoot("mainScreen");
    }
}