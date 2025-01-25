package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ValidationPageController {

    @FXML
    private TextField validationCodeField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button validateButton;

    private int validationCode;
    private String name;
    private String email;
    private String password;
    private String gender;
    private int age;

    public void setUserInfo(int validationCode,String name,String email,String password,String gender,int age) {
        this.validationCode = validationCode;
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.age = age;
    }


    @FXML
    private void handleValidation() throws IOException {
        String enteredCode = validationCodeField.getText();

        if (String.valueOf(validationCode).equals(enteredCode)) {
            statusLabel.setText("Validation successful!");
            statusLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            UserCheck userCheck = new UserCheck(name,password,email,age,gender,0);
            SimpleClient client = SimpleClient.getClient();
            client.sendToServer(userCheck);
            Stage stage = (Stage) validateButton.getScene().getWindow();
            stage.close();
        } else {
            statusLabel.setText("Invalid code. Please try again.");
            statusLabel.setTextFill(javafx.scene.paint.Color.RED);
        }
    }
}

