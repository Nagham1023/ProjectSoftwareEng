package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPageController {

    @FXML
    private Button Delivery;

    @FXML
    private Button PlaceYourOrder;

    @FXML
    private Button arrow;

    @FXML
    private Button cash;

    @FXML
    private Button mastercard;

    @FXML
    private Pane orderPrice;

    @FXML
    private Button orderdate;

    @FXML
    private Button selfPickup;

    @FXML
    private TextField addressField;
    @FXML
    private TextField homeNumberField;

    @FXML
    private Label pickupMessageLabel;

    @FXML
    private ComboBox<String> orderTimeComboBox;

    @FXML
    private Button visa;
    @FXML
    public void initialize() {
        setupOrderTimeComboBox();
        setupArrowButton();
        setupVisaButton();
        setupmastercardButton();
        setupDeliveryAndPickupButtons();
        setupHomeNumberField();
    }

    private void setupOrderTimeComboBox() {
        List<String> availableTimes = calculateTimes();
        orderTimeComboBox.getItems().setAll(availableTimes);
        orderTimeComboBox.getSelectionModel().selectFirst(); // Optionally select the first available time.
    }

    private List<String> calculateTimes() {
        List<String> times = new ArrayList<>();
        LocalTime startTime = LocalTime.now().plusMinutes(30); // Start 30 minutes from now.
        final LocalTime startLimit = LocalTime.of(10, 30); // Start of the time limit at 10:00 AM
        final LocalTime endLimit = LocalTime.of(22, 0); // End of the time limit at 10:00 PM
        final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Adjust start time to 10:30 AM if it's earlier than that or to the next half hour slot if it's later
        if (startTime.isBefore(startLimit)) {
            startTime = startLimit;
        } else if (startTime.isAfter(endLimit)) {
            startTime = endLimit.plusMinutes(30); // Next day's first slot if after 10 PM
        }

        // Loop to add times but ensure it's within the 10:00 AM to 10:00 PM bounds
        while (startTime.isBefore(endLimit.plusMinutes(30))) { // Include 10:00 PM time slot
            times.add(startTime.format(timeFormatter));
            startTime = startTime.plusMinutes(30); // Increment by 30 minutes
        }

        return times;
    }


    private void setupArrowButton() {
        arrow.setOnAction(event -> {
            try {
                navigateToPersonalDetails();
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }

    private void navigateToPersonalDetails() throws IOException {
        App.setRoot("PersonalDetailsPage");

    }

    private void setupVisaButton() {
        visa.setOnAction(event -> {
            try {
                visabutton();
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }
    private void visabutton() throws IOException {
        App.setRoot("CreditDetails");
    }

    private void setupmastercardButton() {
        mastercard.setOnAction(event -> {
            try {
                mastercardbutton();
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }
    private void mastercardbutton() throws IOException {
        App.setRoot("CreditDetails");
    }

    private void setupHomeNumberField() {
        homeNumberField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d*")) {  // Regex to allow only digits
                event.consume();  // Ignore non-digit characters
            }
        });
    }
    private void setupDeliveryAndPickupButtons() {
        selfPickup.setOnAction(event -> toggleDeliveryOptions(true));
        Delivery.setOnAction(event -> toggleDeliveryOptions(false));
    }

    private void toggleDeliveryOptions(boolean isSelfPickupSelected) {
        if (isSelfPickupSelected) {
            // Self Pickup selected
            selfPickup.setStyle("-fx-background-color: #832018; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            Delivery.setStyle("-fx-background-color: white; -fx-text-fill: #832018; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            pickupMessageLabel.setVisible(true);  // Show the pickup message label
            addressField.setVisible(false);
            homeNumberField.setVisible(false);
        } else {
            // Delivery selected
            Delivery.setStyle("-fx-background-color: #832018; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            selfPickup.setStyle("-fx-background-color: white; -fx-text-fill: #832018; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            pickupMessageLabel.setVisible(false); // Hide the pickup message label
            addressField.setVisible(true);
            homeNumberField.setVisible(true);
        }
    }
}
