package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class IPandPortController {

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

//    @FXML
//    void firstconnect(ActionEvent event) {
//
//    }

    @FXML
    private Button connect;

    @FXML
    private void firstconnect(ActionEvent event) {
        String ip = ipField.getText(); // Get the IP address
        String portText = portField.getText(); // Get the port number

        try {
            int port = Integer.parseInt(portText); // Convert port to integer
            // Set the IP and port for SimpleClient
            SimpleClient.IP = ip;
            SimpleClient.Port = port;
            SimpleClient client = SimpleClient.getClient(); // Get the SimpleClient instance

            client.openConnection(); // Attempt to connect to the server
            App.setRoot("Primary");


        } catch (NumberFormatException e) {
            // Handle invalid port input
            System.out.println("Invalid port number!");
        } catch (IOException e) {
            // Handle connection error
            e.printStackTrace();
        }
    }
}