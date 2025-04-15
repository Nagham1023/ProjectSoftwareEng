package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.complainEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updateResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;


public class AddResponseController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML
    private Label checkLabel;
    @FXML // fx:id="ClientNameLabel"
    private Label ClientNameLabel; // Value injected by FXMLLoader
    @FXML
    private TextArea tellLabel;
    @FXML // fx:id="responseField"
    private TextArea responseField; // Value injected by FXMLLoader
    @FXML // fx:id="refundField"
    private TextField refundField;
    @FXML // fx:id="sendResponse"
    private Button sendResponse; // Value injected by FXMLLoader
    private int idComplain;
    private String emailComplain;
    private String ordernumComplain = "";
    private String refundValue;
    private String kindValue;
    private String clientName;
    private String clientTell;
    private boolean isShowMode = false;

    @FXML private Label emailLabel;
    @FXML private Label complaintIdLabel;
    @FXML private Label restaurantLabel;
    @FXML private Label dateLabel;



    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert ClientNameLabel != null : "fx:id=\"ClientNameLabel\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert checkLabel != null : "fx:id=\"checkLabel\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert responseField != null : "fx:id=\"responseField\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert sendResponse != null : "fx:id=\"sendResponse\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert refundField != null : "fx:id=\"refundField\" was not injected: check your FXML file 'addResponse.fxml'.";
    }
    public void setShowMode(boolean isShow) {
        this.isShowMode = isShow;
        if (isShow) {
            sendResponse.setText("Close Response");
            responseField.setEditable(false);
            refundField.setEditable(false);
            sendResponse.setOnAction(e -> sendResponse.getScene().getWindow().hide()); // Close window
        }
        else {
            sendResponse.setText("Send Response");
            responseField.setEditable(true);
            refundField.setEditable(true);
            sendResponse.setOnAction(e->SendResponse());
        }
    }


    // Set complain details
    public void setCompDetails(String clientName, String clientTell, int idComplain, String kind, String emailComplain, String ordernum, String restaurantName, LocalDateTime date) {
        this.ClientNameLabel.setText("Response to: " + clientName);
        this.tellLabel.setText(clientTell);
        this.clientTell = clientTell;
        this.tellLabel.setEditable(false);
        this.idComplain = idComplain;
        this.emailComplain = emailComplain;
        this.ordernumComplain = ordernum;
        this.kindValue = kind;
        this.clientName = clientName;
        //@FXML private Label emailLabel;
        //    @FXML private Label complaintIdLabel;
        //    @FXML private Label restaurantLabel;
        //    @FXML private Label dateLabel;
        emailLabel.setText(emailComplain);
        complaintIdLabel.setText(String.valueOf(idComplain));
        restaurantLabel.setText(restaurantName);
        dateLabel.setText(date.toString());


        if (kindValue.equals("Complaint"))
            refundField.setVisible(true);
        else
            refundField.setVisible(false);
    }
    public void showResp(String clientName, String clientTell, String response,double refund,String kind,String email,int idC,String restaurantName,LocalDateTime date) {
        this.ClientNameLabel.setText("Response to: " + clientName);
        this.tellLabel.setText(clientTell);
        responseField.setText(response);
        emailLabel.setText(email);
        complaintIdLabel.setText(String.valueOf(idC));
        restaurantLabel.setText(restaurantName);
        dateLabel.setText(date.toString());
        if (kind.equals("Complaint")) {
            refundField.setVisible(true);
            refundField.setText(String.valueOf(refund));
        }
        else
            refundField.setVisible(false);
    }

    @FXML
    private void SendResponse() {
        if (responseField.getText() == null || responseField.getText().trim().isEmpty()) {
            checkLabel.setText("Enter A Response");
            return;
        }
        if (kindValue.equals("Complaint") && (refundField.getText() == null || refundField.getText().trim().isEmpty())) {
            checkLabel.setText("Enter A Refund Value");
            return;
        }
        String newResponse = responseField.getText();
        String subject = "Thanks For Contacting MAMA's Kitchen";
        refundValue = refundField.getText();
        double refund = 0;
        if (kindValue.equals("Complaint")) {
            try {
                refund = Double.parseDouble(refundValue);
            } catch (NumberFormatException e) {
                checkLabel.setText("Refund Value Must Be a Number");
                return;
            }
        }
        updateResponse uResponse = new updateResponse(newResponse, idComplain, emailComplain, ordernumComplain, refund);
        try {
            SimpleClient.getClient().sendToServer(uResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        generateResponse();
        checkLabel.setText("Sent Successfully");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            responseField.getScene().getWindow().hide(); // Close the window
        }));
        timeline.setCycleCount(1);
        timeline.play();

        // Notify main controller to refresh
        EventBus.getDefault().post(new complainEvent());
    }

    private void generateResponse() {
        String orderNum = ordernumComplain.trim();
        String clientName = this.clientName.trim();
        String clientSays = clientTell.trim();
        String complaintType = this.kindValue;
        String ourResponse = responseField.getText().trim();
        String refundAmount = refundField.getText().trim();

        if (orderNum.isEmpty() || clientName.isEmpty() || complaintType == null || ourResponse.isEmpty()) {
            showAlert("Error", "Please x in all required fields.");
            return;
        }

        // Generate subject
        String subject = switch (complaintType) {
            case "Suggestion" -> "Thank You for Your Suggestion, " + clientName;
            case "Complaint" -> "Response to Your Complaint - Order #" + orderNum;
            case "Feedback" -> "We Appreciate Your Feedback, " + clientName;
            default -> "Customer Service Response";
        };

        // Generate message
        StringBuilder message = new StringBuilder("Dear " + clientName + ",\n\n");
        message.append("Thank you for reaching out regarding your ").append(complaintType.toLowerCase()).append(".\n\n");
        message.append("Here is a summary of your inquiry:\n\n");
        message.append("**Order Number:** ").append(orderNum).append("\n");
        message.append("**Your Message:** ").append(clientSays).append("\n\n");

        message.append("**Our Response:**\n");
        message.append(ourResponse).append("\n\n");

        if (!refundAmount.isEmpty()) {
            message.append("✅ **Refund Issued:** We have processed a refund of ").append(refundAmount).append("₪ to your account.\n\n");
        }

        message.append("We value you as our customer and are here to assist with any further concerns.\n\n");
        message.append("Warm regards,\nMama's Restaurant Customer Service Team");


        EmailSender.sendEmail(subject, message.toString(), emailComplain);
        //checkLabel.setText("Response finished");
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}