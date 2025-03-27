package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.regex.Pattern;

public class PersonalInfoController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField genderField;


    @FXML
    private TextField workerIdField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField roleField;

    @FXML
    private Button saveButton;

    @FXML
    private Label statusLabel;

    private UserCheck currentUser;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Method to initialize the user's information
    public void setUser(UserCheck user) {
        this.currentUser = user;

        usernameField.setText(user.getUsername());
        genderField.setText(user.getGender());
        ageField.setText(String.valueOf(user.getAge()));
        roleField.setText(user.getRole());
        workerIdField.setText(String.valueOf(user.getId()));

        emailField.setText(user.getEmail());
        passwordField.setText(""); // Empty for security reasons
    }

    @FXML
    private void handleSave() {
        if (currentUser == null) {
            statusLabel.setText("Error: No user loaded.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        String newEmail = emailField.getText().trim();
        String newPassword = passwordField.getText().trim();

        if (newEmail.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("You've to update your email or password.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
            statusLabel.setText("Invalid email format. Must be a valid email (e.g., example@domain.com).");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        // Update user details
        currentUser.setEmail(newEmail);
        currentUser.setPassword(newPassword);
        currentUser.setState(5);

        try {
            saveUserToDatabase(currentUser);
            statusLabel.setText("Information updated successfully.");
            statusLabel.setTextFill(Color.GREEN);
        } catch (Exception e) {
            statusLabel.setText("Error updating information.");
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    // Simulate saving to database (replace with your database logic)
    private void saveUserToDatabase(UserCheck user) {
        try {
            SimpleClient.getClient().sendToServer(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            setUser(SimpleClient.getUser());
        });
        statusLabel.setText("");
    }
}
