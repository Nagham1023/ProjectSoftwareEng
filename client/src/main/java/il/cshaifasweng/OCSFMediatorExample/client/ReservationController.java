package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.DifferentResrvation;
import il.cshaifasweng.OCSFMediatorExample.entities.FinalReservationEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservationEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
                showAlert("No Available Reservations", "There are no available reservations for the selected time.\nHere are different available time slots");
                double layoutY = 200.0; // Starting Y position for the buttons

                List<LocalDateTime> availableTimeSlots = availableReservations.getAvailableTimeSlots(); // Now correctly using LocalDateTime


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
        Platform.runLater(()->{
            // Reset the date picker
            datePicker.setValue(null);

            // Reset the hour and minute combo boxes
            hourComboBox.getSelectionModel().clearSelection();
            minuteComboBox.getSelectionModel().clearSelection();

            // Set placeholder text for combo boxes
            hourComboBox.setPromptText("Hour");
            minuteComboBox.setPromptText("Minute");

            // Clear the seats text field and set placeholder text
            seatsTextField.clear();
            seatsTextField.setPromptText("Seats");

            // Reset the inside/outside combo box
            insideOutsideComboBox.getSelectionModel().clearSelection();
            insideOutsideComboBox.setPromptText("Inside");

            // Clear the restaurant combo box
            restaurantsComboBox.getSelectionModel().clearSelection();
            restaurantsComboBox.setPromptText("Pick a Restaurant");

            // Clear any additional UI elements (e.g., buttons, labels, etc.)
            anchorPane.getChildren().removeIf(node -> node.getId() != null && node.getId().equals("reservationButton"));
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleTimeButtonClick(String buttonText) throws IOException {
        boolean reservationSuccessful = false;

        while (!reservationSuccessful) {
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

            // Create the final reservation date and time
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

            // Show a dialog for user details
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("User Details");
            dialog.setHeaderText("Please enter your details to confirm the reservation.");

            // Set the button types (OK and Cancel)
            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            // Create the input fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField fullNameField = new TextField();
            fullNameField.setPromptText("Full Name");
            TextField phoneNumberField = new TextField();
            phoneNumberField.setPromptText("Phone Number");
            TextField emailField = new TextField();
            emailField.setPromptText("Email");

            grid.add(new Label("Full Name:"), 0, 0);
            grid.add(fullNameField, 1, 0);
            grid.add(new Label("Phone Number:"), 0, 1);
            grid.add(phoneNumberField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Enable the Confirm button only when all fields are filled
            Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
            confirmButton.setDisable(true);

            // Add listeners to enable the Confirm button when all fields are filled
            fullNameField.textProperty().addListener((observable, oldValue, newValue) -> {
                confirmButton.setDisable(newValue.trim().isEmpty() || phoneNumberField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty());
            });
            phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
                confirmButton.setDisable(newValue.trim().isEmpty() || fullNameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty());
            });
            emailField.textProperty().addListener((observable, oldValue, newValue) -> {
                confirmButton.setDisable(newValue.trim().isEmpty() || fullNameField.getText().trim().isEmpty() || phoneNumberField.getText().trim().isEmpty());
            });

            // Convert the result to a Pair when the Confirm button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    return new Pair<>(fullNameField.getText(), phoneNumberField.getText() + "|" + emailField.getText());
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                String fullName = result.get().getKey();
                String[] contactDetails = result.get().getValue().split("\\|");
                String phoneNumber = contactDetails[0];
                String email = contactDetails[1];

                // Validate user inputs
                boolean isValid = validateUserInputs(fullName, phoneNumber, email);

                if (!isValid) {
                    showAlert("Invalid Input", "Please check your name, phone number, and email.");
                    continue; // Retry the reservation process
                }

                // Send verification email
                boolean isEmailSent = sendVerificationEmail(email);

                if (!isEmailSent) {
                    showAlert("Email Error", "Failed to send verification email. Please try again.");
                    continue; // Retry the reservation process
                }

                // Display the result with emojis
                String resultMessage = String.format(
                        "Reservation Details:\n\n" +
                                "Name: %s %s\n" +
                                "Phone: %s %s\n" +
                                "Email: %s %s\n" +
                                "Output Availability: %s",
                        fullName, isValid ? "✅" : "❌",
                        phoneNumber, isValid ? "✅" : "❌",
                        email, isEmailSent ? "✅" : "❌"
                );

                showAlert("Reservation Status", resultMessage);

                // Create FinalReservationEvent with user details
                FinalReservationEvent finalReservationEvent = new FinalReservationEvent(
                        restaurantName, selectedDateTime, seats, isInside, fullName, phoneNumber, email
                );

                // Send the reservation to the server
                try {
                    SimpleClient.getClient().sendToServer(finalReservationEvent);
                    reservationSuccessful = true; // Mark reservation as successful
                } catch (IOException e) {
                    showAlert("Error", "Failed to send reservation to the server. Please try again.");
                    e.printStackTrace();
                }
            } else {
                // User clicked Cancel, exit the loop
                break;
            }
        }
    }
    // Helper method to validate user inputs
    private boolean validateUserInputs(String fullName, String phoneNumber, String email) {
        // Validate full name (non-empty and contains only letters and spaces)
        if (fullName == null || fullName.trim().isEmpty() || !fullName.matches("[a-zA-Z\\s]+")) {
            return false;
        }

        // Validate phone number (10 digits)
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || !phoneNumber.matches("\\d{10}")) {
            return false;
        }

        // Validate email (basic format check)
        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }

        return true;
    }

    // Helper method to send a verification email
    private boolean sendVerificationEmail(String email) {
        // Simulate sending an email (replace with actual email sending logic)
        System.out.println("Sending verification email to: " + email);
        return true; // Assume email is sent successfully
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