package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class ForgetpassController {

    @FXML
    private Button Recoverbtn;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private ImageView imglogo;

    @FXML
    private TextField usernameField;

    @FXML
    void handleRecover(ActionEvent event) throws IOException {
        String email = emailField.getText();
        String username = usernameField.getText();

        if (email.isEmpty() || username.isEmpty()) {
            errorMessageLabel.setText("Please fill in all fields.");
            return;
        }
        //if (!isValidEmail(email)) {
        //    errorMessageLabel.setText("Please enter a valid email address.");
        //    return;
        //}
        //check if user is already available
        UserCheck userCheck = new UserCheck();
        userCheck.setEmail(email);
        userCheck.setUsername(username);
        userCheck.setState(2);
        SimpleClient client = SimpleClient.getClient();
        client.sendToServer(userCheck);

    }
    @Subscribe
    public void handleRecover(UserCheck response) {
        if(response.isState() == 2) {
            if (response.getRespond().equals("Valid")) {
                Platform.runLater(() -> {
                    try {
                        String subject = "Password for Mamas-Restaurant ";
                        String body = "Hi,\n\nWe've received a request to get your password. "
                                + "Your password is : " + response.getPassword() + "\n\n"
                                + "If you didn't request this, please ignore this email.\n\n"
                                + "Best regards,\nMamas-Restaurant Team";

                        // Call EmailSender to send the email
                        //EmailSender.sendEmail(response.getEmail(), subject, body);
                        EmailSender.sendEmail(subject,body,response.getEmail());
                        errorMessageLabel.setText("Your password sent to the E-mail");
                        //errorMessageLabel.setText("Your password is " + response.getPassword());
                        errorMessageLabel.setStyle("-fx-text-fill: green;");
                    } catch (Exception e) {
                        // Handle email sending failure
                        errorMessageLabel.setText("Failed to send email. Please try again later.");
                        errorMessageLabel.setStyle("-fx-text-fill: red;");
                        e.printStackTrace();
                    }
                });
            } else if (response.getRespond().equals("notValid")) {
                Platform.runLater(() -> {
                    errorMessageLabel.setStyle("-fx-text-fill: red;");
                    errorMessageLabel.setText("Incorrect email or username.");
                });
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }
    @FXML
    void toLogin(MouseEvent event) {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
        imglogo.setImage(new Image(getClass().getResourceAsStream("/images/Mom_Sticker.gif")));
    }

}
