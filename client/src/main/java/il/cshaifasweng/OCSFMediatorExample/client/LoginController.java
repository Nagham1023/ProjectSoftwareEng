package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

public class LoginController {


    @FXML
    private Label respondField;
    @FXML
    private ImageView accountimg;

    @FXML
    private ImageView imglogo;

    @FXML
    private ImageView lockimg;


    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordField2;
    private String hiddenPassword = "";

    @FXML
    private TextField usernameField;
    @FXML
    private ImageView passimg;
    int passImgState = 0;

    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
        imglogo.setImage(new Image(getClass().getResourceAsStream("/images/Mom_Sticker.gif")));
        accountimg.setImage(new Image(getClass().getResourceAsStream("/images/account_circle.png")));
        lockimg.setImage(new Image(getClass().getResourceAsStream("/images/Black_Lock.png")));
        passimg.setImage(new Image(getClass().getResourceAsStream("/images/show_password.png")));
        passwordField2.setVisible(false);

    }
    @FXML
    void passShowClick() {
        if(passImgState == 0){
            passwordField2.setVisible(true);
            passwordField.setVisible(false);
            hiddenPassword = passwordField.getText();
            passwordField2.setText(hiddenPassword);
            passImgState = 1;
            passimg.setImage(new Image(getClass().getResourceAsStream("/images/hide_password.png")));
        }
        else if(passImgState == 1){
            passwordField2.setVisible(false);
            passwordField.setVisible(true);
            hiddenPassword = passwordField2.getText();
            passwordField.setText(hiddenPassword);
            passImgState = 0;
            passimg.setImage(new Image(getClass().getResourceAsStream("/images/show_password.png")));
        }
    }
    @FXML
    void LoginButton() throws IOException {

        if(passImgState == 0)
            hiddenPassword = passwordField.getText();
        else hiddenPassword = passwordField2.getText();
        if(usernameField.getText().isEmpty() || hiddenPassword.isEmpty()){
            respondField.setText("Please enter your username and password");
            return;
        }
        UserCheck user = new UserCheck(usernameField.getText(), hiddenPassword,1);
        SimpleClient client = SimpleClient.getClient();
        client.sendToServer(user);

    }
    @Subscribe
    public void LoginResponse(UserCheck response) {
        if(Objects.equals(response.getRespond(), "Valid") && response.isState() == 1) {
            Platform.runLater(() -> {
                try {
                    SimpleClient.setUser(response);
                    App.setRoot("worker_screen");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        else if(response.isState() == 1)
        {
            Platform.runLater(() -> {
                respondField.setText(response.getRespond());
                    try {
                        App.setRoot("worker_screen");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
            });
        }
    }
    @FXML
    void RegisterButton() {
        try {
            App.setRoot("register");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void toForgetpass() {
        try {
            App.setRoot("forgetpass");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}