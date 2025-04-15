package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;
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

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Update");
        confirmationAlert.setHeaderText("Are you sure you want to save the changes?");
        confirmationAlert.setContentText("Your email and/or password will be updated.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentUser.setEmail(newEmail);
            currentUser.setPassword(newPassword);
            currentUser.setState(5);

            try {
                saveUserToDatabase(currentUser);
                statusLabel.setText("Information updated successfully.");
                statusLabel.setTextFill(Color.GREEN);

                showSavedPopup(); // ✨ Show the fading popup here

            } catch (Exception e) {
                statusLabel.setText("Error updating information.");
                statusLabel.setTextFill(Color.RED);
                e.printStackTrace();
            }
        } else {
            statusLabel.setText("Update canceled.");
            statusLabel.setTextFill(Color.GRAY);
        }
    }
    private void showSavedPopup() {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);

        Label savedLabel = new Label("✔ Saved");
        savedLabel.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 30;");
        StackPane root = new StackPane(savedLabel);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root);
        scene.setFill(null);
        popupStage.setScene(scene);
        popupStage.setAlwaysOnTop(true);

        // Center on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        popupStage.setX(screenBounds.getMinX() + screenBounds.getWidth() / 2 - 100);
        popupStage.setY(screenBounds.getMinY() + screenBounds.getHeight() / 2 - 50);

        popupStage.show();

        // Fade out transition
        FadeTransition fade = new FadeTransition(Duration.seconds(2), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(1));
        fade.setOnFinished(event -> popupStage.close());
        fade.play();
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

