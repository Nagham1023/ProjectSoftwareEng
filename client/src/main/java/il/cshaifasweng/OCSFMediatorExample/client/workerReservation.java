package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.restaurantList;


public class workerReservation {
    private boolean isFinished = false;

    private String currentWorker;
    private String currentRestaurant;

    @FXML
    private ScrollPane scrollPane; // Inject the ScrollPane from the FXML file

    @FXML
    private AnchorPane anchorPane; // Inject the AnchorPane from the FXML file

    // Other injected components

    //@FXML
    //private ComboBox<String> restaurantsComboBox;
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
        isFinished = false;
        addMouseGlowEffect(confirmButton);


        // Populate inside/outside combo box
        insideOutsideComboBox.getItems().addAll("Inside", "Outside");

        // Load the Loading.gif image
        Image loadingImage = new Image(getClass().getResourceAsStream("/images/Loading.gif"));
        loadingGif.setImage(loadingImage);



        // Fetch all restaurants
        /*if(restaurantList == null)
            SimpleClient.getClient().sendToServer("getAllRestaurants");
        else putResturants(restaurantList);*/
        //startLoading();
    }

    /*@Subscribe
    public void putResturants(RestaurantList restaurants) {
        Platform.runLater(() -> {
            SimpleClient.restaurantList = restaurants;
            System.out.println("Received restaurant list: " + restaurants);
            restaurantsComboBox.getItems().clear(); // Clear previous items

            // Populate the combo box
            restaurants.getRestaurantList().forEach(restaurant ->
                    restaurantsComboBox.getItems().add(restaurant.getRestaurantName()) // Assuming Restaurant has getRestaurantName()
            );
            stopLoading();
        });
    }*/

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
    void handleConfirm() throws IOException {
        // Start loading animation
        startLoading();

        // Get selected date and time
        LocalDate selectedDate = LocalDate.now();
        LocalTime selectedTime = roundUpToNearest15Minutes(LocalTime.now());
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

        String restaurantName = currentRestaurant;
        String seatsInput = seatsTextField.getText(); // Get user input for seats
        // Validate selections
        if (selectedDate == null) {
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "Please select a valid date!");
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


        if(insideOutsideComboBox.getSelectionModel().getSelectedItem()==null){
            stopLoading(); // Stop loading if validation fails
            showAlert("Error", "You should pick where you want to set");
            return;
        }
        boolean isInside = insideOutsideComboBox.getSelectionModel().getSelectedItem().equals("Inside");

        // Create ReservationEvent with seats and isInside
        ReservationEvent reservationEvent = new ReservationEvent(true, restaurantName, selectedDateTime, seats, isInside);
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
                double layoutY = 350.0; // Starting Y position for the buttons

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
                    reservationButton.getStyleClass().add("button"); // Apply the same button style as the confirm button

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
                    layoutY += 50.0; // Move Y position down for the next button
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

            if (availableReservations.isEmpty() || availableReservations.get(0).getReservationDateTime() == null) {
                // Show an alert if no available reservations are found
                showAlert("No Available Reservations", "There are no available reservations for this date and restaurant.");
            } else {
                // Create buttons for each available reservation
                double layoutY = 350.0; // Starting Y position for the buttons
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
                    reservationButton.getStyleClass().add("button"); // Apply the same button style as the confirm button

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
                    layoutY += 50.0;
                }
            }
        });
    }

    @Subscribe
    public void reservationConfirmed(String msg) {
        if (msg.equals("Reservation confirmed successfully.")) {
            isFinished = true;
            Platform.runLater(() -> {
                // Clear all fields and reset the page
                clearAllFields();
                // Stop loading animation
                stopLoading();
                try {
                    backToHome();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Subscribe
    public void reConfirmFunction(ReConfirmEvent reConfirmEvent) {
        if(isFinished)return;
        try {
            handleConfirm();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearAllFields() {
        Platform.runLater(() -> {
            // Clear the seats text field and set placeholder text
            seatsTextField.clear();

            // Reset the inside/outside combo box
            insideOutsideComboBox.getSelectionModel().clearSelection();


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
            // Get current date and time
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = roundUpToNearest15Minutes(LocalTime.now());

            // Extract reservation details (excluding date and time since they are automatic)
            String[] parts = buttonText.split(", ");
            //String restaurantPart = parts[2].replace("Restaurant: ", "");
            String seatsPart = parts[3].replace("Seats: ", "");
            String insideOutsidePart = parts[4];
            String datePart = parts[0].replace("Date: ", "");
            String timePart = parts[1].replace("Time: ", "");
            LocalDate date = LocalDate.parse(datePart);         // Format: "yyyy-MM-dd"
            LocalTime time = LocalTime.parse(timePart);         // Format: "HH:mm"
            LocalDateTime selectedDateTime = LocalDateTime.of(date, time);
            String restaurantName = currentRestaurant;
            int seats = Integer.parseInt(seatsPart);
            boolean isInside = insideOutsidePart.equals("Inside");

            // Worker details (predefined)
            String fullName = SimpleClient.getUser().getUsername();
            String phoneNumber = "1234567891";
            String email = SimpleClient.getUser().getEmail();

            // Confirmation message
            String resultMessage = String.format("""
                Reservation Details:

                Date: %s
                Time: %s
                Restaurant: %s
                Seats: %d
                Inside: %b
                Worker Name: %s
                """,
                    selectedDateTime.toLocalDate(),
                    selectedDateTime.toLocalTime(),
                    restaurantName,
                    seats,
                    isInside,
                    fullName
            );

            showAlert("Reservation Confirmed", resultMessage);

            // Send reservation event
            FinalReservationEvent finalReservationEvent = new FinalReservationEvent(
                    true, restaurantName, selectedDateTime, seats, isInside, fullName, phoneNumber, email
            );

            try {
                SimpleClient.getClient().sendToServer(finalReservationEvent);
                reservationSuccessful = true;
            } catch (IOException e) {
                showAlert("Error", "Failed to send reservation to the server. Please try again.");
                e.printStackTrace();
            }
        }
    }

    // Method to round time to the nearest 15-minute interval
    private LocalTime roundUpToNearest15Minutes(LocalTime time) {
        int minutes = time.getMinute();
        int remainder = minutes % 15;
        if (remainder == 0) {
            return time.truncatedTo(ChronoUnit.MINUTES); // Already aligned
        }
        return time.plusMinutes(15 - remainder).truncatedTo(ChronoUnit.MINUTES);
    }





    @FXML
    void backToHome() throws IOException {
        Platform.runLater(() -> {
            try {
                EventBus.getDefault().unregister(this);
                App.setRoot("worker_screen"); // Navigate back to the main screen
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addMouseGlowEffect(Button button) {
        InnerShadow shadowEffect = new InnerShadow();
        shadowEffect.setRadius(30);  // Controls the light size
        shadowEffect.setColor(Color.rgb(255, 92, 92, 0.8)); // Light Red

        // Event to update light position dynamically
        button.setOnMouseMoved(event -> updateGlowEffect(shadowEffect, event));

        // Apply shadow when hovering
        button.setOnMouseEntered(event -> button.setEffect(shadowEffect));

        // Remove shadow when exiting
        button.setOnMouseExited(event -> button.setEffect(null));
    }

    private void updateGlowEffect(InnerShadow effect, MouseEvent event) {
        double x = event.getX() - 50;  // Adjust for better positioning
        double y = event.getY() - 20;
        effect.setOffsetX(x);
        effect.setOffsetY(y);
    }

    public void setBranch(String currentBranch) {
        this.currentRestaurant = currentBranch;
    }

    public void setRole(String currentWorker) {
        this.currentWorker= currentWorker;
    }
}