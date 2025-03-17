/**
 * Sample Skeleton for 'addResponse.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.updateResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

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
    private TextField tellLabel;

    @FXML // fx:id="responseField"
    private TextField responseField; // Value injected by FXMLLoader

    @FXML // fx:id="refundField"
    private TextField refundField;

    @FXML // fx:id="sendResponse"
    private Button sendResponse; // Value injected by FXMLLoader

    private int idComplain;
    private String emailComplain;
    private String ordernumComplain = "";
    private String refundValue;
    private String kindValue;


    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert ClientNameLabel != null : "fx:id=\"ClientNameLabel\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert checkLabel != null : "fx:id=\"checkLabel\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert responseField != null : "fx:id=\"responseField\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert sendResponse != null : "fx:id=\"sendResponse\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert refundField != null : "fx:id=\"refundField\" was not injected: check your FXML file 'addResponse.fxml'.";
    }

    // Set complain details
    public void setCompDetails(String clientName, String clientTell, int idComplain, String kind, String emailComplain, String ordernum) {
        this.ClientNameLabel.setText("Response to: " + clientName);
        this.tellLabel.setText("The Client Says: " + clientTell);
        this.tellLabel.setEditable(false);
        this.idComplain = idComplain;
        this.emailComplain = emailComplain;
        this.ordernumComplain = ordernum;
        this.kindValue = kind;

        if (kindValue.equals("Complaint"))
            refundField.setVisible(true);
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
        if (!newResponse.trim().isEmpty()) {
            SimpleClient client = SimpleClient.getClient();
            updateResponse uResponse = new updateResponse(newResponse, idComplain, emailComplain, ordernumComplain, refund);
            try {
                System.out.println("Sending updateResponse to server: " + uResponse.getnewResponse());
                client.sendToServer(uResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            checkLabel.setText("Sent Successfully");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
                responseField.getScene().getWindow().hide(); // Close the window
            }));
            timeline.setCycleCount(1); // להריץ פעם אחת בלבד
            timeline.play();
        }
    }
}
