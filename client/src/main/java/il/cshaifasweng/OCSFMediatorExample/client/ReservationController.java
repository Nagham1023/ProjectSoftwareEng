package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DifferentResrvation;
import il.cshaifasweng.OCSFMediatorExample.entities.FinalReservationEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservationEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ReservationController {
    @FXML
    private ScrollPane scrollPane; // Inject the ScrollPane from the FXML file

    @FXML
    private AnchorPane anchorPane; // Inject the AnchorPane from the FXML file

    // Other injected components
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<Integer> hourComboBox;
    @FXML
    private ComboBox<Integer> minuteComboBox;
    @FXML
    private ComboBox<String> restaurantsComboBox;
    @FXML
    private TextField seatsTextField;
    @FXML
    private ComboBox<String> insideOutsideComboBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;

    // Loading GIF components
    @FXML
    private ImageView loadingGif;

    @FXML
    public void initialize() throws IOException {
        EventBus.getDefault().register(this);

        // Populate hour combo box (24-hour format: 0-23)
        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }

        // Populate minute combo box (0-59, step 15)
        for (int i = 0; i < 60; i += 15) {
            minuteComboBox.getItems().add(i);
        }

        // Populate inside/outside combo box
        insideOutsideComboBox.getItems().addAll("Inside", "Outside");
        insideOutsideComboBox.getSelectionModel().selectFirst(); // Default to "Inside"

        // Load the Loading.gif image
        Image loadingImage = new Image(getClass().getResourceAsStream("/images/Loading.gif"));
        loadingGif.setImage(loadingImage);

        // Fetch all restaurants
        SimpleClient.getClient().sendToServer("getAllRestaurants");
        startLoading();
    }

    @Subscribe
    public void putResturants(RestaurantList restaurants) {
        Platform.runLater(() -> {
            System.out.println("Received restaurant list: " + restaurants);
            restaurantsComboBox.getItems().clear(); // Clear previous items

            // Populate the combo box
            restaurants.getRestaurantList().forEach(restaurant ->
                    restaurantsComboBox.getItems().add(restaurant.getRestaurantName()) // Assuming Restaurant has getRestaurantName()
            );
            stopLoading();
        });
    }

    // Show loading animation and disable UI
    private void startLoading() {
        Platform.runLater(() -> {
            loadingGif.setVisible(true);
            anchorPane.setDisable(true); // Disable all UI components
        });
    }

    // Hide loading animation and enable UI
    private void stopLoading() {
        Platform.runLater(() -> {
            loadingGif.setVisible(false);
            anchorPane.setDisable(false); // Enable all UI components
        });
    }

    @FXML
    void handleConfirm(ActionEvent event) throws IOException {
        // Start loading animation
        startLoading();

        // Get selected date and time
        LocalDate selectedDate = datePicker.getValue();
        Integer selectedHour = hourComboBox.getValue();
        Integer selectedMinute = minuteComboBox.getValue();
        String restaurantName = restaurantsComboBox.getValue();
        String seatsInput = seatsTextField.getText(); // Get user input for seats
        boolean isInside = insideOutsideComboBox.getSelectionModel().getSelectedItem().equals("Inside");

        // Validate selections
        if (selectedDate == null) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "Please select a valid date!");
            return;
        }

        if (selectedHour == null || selectedMinute == null) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "Please select a valid hour and minute!");
            return;
        }

        if (restaurantName == null || restaurantName.isEmpty()) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "Please select a restaurant!");
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsInput);
            if (seats <= 0) {
                stopLoading(); // Stop loading if validation fails
                showAlert("Error", "The number of seats must be greater than zero!");
                return;
            }
        } catch (NumberFormatException e) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "Please enter a valid number for seats!");
            return;
        }

        // Create LocalDateTime from the selected values
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute));

        // Ensure the reservation is for a future date/time
        if (selectedDateTime.isBefore(LocalDateTime.now())) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "You cannot make a reservation in the past!");
            return;
        }

        // Create ReservationEvent with seats and isInside
        ReservationEvent reservationEvent = new ReservationEvent(restaurantName, selectedDateTime, seats, isInside);
        SimpleClient.getClient().sendToServer(reservationEvent); // Send it to the server
    }

    @Subscribe
    public void showDifferentAvailableReservation(DifferentResrvation availableReservations) {
        Platform.runLater(() -> {
            // Stop loading animation
            stopLoading();
            // Clear any existing buttons for available reservations
            anchorPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("reservationButton"));

            if (availableReservations.getAvailableTimeSlots() == null) {
                // No available reservations at the requested time, show a message
                showAlert("No Available Reservations", "There are no available reservations for the selected time.\nPlease check alternative time slots.");
            } else {
                double layoutY = 200.0; // Starting Y position for the buttons

                List<LocalDateTime> availableTimeSlots = availableReservations.getAvailableTimeSlots(); // Now correctly using LocalDateTime

                // Inform the user that the requested time isn't available, but suggest alternatives
                Label suggestionLabel = new Label("Alternative time slots available for " + availableReservations.getRestaurantName() + ":");
                suggestionLabel.setLayoutX(50.0);
                suggestionLabel.setLayoutY(layoutY);
                anchorPane.getChildren().add(suggestionLabel);
                layoutY += 25.0; // Move Y position down for buttons

                for (LocalDateTime timeSlot : availableTimeSlots) {
                    // Create a button for each available time slot
                    String reservationDetails = String.format(
                            "Date: %s, Time: %s, Restaurant: %s, Seats: %d, %s",
                            timeSlot.toLocalDate().toString(), // Date
                            timeSlot.toLocalTime().toString(), // Alternative available time
                            availableReservations.getRestaurantName(), // Restaurant name
                            availableReservations.getSeats(), // Number of seats
                            availableReservations.isInside() ? "Inside" : "Outside" // Inside/Outside
                    );

                    Button reservationButton = new Button(reservationDetails);
                    reservationButton.setId("reservationButton");
                    reservationButton.setLayoutX(50.0);
                    reservationButton.setLayoutY(layoutY);
                    reservationButton.setOnAction(event -> {
                        try {
                            startLoading();
                            handleTimeButtonClick(reservationButton.getText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    // Add the button to the UI
                    anchorPane.getChildren().add(reservationButton);
                    layoutY += 30.0; // Move Y position down for the next button
                }
            }
        });
    }

    @Subscribe
    public void showAvailableReservation(List<ReservationEvent> availableReservations) {
        Platform.runLater(() -> {
            // Stop loading animation
            stopLoading();
            // Clear any existing buttons for available reservations
            anchorPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("reservationButton"));

            if (availableReservations.isEmpty() || availableReservations.getFirst().getReservationDateTime() == null) {
                // Show an alert if no available reservations are found
                showAlert("No Available Reservations", "There are no available reservations for this date and restaurant.");
            } else {
                // Create buttons for each available reservation
                double layoutY = 200.0; // Starting Y position for the buttons
                for (ReservationEvent reservation : availableReservations) {
                    // Create a detailed description for the button (including date and time)
                    String reservationDetails = String.format(
                            "Date: %s, Time: %s, Restaurant: %s, Seats: %d, %s",
                            reservation.getReservationDateTime().toLocalDate().toString(), // Date
                            reservation.getReservationDateTime().toLocalTime().toString(), // Time
                            reservation.getRestaurantName(), // Restaurant name
                            reservation.getSeats(), // Number of seats
                            reservation.isInside() ? "Inside" : "Outside" // Inside/Outside
                    );

                    // Create the button with the reservation details
                    Button reservationButton = new Button(reservationDetails);
                    reservationButton.setId("reservationButton"); // Set an ID to identify these buttons later
                    reservationButton.setLayoutX(50.0); // X position for the buttons
                    reservationButton.setLayoutY(layoutY); // Y position for the buttons
                    reservationButton.setOnAction(event -> {
                        try {
                            // Start loading animation
                            startLoading();

                            // Pass the button's text to handleTimeButtonClick
                            handleTimeButtonClick(reservationButton.getText());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }); // Handle button click

                    // Add the button to the AnchorPane
                    anchorPane.getChildren().add(reservationButton);

                    // Increment the Y position for the next button
                    layoutY += 30.0;
                }
            }
        });
    }

    @Subscribe
    public void reservationConfirmed(String msg) {
        Platform.runLater(() -> {
            if (msg.equals("Reservation confirmed successfully.")) {
                // Stop loading animation
                stopLoading();
                showAlert("Reservation Confirmed", "Your reservation has been confirmed successfully!");

                // Clear all fields and reset the page
                clearAllFields();
            }
        });
    }

    private void clearAllFields() {
        // Reset the date picker
        datePicker.setValue(null);

        // Reset the hour and minute combo boxes
        hourComboBox.getSelectionModel().clearSelection();
        minuteComboBox.getSelectionModel().clearSelection();

        hourComboBox.setAccessibleText("Hour");
        minuteComboBox.setAccessibleText("Minute");
        // Clear the seats text field
        seatsTextField.clear();
        seatsTextField.setAccessibleText("Seats");
        // Reset the inside/outside combo box
        insideOutsideComboBox.getSelectionModel().clearSelection();
        insideOutsideComboBox.setAccessibleText("Inside");
        // Clear the restaurant combo box
        restaurantsComboBox.getSelectionModel().clearSelection();
        restaurantsComboBox.setAccessibleText("Pick a Restaurant");
        // Clear any additional UI elements (e.g., buttons, labels, etc.)
        anchorPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("reservationButton"));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to handle button clicks for available times
    private void handleTimeButtonClick(String buttonText) throws IOException {
        // Extract information from the button's text
        String[] parts = buttonText.split(", "); // Split the button text by ", "
        String datePart = parts[0].replace("Date: ", ""); // Extract date
        String timePart = parts[1].replace("Time: ", ""); // Extract time
        String restaurantPart = parts[2].replace("Restaurant: ", ""); // Extract restaurant name
        String seatsPart = parts[3].replace("Seats: ", ""); // Extract seats
        String insideOutsidePart = parts[4]; // Extract inside/outside

        // Parse the extracted information
        LocalDate selectedDate = LocalDate.parse(datePart); // Parse date
        LocalTime selectedTime = LocalTime.parse(timePart); // Parse time
        String restaurantName = restaurantPart; // Restaurant name
        int seats = Integer.parseInt(seatsPart); // Parse seats
        boolean isInside = insideOutsidePart.equals("Inside"); // Check if inside

        // Update the UI with the selected time
        hourComboBox.getSelectionModel().select(selectedTime.getHour());
        minuteComboBox.getSelectionModel().select(selectedTime.getMinute());

        // Create the final reservation date and time
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

        // Create FinalReservationEvent with isInside
        FinalReservationEvent finalReservationEvent = new FinalReservationEvent(restaurantName, selectedDateTime, seats, isInside);
        SimpleClient.getClient().sendToServer(finalReservationEvent);
        startLoading();
    }

    @FXML
    void backToHome(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            try {
                App.setRoot("mainScreen"); // Navigate back to the main screen
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}