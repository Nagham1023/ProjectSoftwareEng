package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CreditCard;
import il.cshaifasweng.OCSFMediatorExample.entities.ListOfCC;
import il.cshaifasweng.OCSFMediatorExample.entities.PersonalDetails;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.done_Order;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.deliveryPrice;

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
    private Label price;


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
    private ListView<CreditCard> creditCardListView; // Example component for displaying cards

    static public PersonalDetails personalDetails;

    @FXML
    public void initialize() {
        setupOrderTimeComboBox();
        setupArrowButton();
        setupVisaButton();
        setupmastercardButton();
        setupcashButton();
        setupDeliveryAndPickupButtons();
        setupHomeNumberField();
        PlaceYourOrder.setDisable(true);
        PlaceYourOrder.setVisible(false);
        price.setText(done_Order.getTotal_price()+"₪");

    }

    private void setupOrderTimeComboBox() {
        System.out.println("setupOrderTimeComboBox");
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
        // If current time is after the delivery window, show tomorrow's slots
        if (startTime.isAfter(endLimit)) {
            startTime = startLimit; // Start from 10:30 tomorrow
        } else if (startTime.isBefore(startLimit)) {
            startTime = startLimit; // Clamp to 10:30 if too early
        }

        // Add times every 30 mins up to 10:00 PM
        while (!startTime.isAfter(endLimit)) {
            times.add(startTime.format(timeFormatter));
            startTime = startTime.plusMinutes(30);
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
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Selection Required");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        if (!isDeliveryOrPickupSelected()) {
            showAlert("Please select either Delivery or Self Pickup before proceeding.");
            return; // Stop further execution
        }
        if(isDeliverySelected() && addressField.getText().trim().isEmpty() && homeNumberField.getText().trim().isEmpty()){
            showAlert("Please enter a valid address.");
            return;
        }
        done_Order.setDate(LocalDate.now());
        String selectedTime = orderTimeComboBox.getValue();
        LocalDate today = LocalDate.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(selectedTime, timeFormatter);

        LocalDateTime dateTime = LocalDateTime.of(today, time);
        done_Order.setOrderTime(dateTime);
        App.setRoot("CreditDetails");
    }
    private boolean isDeliveryOrPickupSelected() {
        return Delivery.getStyle().contains("-fx-background-color: #832018") ||
                selfPickup.getStyle().contains("-fx-background-color: #832018");
    }
    private boolean isDeliverySelected() {
        return Delivery.getStyle().contains("-fx-background-color: #832018");

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
        if (!isDeliveryOrPickupSelected()) {
            showAlert("Please select either Delivery or Self Pickup before proceeding.");
            return; // Stop further execution
        }
        if(isDeliverySelected() && addressField.getText().trim().isEmpty() && homeNumberField.getText().trim().isEmpty()){
            showAlert("Please enter a valid address.");
            return;
        }
        done_Order.setDate(LocalDate.now());
        String selectedTime = orderTimeComboBox.getValue();
        LocalDate today = LocalDate.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(selectedTime, timeFormatter);

        LocalDateTime dateTime = LocalDateTime.of(today, time);
        done_Order.setOrderTime(dateTime);
        App.setRoot("CreditDetails");
    }

    private void setupcashButton() {
        cash.setOnAction(event -> {
            try {
                cashbutton();
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }
    private void cashbutton() throws IOException {
        if (!isDeliveryOrPickupSelected()) {
            showAlert("Please select either Delivery or Self Pickup before proceeding.");
            return; // Stop further execution
        }
        if(isDeliverySelected() && addressField.getText().trim().isEmpty() && homeNumberField.getText().trim().isEmpty()){
            showAlert("Please enter a valid address.");
            return;
        }
        done_Order.setDate(LocalDate.now());
        String selectedTime = orderTimeComboBox.getValue();
        LocalDate today = LocalDate.now();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(selectedTime, timeFormatter);

        LocalDateTime dateTime = LocalDateTime.of(today, time);
        done_Order.setOrderTime(dateTime);
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
            done_Order.setOrderType("Self PickUp");
            price.setText(done_Order.getTotal_price()+"₪");
        } else {
            // Delivery selected
            Delivery.setStyle("-fx-background-color: #832018; -fx-text-fill: white; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            selfPickup.setStyle("-fx-background-color: white; -fx-text-fill: #832018; -fx-background-radius: 20; -fx-border-color: #832018; -fx-border-width: 2; -fx-border-radius: 20;");
            pickupMessageLabel.setVisible(false); // Hide the pickup message label
            addressField.setVisible(true);
            homeNumberField.setVisible(true);
            done_Order.setOrderType("Delivery");
            int newPrice = done_Order.getTotal_price() + deliveryPrice;
            price.setText(newPrice + "₪");
        }
    }
    // Don't forget to unregister when no longer needed
    public void unregister() {
        EventBus.getDefault().unregister(this);
    }


}
