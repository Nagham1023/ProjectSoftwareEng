/**
 * Sample Skeleton for 'addResponse.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.updateResponse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddResponseController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="ClientNameLabel"
    private Label ClientNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="responseField"
    private TextField responseField; // Value injected by FXMLLoader

    @FXML // fx:id="sendResponse"
    private Button sendResponse; // Value injected by FXMLLoader

    private String idComplain;




    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert ClientNameLabel != null : "fx:id=\"ClientNameLabel\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert responseField != null : "fx:id=\"responseField\" was not injected: check your FXML file 'addResponse.fxml'.";
        assert sendResponse != null : "fx:id=\"sendResponse\" was not injected: check your FXML file 'addResponse.fxml'.";

    }

    // Set complain details
    public void setCompDetails(String clientName,String idComplain) {
        this.ClientNameLabel.setText("Response to: " + clientName);
        this.idComplain = idComplain;
    }

    @FXML
    private void SendResponse() {
        String newResponse = responseField.getText();
        if (newResponse != null && !newResponse.trim().isEmpty()) {
            //System.out.println("im in to change price");
            SimpleClient client;
            client = SimpleClient.getClient();
            updateResponse uResponse = new updateResponse(newResponse,idComplain);
            try {
                client.sendToServer(uResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //priceLabel.setText(newPrice + "₪");
            responseField.getScene().getWindow().hide(); // Close the window
        }
    }

}
