package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import static il.cshaifasweng.OCSFMediatorExample.client.ReservationController.noValidation;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.deliveryPrice;


public class CreditDetailsController {

    static public Order done_Order;
    static public ReservationSave done_Reservation;
    static public String mode;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField cardholderNameField;  // TextField for the cardholder's name
    @FXML
    private TextField CardholdersIDcardField;
    @FXML
    private TextField cvvnumber;
    @FXML
    private Button savecreditButton;

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


    static public PersonalDetails personalDetails;
    @FXML
    private ComboBox<CreditCard> savedCardsComboBox;

//    @FXML
//    private ListView<CreditCard> creditCardListView; // Example component for displaying cards


    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        setupCardNumberField();
        setupCardholderNameField();
        setupIDCardField();
        setupCVVField();
        setupMonthYearComboBox();
        setupBindings();
        setuparrowButton();

        savedCardsComboBox.setOnAction(event -> handleCardSelection());
        /*sending to get all the cc*/
        try {
            System.out.println("sending to server");
            SimpleClient.getClient().sendToServer(personalDetails);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        savecreditButton.setOnAction(event -> sendCreditCardDetailsToServer());

    }

    private void handleCardSelection() {
        CreditCard selectedCard = savedCardsComboBox.getValue();

        boolean isNoneSelected = (selectedCard == null);

        // Enable/Disable fields based on selection
        cardNumberField.setDisable(!isNoneSelected);
        cardholderNameField.setDisable(!isNoneSelected);
        monthYearComboBox.setDisable(!isNoneSelected);
        cvvnumber.setDisable(!isNoneSelected);
        CardholdersIDcardField.setDisable(!isNoneSelected);
        if (savecreditButton.disableProperty().isBound()) {
            savecreditButton.disableProperty().unbind();
        }

        savecreditButton.setDisable(isNoneSelected);

        // Fill details if a card is selected, otherwise clear fields
        if (selectedCard != null) {
            errorLabel.setText("");
            errorLabelcvv.setText("");
            errorLabelID.setText("");
            errorLabelName.setText("");
            errorLabelexpirydate.setText("");
            emailErrorLabelC.setText("");
            cvvnumber.setText("***");
            cardholderNameField.setText(selectedCard.getCardholderName());
            monthYearComboBox.setValue(selectedCard.getExpiryDate());
            cardNumberField.setText("**** **** **** " + selectedCard.getCardNumber().substring(selectedCard.getCardNumber().length() - 4));
            CardholdersIDcardField.setText(selectedCard.getCardholdersID());
            setupBindings();
        } else {
            clearFields();
            setupBindings();
        }
    }


    private void clearFields() {
        cardNumberField.clear();
        cardholderNameField.clear();
        //monthYearComboBox.clear();
        cvvnumber.clear();
        CardholdersIDcardField.clear();
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

        BooleanBinding isAllValid;

        if(savedCardsComboBox.getValue() != null) {
            isAllValid = isCardNumberValid
                    .and(isCardholderNameValid)
                    .and(isIDValid)
                    .and(isMonthYearValid);
        }
        else isAllValid = isCardNumberValid
                .and(isCardholderNameValid)
                .and(isIDValid)
                .and(isCvvValid)
                .and(isMonthYearValid);
//                .and(emailInteractedC);
        savecreditButton.disableProperty().bind(isAllValid.not());
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
    private void setupMonthYearComboBox() {List<String> monthYears = new ArrayList<>();
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
        } else {monthYearComboBox.getSelectionModel().selectFirst();  // Fallback to the first item if current month/year not found
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
    private void setuparrowButton() {
        arrow.setOnAction(event -> {
            try {
                if(mode.equals("Order")){
                System.out.println("Arrow button pressed.");
                App.setRoot("deliverypage");}
                else{
                    App.setRoot("Reservation");
                }
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling
            }
        });
    }
    /************************************new***********************************************/
    @Subscribe
    public void onPaymentResponse(PaymentCheck creditCardCheck) {
        // This method gets called when a CreditCardCheck object is posted to the EventBus
        if(creditCardCheck.getMode().equals("Order")){
        Platform.runLater(() -> {

            System.out.println("Credit card is valid." + creditCardCheck.getOrder());
            errorLabel.setText(creditCardCheck.getResponse());
            done_Order = creditCardCheck.getOrder();
            try {
                App.setRoot("receipt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        } else {
            errorLabel.setText(creditCardCheck.getResponse());
            Platform.runLater(() -> {
                try {
                    //errorLabel.setText(creditCardCheck.getResponse());
                    errorLabel.setText(creditCardCheck.getResponse());
                    if(!(creditCardCheck.getResponse().equals("Payment Failed"))){
                    sendReservationConfirmationEmail(CreditDetailsController.personalDetails,done_Reservation.getReservationSaveID(), done_Reservation.getRestaurantName(),done_Reservation.getSeats() );
                    App.setRoot("mainScreen");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    public void sendCreditCardDetailsToServer() {
        String cardNumber = cardNumberField.getText().trim();
        String cardholderName = cardholderNameField.getText().trim();
        String cardholdersID = CardholdersIDcardField.getText().trim();
        String cvv = cvvnumber.getText().trim();
        String expiryDateStr = monthYearComboBox.getValue();  // the expiry date is selected from a ComboBox and is in the format "MM/yyyy"
        if(mode.equals("Order")){
            done_Order.setDate(LocalDate.now());
            done_Order.setOrderTime(LocalDateTime.now());
            if(done_Order.getOrderType().equals("Delivery"))
            {
             done_Order.setTotal_price(done_Order.getTotal_price()+deliveryPrice);
            }
        }

        // Attempt to validate and then directly use the expiry date string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        try {
            System.out.println("Inserting credit card details to server...");
            // This is just for validation to ensure the format is correct
            YearMonth.parse(expiryDateStr, formatter);
            // Since the format is correct, assign it directly
            CreditCard creditcard = new CreditCard();
            creditcard.setCardNumber(cardNumber);
            creditcard.setCardholderName(cardholderName);
            creditcard.setCardholdersID(cardholdersID);
            creditcard.setCvv(cvv);
            creditcard.setExpiryDate(expiryDateStr); // Store as String
            PaymentCheck paymentCheck;

            if(savedCardsComboBox.getValue() != null) {
                System.out.println("selected credit card");
                if(mode.equals("Order")) {
                    paymentCheck = new PaymentCheck(savedCardsComboBox.getValue(), personalDetails, done_Order, "Order");
                } else {
                    paymentCheck = new PaymentCheck(savedCardsComboBox.getValue(), personalDetails,done_Reservation,"Reservation");
                }
            }
            else {
                if(mode.equals("Order")) {
                    paymentCheck = new PaymentCheck(creditcard, personalDetails, done_Order, "Order");
                }
                else {
                    paymentCheck = new PaymentCheck(creditcard, personalDetails,done_Reservation, "Reservation");
                }

                System.out.println("new credit card");
            }
            try {
                SimpleClient client = SimpleClient.getClient();
                client.sendToServer(paymentCheck);
            } catch (IOException e) {
                System.err.println("Error sending credit card details to server: " + e.getMessage());
                errorLabel.setText("Failed to send data to server.");  // Update UI to show error message
            }
        } catch (DateTimeParseException e) {
            System.err.println("Failed to parse expiry date: " + e.getMessage());
            errorLabel.setText("Invalid expiry date format.");
        }
    }

    @Subscribe
    public void onCreditCardDetailsReceived(ListOfCC creditCards) {
        Platform.runLater(() -> {
            System.out.println("getting from server");
            savedCardsComboBox.getItems().clear();
            savedCardsComboBox.getItems().add(null); // First option
            for (CreditCard creditCard : creditCards.getCreditCards()) {
                savedCardsComboBox.getItems().add(creditCard);
            }
            savedCardsComboBox.getSelectionModel().selectFirst();

        });
    }

    @Subscribe
    public void noCc(String msg) {
        System.out.println(msg);
        savedCardsComboBox.getItems().clear();
        savedCardsComboBox.getItems().add(null); // First option
        savedCardsComboBox.getSelectionModel().selectFirst();
    }

    public static void sendReservationConfirmationEmail(PersonalDetails customer, int orderNumber,
                                                  String restaurantName, int seats) {
        // Create the subject
        String subject = "Reservation Confirmation - Order #" + orderNumber;

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        // Construct the body of the email
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(customer.getName()).append(",\n\n");
        body.append("Thank you for your order at ").append(restaurantName).append("!\n\n");
        body.append("Here are the details of your reservation").append("\n");
        body.append("Order Date: ").append(currentDate).append("\n\n");
        body.append("number of seats:\n");

        body.append("- ").append(seats).append("\n");

        body.append("\nWe hope you enjoy your visit!\n\n");
        body.append("Best regards,\n");
        body.append("Mama's Restaurant Team");

        // Now you can call the email sender with the subject, body, and the customer's email
        EmailSender.sendEmail(subject, body.toString(), customer.getEmail());
    }

    @Subscribe
    public void goBackToReservation(FaildPayRes event){
        Platform.runLater(() -> {
            try {
                event.setPersonalDetails(personalDetails);
                event.setHasBeenHereBefore(true);
                SimpleClient client = SimpleClient.getClient();
                client.sendToServer(event);
                EventBus.getDefault().unregister(this);
                App.setRoot("Reservation");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
    }
}

