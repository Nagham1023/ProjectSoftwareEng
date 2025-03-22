package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import il.cshaifasweng.OCSFMediatorExample.entities.complainEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class AddComplainController {
    String ComplainKind = null;
    String name;
    String email;
    String tell;
    Date date;
    Time time;
    Restaurant restaurant_chosen = null;
    String status = "do";


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button ComplainButton;

    @FXML
    private Button FeedbackButton;

    @FXML
    private Button SuggestionButton;

    @FXML
    private ComboBox<String> branchesList;

    @FXML
    private Button backButton;

    @FXML
    private Label checkLabel;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ImageView logoImage;

    @FXML
    private Button sendButton;

    @FXML
    private TextArea textAreaTellUs;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private TextField textFieldOrderNum;

    @FXML
    private TextField textFieldName;

    private String nameValue = "";
    private RestaurantList restaurantList = new RestaurantList();
    private String response = "";
    private String orderNumValue = "";
    private double refundVal = 0;


    @FXML
    public void initialize() {
        textFieldOrderNum.setVisible(false);
        EventBus.getDefault().register(this);
        SimpleClient client = SimpleClient.getClient();
        try {
            client.sendToServer("getAllRestaurants");
            System.out.println("here first");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void handle( RestaurantList restaurantList) {
        Platform.runLater(() -> {
            this.restaurantList = restaurantList;
            fillComboBox(restaurantList);
        });

    }

    @FXML
    private void ComplainButton(ActionEvent event) throws IOException {
        ComplainKind = "Complaint";
        ComplainButton.setStyle("-fx-background-color: linear-gradient(to bottom, #C76A58, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        FeedbackButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        SuggestionButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        textFieldOrderNum.setVisible(true);
    }

    @FXML
    private void FeedbackButton(ActionEvent event) throws IOException {
        ComplainKind = "Feedback";
        ComplainButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        FeedbackButton.setStyle("-fx-background-color: linear-gradient(to bottom, #C76A58, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        SuggestionButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        textFieldOrderNum.setVisible(false);

    }
    @FXML
    private void SuggestionButton(ActionEvent event) throws IOException {
        ComplainKind = "Suggestion";
        ComplainButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        FeedbackButton.setStyle("-fx-background-color: linear-gradient(to bottom, #832018, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        SuggestionButton.setStyle("-fx-background-color: linear-gradient(to bottom, #C76A58, #9a2c25); -fx-background-radius: 20; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0, 1, 1);");
        textFieldOrderNum.setVisible(false);

    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @FXML
    private void sendButton(ActionEvent event) throws IOException {
        getRestaurantByName(restaurantList);

        // Check if any field is empty
        if (ComplainKind == null || textFieldName.getText() == null || textFieldEmail.getText() == null ||
                textAreaTellUs.getText() == null || datePicker.getValue() == null || branchesList.getValue() == null ||
                (orderNumValue == null && Objects.equals(ComplainKind, "Complaint"))) {
            checkLabel.setText("There is at least one field empty!");
        } else if (!isValidEmail(textFieldEmail.getText())) {
            // If the email is not valid, show a message
            checkLabel.setText("Please enter a valid email address!");
        } else {
            try {
                sendAndSaveComplain();
                checkLabel.setText("Sent Successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }



    private void sendAndSaveComplain() throws IOException {
        LocalDateTime timeNow = LocalDateTime.now(); // Get the current time
        complainEvent complainEvent = new complainEvent(ComplainKind,textFieldName.getText(),textFieldEmail.getText(),textAreaTellUs.getText(),datePicker.getValue(),timeNow,restaurant_chosen,"Do",response,textFieldOrderNum.getText(),refundVal);
        SimpleClient client;
        client = SimpleClient.getClient();
        client.sendToServer(complainEvent);
        checkLabel.setText("Sent Successfully");
    }
    @FXML
    private void backButton(ActionEvent event)throws IOException {
        App.setRoot("mainScreen");
    }


    public void fillComboBox(RestaurantList restaurantList) {
        branchesList.getItems().clear();
        System.out.println("here");
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        List<String> branches_List = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            branchesList.getItems().add(restaurant.getRestaurantName()); // Add each restaurant name
        }
    }

    public void getRestaurantByName(RestaurantList restaurantList) {

        nameValue=branchesList.getValue();
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getRestaurantName().equals(nameValue)) {
                restaurant_chosen = restaurant;
            }
        }

    }
    @Subscribe
    public void onComplainEvent(complainEvent event) {
        Platform.runLater(() -> {
            checkLabel.setText("Complaint received successfully!");
            String subject;
            String body;
            String customerMessage = event.getTell(); // Get what the customer said

            switch (event.getKind()) {
                case "Complaint": {
                    subject = "We’re Addressing Your Concern - Mama's Restaurant";
                    body = "Dear " + event.getName() + ",\n\n"
                            + "Thank you for bringing your concern to our attention. We deeply apologize for any inconvenience you may have experienced during your visit to Mama's Restaurant.\n\n"
                            + "Your satisfaction is our priority, and we are currently reviewing your complaint to resolve the issue promptly. Our team will reach out to you soon with a follow-up.\n\n"
                            + "Here’s what you told us:\n"
                            + "----------------------------------------\n"
                            + customerMessage + "\n"
                            + "----------------------------------------\n\n"
                            + "We appreciate your patience and the opportunity to make things right. If there’s anything else you’d like to share, please feel free to reply to this email.\n\n"
                            + "Warm regards,\n\n"
                            + "Mama's Restaurant Team\n";
                    break;
                }
                case "Suggestion": {
                    subject = "Thank You for Your Suggestion - Mama's Restaurant";
                    body = "Dear " + event.getName() + ",\n\n"
                            + "Thank you for taking the time to share your suggestion with us. We genuinely value your input as it helps us improve and provide the best dining experience possible.\n\n"
                            + "Your idea has been shared with our team, and we will carefully consider it as we continue enhancing our services and menu.\n\n"
                            + "Here’s what you suggested:\n"
                            + "----------------------------------------\n"
                            + customerMessage + "\n"
                            + "----------------------------------------\n\n"
                            + "We truly appreciate your support and look forward to welcoming you back to Mama's Restaurant soon!\n\n"
                            + "Warm regards,\n\n"
                            + "Mama's Restaurant Team\n";
                    break;
                }
                case "Feedback": {
                    subject = "Thank You for Your Feedback - Mama's Restaurant";
                    body = "Dear " + event.getName() + ",\n\n"
                            + "We truly appreciate you taking the time to share your feedback with us. Your thoughts are incredibly valuable and help us maintain the quality and service our customers expect.\n\n"
                            + "We’re always working to improve, and your feedback plays a vital role in that process. Thank you for helping us grow and become better.\n\n"
                            + "Here’s what you shared with us:\n"
                            + "----------------------------------------\n"
                            + customerMessage + "\n"
                            + "----------------------------------------\n\n"
                            + "We look forward to serving you again soon!\n\n"
                            + "Warm regards,\n\n"
                            + "Mama's Restaurant Team\n";
                    break;
                }
                default:
                    subject = "";
                    body = "";
            }

            EmailSender.sendEmail(subject, body, event.getEmail());
        });
    }

    @Subscribe
    public void noOrderEvent(String msg)
    {
        Platform.runLater(() -> {
            if (msg.equals("No order!")) {
                checkLabel.setText("There is no order with this Order Number!");
            }
            else if (msg.equals("Not same restaurant!")) {
                checkLabel.setText("The order was not completed at the same restaurant!");
            }
        });
    }




}