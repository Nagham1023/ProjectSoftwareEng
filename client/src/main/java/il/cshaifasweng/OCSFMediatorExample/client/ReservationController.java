package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.ReservationEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationController {

    @FXML
    private Button confirmButton;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Integer> hourComboBox;

    @FXML
    private ComboBox<Integer> minuteComboBox;

    @FXML
    public void initialize() {
        // Populate hour combo box (24-hour format: 0-23)
        for (int i = 0; i < 24; i++) {
            hourComboBox.getItems().add(i);
        }

        // Populate minute combo box (0-59, step 15 )
        for (int i = 0; i < 60; i += 15) {
            minuteComboBox.getItems().add(i);
        }
    }

    @FXML
    void handleConfirm(ActionEvent event) throws IOException {
        // Get selected date and time
        LocalDate selectedDate = datePicker.getValue();
        Integer selectedHour = hourComboBox.getValue();
        Integer selectedMinute = minuteComboBox.getValue();

        // Validate selections
        if (selectedDate == null) {
            showAlert("Error", "Please select a valid date!");
            return;
        }

        if (selectedHour == null || selectedMinute == null) {
            showAlert("Error", "Please select a valid hour and minute!");
            return;
        }

        // Create LocalDateTime from the selected values
        LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, LocalTime.of(selectedHour, selectedMinute));

        // Ensure the reservation is for a future date/time
        if (selectedDateTime.isBefore(LocalDateTime.now())) {
            showAlert("Error", "You cannot make a reservation in the past!");
            return;
        }

        // If all validations pass, show success message
        showAlert("Success", "Reservation confirmed for " + selectedDate + " at " + selectedHour + ":" + selectedMinute);
        ReservationEvent reservationEvent = new ReservationEvent("Test", selectedDateTime);
        SimpleClient.getClient().sendToServer(reservationEvent);//send it to the server
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
