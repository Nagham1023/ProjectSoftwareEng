package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
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
import java.util.Random;

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

                showAlert("Reservation Confirmed", "Your reservation has been confirmed successfully!");

                // Clear all fields and reset the page
                clearAllFields();
                // Stop loading animation
                stopLoading();
            }
        });
    }

    private void clearAllFields() {
        Platform.runLater(() -> {
            // Reset the date picker
            datePicker.setValue(null);

            // Reset the hour and minute combo boxes
            hourComboBox.getSelectionModel().clearSelection();
            minuteComboBox.getSelectionModel().clearSelection();

            // Clear the seats text field and set placeholder text
            seatsTextField.clear();

            // Reset the inside/outside combo box
            insideOutsideComboBox.getSelectionModel().clearSelection();

            // Clear the restaurant combo box
            restaurantsComboBox.getSelectionModel().clearSelection();

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
            // Extract reservation details
            String[] parts = buttonText.split(", ");
            String datePart = parts[0].replace("Date: ", "");
            String timePart = parts[1].replace("Time: ", "");
            String restaurantPart = parts[2].replace("Restaurant: ", "");
            String seatsPart = parts[3].replace("Seats: ", "");
            String insideOutsidePart = parts[4];

            LocalDate selectedDate = LocalDate.parse(datePart);
            LocalTime selectedTime = LocalTime.parse(timePart);
            String restaurantName = restaurantPart;
            int seats = Integer.parseInt(seatsPart);
            boolean isInside = insideOutsidePart.equals("Inside");
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

            // Create input dialog for user details
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("User Details");
            dialog.setHeaderText("Enter your details to confirm the reservation.");

            ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField fullNameField = new TextField();
            fullNameField.setPromptText("Full Name");
            TextField phoneNumberField = new TextField();
            phoneNumberField.setPromptText("Phone Number");
            TextField emailField = new TextField();
            emailField.setPromptText("Email");

            Label errorLabel = new Label();
            errorLabel.setStyle("-fx-text-fill: red;");

            grid.add(new Label("Full Name:"), 0, 0);
            grid.add(fullNameField, 1, 0);
            grid.add(new Label("Phone Number:"), 0, 1);
            grid.add(phoneNumberField, 1, 1);
            grid.add(new Label("Email:"), 0, 2);
            grid.add(emailField, 1, 2);
            grid.add(errorLabel, 0, 3, 2, 1);

            dialog.getDialogPane().setContent(grid);

            // Convert the result to a Pair of fullName, phoneNumber, and email
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    return new Pair<>(fullNameField.getText(), phoneNumberField.getText() + "," + emailField.getText());
                }
                return null; // Return null if the user clicks "Cancel"
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            if (result.isPresent()) {
                // User clicked "Confirm"
                String fullName = result.get().getKey();
                String[] phoneAndEmail = result.get().getValue().split(",");
                String phoneNumber = phoneAndEmail[0];
                String email = phoneAndEmail[1];

                if (!validateUserInputs(fullName, phoneNumber, email)) {
                    showAlert("Invalid Input", "Please check your name, phone number, and email.");
                    continue; // Retry entering details
                }

                // Generate a random 6-digit verification code
                Random random = new Random();
                String verificationCode = String.format("%06d", random.nextInt(1000000));

                String messageBody = "Your reservation verification code is: " + verificationCode;

                EmailSender.sendEmail("Verification Code", messageBody, email);

                // Open verification code dialog
                boolean isVerified = verifyCode(verificationCode);

                if (!isVerified) {
                    showAlert("Verification Failed", "Incorrect verification code. Please try again.");
                    continue;
                }

                // Successfully verified
                String resultMessage = String.format(
                        """
                                Reservation Details:
                               \s
                                Name: %s\s
                                Phone: %s\s
                                Email: %s\s
                               \s""",
                        fullName,
                        phoneNumber,
                        email
                );

                showAlert("Reservation Confirmed", resultMessage);

                FinalReservationEvent finalReservationEvent = new FinalReservationEvent(
                        restaurantName, selectedDateTime, seats, isInside, fullName, phoneNumber, email
                );

                try {
                    SimpleClient.getClient().sendToServer(finalReservationEvent);
                    reservationSuccessful = true;
                } catch (IOException e) {
                    showAlert("Error", "Failed to send reservation to the server. Please try again.");
                    e.printStackTrace();
                }
            } else {
                // User clicked "Cancel"
                stopLoading();
                return; // Exit the function
            }
        }
    }




    private boolean verifyCode(String generatedCode) {
        Dialog<String> codeDialog = new Dialog<>();
        codeDialog.setTitle("Gmail Verification");
        codeDialog.setHeaderText("Enter the 6-digit code sent to your WhatsApp:");

        ButtonType confirmButtonType = new ButtonType("Verify", ButtonBar.ButtonData.OK_DONE);
        codeDialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField codeField = new TextField();
        codeField.setPromptText("Enter code");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        grid.add(new Label("Verification Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(errorLabel, 0, 1, 2, 1);

        codeDialog.getDialogPane().setContent(grid);

        // Disable verify button until input is given
        Node confirmButton = codeDialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // Enable button when the field is not empty
        codeField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(newValue.trim().isEmpty());
        });

        codeDialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return codeField.getText();
            }
            return null;
        });

        Optional<String> result = codeDialog.showAndWait();

        // Check if the entered code matches the generated code
        return result.isPresent() && result.get().equals(generatedCode);
    }

    // Helper method to validate user inputs
    private boolean validateUserInputs(String fullName, String phoneNumber, String email) {
        // Validate full name (non-empty and contains only letters and spaces)
        if (fullName == null || fullName.trim().isEmpty() || !fullName.matches("[a-zA-Z\\s]+")) {
            return false;
        }

        // Validate phone number (supports both local and international format)
        if (phoneNumber == null || phoneNumber.trim().isEmpty() ||
                !phoneNumber.matches("^(\\+972\\d{8,9}|0\\d{9})$")) {
            return false;
        }

        // Validate email (basic format check)
        if (email == null || email.trim().isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return false;
        }

        return true;
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