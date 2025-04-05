package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.CreditCard;
import il.cshaifasweng.OCSFMediatorExample.entities.PersonalDetails;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.done_Order;

public class PersonalDetailsPageController {

    @FXML
    private Button ContinuePersonalDetails;

    @FXML
    private TextField EmailPersonalDetails;

    @FXML
    private TextField NamePersonalDetails;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private TextField PhoneNumberPersonalDetails;


    @FXML
    private Button arrowPersonalDetails;

    @FXML
    private Label phoneNumberErrorLabel;

    @FXML
    private Label emailErrorLabel;
    private BooleanProperty emailInteracted = new SimpleBooleanProperty(false);  // Track interaction

    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label statusLabel;
    // Declare `personalDetails` as a class member if not already
    public PersonalDetails personalDetails;

    @FXML
    private void initialize() {
        //EventBus.getDefault().register(this);
        setupEmailField();
        setupPhoneNumberField();
        setupNameField();
        setupContinueButton();
        //setupContinueeButton();
        ContinuePersonalDetails.setOnAction(event -> handleContinueAction(event));
    }

    private void setupNameField() {
        // Listener for when the focus is lost from the Name field
        NamePersonalDetails.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) { // Focus is lost
                validateNameField();
            }
        });

        // Listener for changes in the text property of the Name field
        NamePersonalDetails.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNameField();
        });
    }

    private void validateNameField() {
        if (NamePersonalDetails.getText().trim().isEmpty()) {
            nameErrorLabel.setText("This field is required.");
        } else {
            nameErrorLabel.setText(""); // Clear the error message if valid
        }
    }

    private void setupEmailField() {
        EmailPersonalDetails.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {
                emailInteracted.set(true);  // Set interacted to true when focus is lost
                validateEmail();
            }
        });

        EmailPersonalDetails.textProperty().addListener((observable, oldValue, newValue) -> {
            if (emailInteracted.get()) {
                validateEmail();
            }
        });
    }

    private void validateEmail() {
        if (!emailInteracted.get()) {
            return;  // Skip validation if there hasn't been any interaction
        }

        String email = EmailPersonalDetails.getText();
        if (email.isEmpty()) {
            emailErrorLabel.setText("This field is required.");
        } else if (!email.matches("^[\\w.+\\-]+@gmail\\.com$")) {
            emailErrorLabel.setText("Email should end with @gmail.com");
        } else {
            emailErrorLabel.setText(""); // Clear the error message if valid
        }
    }
    private boolean phoneNumberFieldInteracted = false; // Initialize as false
    private void setupPhoneNumberField() {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (!newText.matches("\\+972 5\\d{0,10}")) {  // Allow up to 10 digits after the prefix
                return null;  // reject the change if not matching
            }
            if (newText.length() > 14) {  // limit length
                return null;
            }
            return change;  // accept the change otherwise
        });
        PhoneNumberPersonalDetails.setTextFormatter(formatter);
        PhoneNumberPersonalDetails.setText("+972 5");  // Set initial text
        PhoneNumberPersonalDetails.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (isFocused) {
                phoneNumberFieldInteracted = true;  // User has interacted with the field
            }
            if (!isFocused) {
                validatePhoneNumber(PhoneNumberPersonalDetails.getText()); // Validate when focus is lost
            }
        });
        PhoneNumberPersonalDetails.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePhoneNumber(newValue); // Validate after any change to dynamically clear or show the error
        });
    }
    private void validatePhoneNumber(String text) {
        String rawNumber = text.length() > 6 ? text.substring(7) : "";  // Skip "+972 05"
        if (rawNumber.length() == 7) {
            phoneNumberErrorLabel.setText("");  // Clear any error message if the number is valid
        } else if (rawNumber.isEmpty() && phoneNumberFieldInteracted) {
            phoneNumberErrorLabel.setText("This field is required.");
        } else if (!rawNumber.isEmpty() && rawNumber.length() != 7 && !PhoneNumberPersonalDetails.isFocused()) {
            phoneNumberErrorLabel.setText("Phone number must be exactly 10 digits (excluding +972 05).");
        } else {
            phoneNumberErrorLabel.setText("");  // Clear the error message if the field is still focused or the error doesn't apply
        }
    }
    private void setupContinueButton() {
        // Validate the name field: It should not be empty.
        BooleanBinding isNameValid = Bindings.createBooleanBinding(() ->
                        !NamePersonalDetails.getText().trim().isEmpty(),
                NamePersonalDetails.textProperty());
        // Validate the phone number field: It should have exactly 15 characters (including +972 05).
        BooleanBinding isPhoneNumberValid = Bindings.createBooleanBinding(() ->
                        PhoneNumberPersonalDetails.getText().length() == 14,
                PhoneNumberPersonalDetails.textProperty());
        // Validate the Email field: It should match the Gmail regex pattern.
        BooleanBinding isEmailValid = Bindings.createBooleanBinding(() -> {
            String email = EmailPersonalDetails.getText();
            return email.matches("^[\\w.+\\-]+@gmail\\.com$");
        }, EmailPersonalDetails.textProperty());
        // Bind the disable property of the continue button to the NOT of all fields being valid.
        ContinuePersonalDetails.disableProperty().bind(
                isNameValid.not()
                        .or(isPhoneNumberValid.not())
                        .or(isEmailValid.not()));
    }
    /******************************************************/
    @FXML
    void backToCart(ActionEvent event) {
        try {
            App.setRoot("Cart_page");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void handleContinueAction(ActionEvent event) {
        // Initialize and set up personalDetails here
        //System.out.println("here3");
        personalDetails = new PersonalDetails();
        personalDetails.setName(NamePersonalDetails.getText().trim());
        personalDetails.setEmail(EmailPersonalDetails.getText().trim());
        personalDetails.setPhoneNumber(PhoneNumberPersonalDetails.getText().trim());



        // Send data to the server
        try {
            // Navigate to the next screen after handling the server interaction

            //System.out.println("here2");

            DeliveryPageController.personalDetails = personalDetails;
            CreditDetailsController.personalDetails = personalDetails;
            CreditDetailsController.mode="Order";
            done_Order.setCustomerEmail(EmailPersonalDetails.getText());
            App.setRoot("deliverypage");
        } catch (IOException e) {
            System.err.println("Error in handleContinueAction: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

