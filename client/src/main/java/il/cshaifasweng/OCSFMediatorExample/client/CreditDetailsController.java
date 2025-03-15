package il.cshaifasweng.OCSFMediatorExample.client;

//import il.cshaifasweng.OCSFMediatorExample.entities.CreditCardCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.CreditCardCheck;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class CreditDetailsController {

    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardholderNameField;  // TextField for the cardholder's name
    @FXML
    private TextField CardholdersIDcardField;
    @FXML
    private TextField cvvnumber;
    @FXML
    private TextField Personalemail;
    @FXML
    private Button checkoutButton;
    @FXML
    private Label errorLabel;
    @FXML
    private Label errorLabelexpirydate;
    @FXML
    private Label errorLabelcvv;
    @FXML
    private Label errorLabelID;
    @FXML
    private Label errorLabelName;
    @FXML
    private Button arrow;

    @FXML
    private ComboBox<String> monthYearComboBox;
    @FXML
    private Label emailErrorLabelC;
    private BooleanProperty emailInteractedC = new SimpleBooleanProperty(false);  // Track interaction

    @FXML
    private void initialize() {
        EventBus.getDefault().register(this);
        setupCardNumberField();
        setupCardholderNameField();
        setupIDCardField();
        setupCVVField();
        setupMonthYearComboBox();
        setupBindings();
        setuparrowButton();
        setupEmailField();
        checkoutButton.setOnAction(event -> sendCreditCardDetailsToServer());
    }

    private void setupBindings() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");

        BooleanBinding isCardNumberValid = cardNumberField.textProperty().length().isEqualTo(19);
        BooleanBinding isCardholderNameValid = cardholderNameField.textProperty().isNotEmpty();
        BooleanBinding isIDValid = CardholdersIDcardField.textProperty().length().isEqualTo(9);
        BooleanBinding isCvvValid = cvvnumber.textProperty().length().isEqualTo(3);
        BooleanBinding isMonthYearValid = Bindings.createBooleanBinding(() -> {
            if (monthYearComboBox.getValue() != null) {
                try {
                    YearMonth selectedYearMonth = YearMonth.parse(monthYearComboBox.getValue(), formatter);
                    YearMonth currentYearMonth = YearMonth.now();
                    return !selectedYearMonth.isBefore(currentYearMonth);
                } catch (DateTimeParseException e) {
                    return false;
                }
            }
            return false;
        }, monthYearComboBox.valueProperty());

        BooleanBinding isAllValid = isCardNumberValid
                .and(isCardholderNameValid)
                .and(isIDValid)
                .and(isCvvValid)
                .and(isMonthYearValid)
                .and(emailInteractedC);

        checkoutButton.disableProperty().bind(isAllValid.not());
    }

    @FXML
    void checkoutFunction(ActionEvent event) {
        //yousef
        //SimpleClient simpleClient = ne

    }







    private void setupCardNumberField() {
        cardNumberField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            // Only allow numeric input and spaces, block if the limit is reached
            String txt = cardNumberField.getText();
            if (!Character.isDigit(event.getCharacter().charAt(0)) && !event.getCharacter().equals(" ")) {
                event.consume();  // Ignore non-digits and non-space characters
            } else if ((txt.length() == 19 && !event.getCharacter().equals(" ")) ||
                    getDigitCount(txt) >= 16) {
                event.consume();  // Prevent further typing if the maximum number of digits (16) has been reached
            }
        });

        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) return;  // Skip empty input
            String formattedValue = formatCardNumber(newValue);
            if (!newValue.equals(formattedValue)) {
                Platform.runLater(() -> {
                    cardNumberField.setText(formattedValue);
                    cardNumberField.positionCaret(formattedValue.length()); // Move caret to the end
                });
            }
            // Clear error message dynamically when valid input is provided
            if (getDigitCount(newValue) == 16) {
                errorLabel.setText("");
            }
        });

        cardNumberField.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                validateCardNumber(); // Validate when the field loses focus
            }
        });
    }

    private void validateCardNumber() {
        String numericText = cardNumberField.getText().replaceAll("\\s", "");
        if (numericText.length() != 16) {
            errorLabel.setText("The card number must be exactly 16 digits.");
        } else {
            errorLabel.setText("");  // Clear error message if the input is valid
        }
    }

//    private String formatCardNumber(String text) {
//        String digitsOnly = text.replaceAll("\\s+", "");
//        StringBuilder formatted = new StringBuilder();
//        for (int i = 0; i < digitsOnly.length(); i++) {
//            if (i > 0 && i % 4 == 0) {
//                formatted.append(" "); // Insert space every four characters
//            }
//            formatted.append(digitsOnly.charAt(i));
//        }
//        return formatted.toString();
//    }

    private int getDigitCount(String text) {
        return text.replaceAll("\\s+", "").length();
    }



    private void setupCardholderNameField() {
        cardholderNameField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char typedCharacter = event.getCharacter().charAt(0);
            if (!Character.isLetter(typedCharacter) && !Character.isWhitespace(typedCharacter)) {
                event.consume();  // Ignore non-letter characters
            }
        });

        cardholderNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            cardholderNameField.setText(newValue.toUpperCase());  // Convert to upper case
            if (!newValue.isEmpty()) {
                errorLabelName.setVisible(false);  // Hide error if field is not empty
            }
        });

        cardholderNameField.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {  // When focus is lost
                if (cardholderNameField.getText().trim().isEmpty()) {
                    errorLabelName.setText("This field is required.");
                    errorLabelName.setVisible(true);  // Show error if field is empty
                } else {
                    errorLabelName.setVisible(false);  // Otherwise hide the error
                }
            }
        });
    }


    private void setupMonthYearComboBox() {
    List<String> monthYears = new ArrayList<>();
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();
    String currentMonthYear = String.format("%02d/%d", currentMonth, currentYear); // Current month/year string
    boolean currentMonthYearFound = false;

    for (int year = currentYear; year <= currentYear + 10; year++) {
        for (int month = 1; month <= 12; month++) {
            String monthYear = String.format("%02d/%d", month, year);
            monthYears.add(monthYear);
            if (monthYear.equals(currentMonthYear)) {
                currentMonthYearFound = true;
            }
        }
    }
    monthYearComboBox.getItems().setAll(monthYears);

    // Set the current month and year as the selected item if found
    if (currentMonthYearFound) {
        monthYearComboBox.getSelectionModel().select(currentMonthYear);
    } else {
        monthYearComboBox.getSelectionModel().selectFirst();  // Fallback to the first item if current month/year not found
    }

    monthYearComboBox.setOnAction(event -> {
        if (monthYearComboBox.getValue() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
            try {
                YearMonth selectedYearMonth = YearMonth.parse(monthYearComboBox.getValue(), formatter);
                YearMonth currentYearMonth = YearMonth.now();

                // Check if the selected year-month is before the current year-month
                if (selectedYearMonth.isBefore(currentYearMonth)) {
                    System.out.println("Date is in the past.");
                    errorLabelexpirydate.setText("Expiration date must be in the future.");
                } else {
                    System.out.println("Date is valid.");
                    errorLabelexpirydate.setText("");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Parsing failed: " + e.getMessage());
                errorLabelexpirydate.setText("Invalid date format.");
            }
        } else {
            errorLabelexpirydate.setText("No date selected.");
        }
    });
}



    private String formatCardNumber(String text) {
        String digitsOnly = text.replaceAll("\\s+", "");
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < digitsOnly.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" "); // Insert space every four characters
            }
            formatted.append(digitsOnly.charAt(i));
        }
        return formatted.toString();
    }

    private void adjustCaretPosition(String formattedText) {
        // Move caret position to the end of text
        Platform.runLater(() -> cardNumberField.positionCaret(formattedText.length()));
    }

    private void setupIDCardField() {
        CardholdersIDcardField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String input = event.getCharacter();
            if (!input.matches("\\d")) { // Allow only digits
                event.consume();
            }
        });

        CardholdersIDcardField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 9) {
                CardholdersIDcardField.setText(oldValue); // Limit to 9 digits
            }
        });

        CardholdersIDcardField.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            System.out.println("Focus lost: " + CardholdersIDcardField.getText()); // Debug output
            if (!isFocused && CardholdersIDcardField.getText().length() != 9) {
                System.out.println("Setting error text."); // Debug output
                errorLabelID.setText("ID must be exactly 9 digits.");
            } else {
                errorLabelID.setText(""); // Clear error message
            }
        });
    }


    private void setupCVVField() {
        cvvnumber.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d")) { // Allow only digits
                event.consume(); // Ignore non-digits
            }
        });

        cvvnumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,3}")) { // Allow up to 3 digits only
                cvvnumber.setText(oldValue);
            }
            // Clear error message dynamically when valid input is provided
            if (newValue.length() == 3) {
                errorLabelcvv.setText("");
            }
        });

        cvvnumber.focusedProperty().addListener((observable, wasFocused, isNowFocused) -> {
            if (!isNowFocused) { // Only when focus is lost
                validateCVV(); // Validate CVV when field loses focus
            }
        });
    }

    private void validateCVV() {
        // Check if the CVV length is exactly 3 digits
        if (cvvnumber.getText().length() != 3) {
            errorLabelcvv.setText("CVV must be exactly 3 digits.");
        } else {
            errorLabelcvv.setText(""); // Clear error message if the input is valid
        }
    }

    private void setupEmailField() {
        // Validate email on text change and mark interaction
        Personalemail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!emailInteractedC.get()) {
                emailInteractedC.set(true); // Mark as interacted on first text change
            }
            validateEmail();
        });

        // Add this if you need to validate on focus loss, though it might be redundant
        Personalemail.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            if (!isFocused) {
                validateEmail();
            }
        });
    }




    private void validateEmail() {
        String email = Personalemail.getText();
        boolean valid = false;
        if (email.isEmpty()) {
            emailErrorLabelC.setText("This field is required.");
            System.out.println("Email validation: field is required");
        } else if (!email.matches("^[\\w.+\\-]+@gmail\\.com$")) {
            emailErrorLabelC.setText("Email should be correct and end with @gmail.com");
            System.out.println("Email validation: incorrect format");
        } else {
            emailErrorLabelC.setText(""); // Clear the error message if valid
            valid = true;  // Only set valid to true if email is correctly formatted
            System.out.println("Email validation: correct format");
        }
        emailInteractedC.set(valid);
        System.out.println("isEmailValid set to: " + valid);
    }




    private void setuparrowButton() {
        arrow.setOnAction(event -> {
            try {
                goback();
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }

    private void goback() throws IOException {
        App.setRoot("deliverypage");
    }
/************************************new***********************************************/
@Subscribe
public void onCreditCardCheckResponse(CreditCardCheck creditCardCheck) {
    // This method gets called when a CreditCardCheck object is posted to the EventBus
    Platform.runLater(() -> {
        if (creditCardCheck.isValid()) {
            // If the credit card is valid, update UI to reflect success or proceed to next steps
            System.out.println("Credit card is valid.");
            // For example, clear any error messages and proceed to a confirmation page or enable further actions
            errorLabel.setText("");
            // Maybe navigate to a success page or enable a purchase button
            // navigateToSuccessPage(); // This is a hypothetical method call
        } else {
            // If the credit card is not valid, update UI to show the error message
            System.out.println("Credit card validation failed.");
            errorLabel.setText(creditCardCheck.getRespond());
            // Display more details or log them, ensure the error message is set to display why validation failed
        }
    });
}

//    public void sendCreditCardDetailsToServer() {
//        String cardNumber = cardNumberField.getText().trim();
//        String cardholderName = cardholderNameField.getText().trim();
//        String cardholdersID = CardholdersIDcardField.getText().trim();
//        String cvv = cvvnumber.getText().trim();
//        String expiryDateStr = monthYearComboBox.getValue();  //  the expiry date is selected from a ComboBox and is in the format "MM/yyyy"
//
//        // Parse the expiry date string to LocalDate
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
//        String expiryDate = null;
//        try {
//            YearMonth.parse(expiryDateStr, formatter); // This is just for validation
//            expiryDate = expiryDateStr; // Since the format is correct, assign it directly
//        } catch (DateTimeParseException e) {
//            System.err.println("Failed to parse expiry date: " + e.getMessage());
//            errorLabel.setText("Invalid expiry date format.");
//            return; // Exit if parsing fails
//        }
//
//        // Create a CreditCardCheck instance and set values
//        CreditCardCheck creditCardCheck = new CreditCardCheck();
//        creditCardCheck.setCardNumber(cardNumber);
//        creditCardCheck.setCardholderName(cardholderName);
//        creditCardCheck.setCardholdersID(cardholdersID);
//        creditCardCheck.setCvv(cvv);
//        creditCardCheck.setExpiryDate(expiryDate);
//
//        // Send this information to the server using SimpleClient
//        try {
//            SimpleClient client = SimpleClient.getClient();
//            if (!client.isConnected()) {
//                client.openConnection();  // Attempt to open the connection if not already connected
//            }
//            client.sendToServer(creditCardCheck);
//        } catch (IOException e) {
//            System.err.println("Error sending credit card details to server: " + e.getMessage());
//            errorLabel.setText("Failed to connect to server. Check connection and try again.");
//        }
//
//    }

    public void sendCreditCardDetailsToServer() {
        String cardNumber = cardNumberField.getText().trim();
        String cardholderName = cardholderNameField.getText().trim();
        String cardholdersID = CardholdersIDcardField.getText().trim();
        String cvv = cvvnumber.getText().trim();
        String expiryDateStr = monthYearComboBox.getValue();  // Assuming the expiry date is selected from a ComboBox and is in the format "MM/yyyy"

        // Attempt to validate and then directly use the expiry date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            System.out.println("Inserting credit card details to server...");
            // This is just for validation to ensure the format is correct
            YearMonth.parse(expiryDateStr, formatter);

            // Since the format is correct, assign it directly
            CreditCardCheck creditCardCheck = new CreditCardCheck();
            creditCardCheck.setCardNumber(cardNumber);
            creditCardCheck.setCardholderName(cardholderName);
            creditCardCheck.setCardholdersID(cardholdersID);
            creditCardCheck.setCvv(cvv);
            creditCardCheck.setExpiryDate(expiryDateStr); // Store as String

            // Send this information to the server using SimpleClient
            try {
                SimpleClient client = SimpleClient.getClient();
                client.sendToServer(creditCardCheck);
            } catch (IOException e) {
                System.err.println("Error sending credit card details to server: " + e.getMessage());
                errorLabel.setText("Failed to send data to server.");  // Update UI to show error message
            }
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse expiry date: " + e.getMessage());
            errorLabel.setText("Invalid expiry date format.");
        }
    }




}

