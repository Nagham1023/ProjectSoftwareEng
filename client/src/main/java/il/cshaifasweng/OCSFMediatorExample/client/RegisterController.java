package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public class RegisterController {


    @FXML
    private TextField ageField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView imglogo;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;


    @FXML
    private ComboBox<String> genderComboBox;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @FXML
    void RegisterButton(ActionEvent event) throws IOException {
        if(usernameField.getText().isEmpty() || emailField.getText().isEmpty() || passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty() || ageField.getText().isEmpty()) {
            errorMessageLabel.setText("Please fill all the fields");
            return;
        }
        else if(genderComboBox.getValue() == null) {
            errorMessageLabel.setText("Please fill the gender field");
            return;
        }
        else if(!passwordField.getText().equals(confirmPasswordField.getText())) {
            errorMessageLabel.setText("Passwords do not match!");
            return;
        }
        if(passwordField.getText().equals(confirmPasswordField.getText()))
        {
            if(passwordField.getText().length() > 15)
            {
                errorMessageLabel.setText("The password is too long! Use less than 15 characters");
                return;
            }
        }
        if (!isValidEmail(emailField.getText())) {
            errorMessageLabel.setText("Please enter a valid email address.");
            return;
        }
        if(!isValidAge(ageField.getText())) {
            errorMessageLabel.setText("Type a correct age!");
            return;
        }

        UserCheck userCheck = new UserCheck(usernameField.getText(), 3);//check if the name can be used .
        SimpleClient client = SimpleClient.getClient();
        client.sendToServer(userCheck);
    }
    private boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    private boolean isValidAge(String ageText) {
        try {
            int age = Integer.parseInt(ageText);

            return age > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
        imglogo.setImage(new Image(getClass().getResourceAsStream("/images/Mom_Sticker.gif")));
        genderComboBox.getItems().addAll("Male", "Female", "Other");
    }
    @FXML
    void toLogin() {
        try {
            App.setRoot("login");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Subscribe
    public void RegisterRespond(UserCheck response) throws IOException {
        String resp = response.getRespond();
        if(response.isState() == 0) {
            Platform.runLater(() -> {
                errorMessageLabel.setText(resp);
            });
            if (Objects.equals(resp, "Registration completed successfully"))
                Platform.runLater(() -> {
                    errorMessageLabel.setStyle("-fx-text-fill: green;");
                });
            else
                Platform.runLater(() -> {
                    errorMessageLabel.setStyle("-fx-text-fill: red;");
                });
        }
        else if (response.isState() == 3) {
            if (response.getRespond().equals("Valid"))
            {
                Platform.runLater(() -> {
                    errorMessageLabel.setText("");
                    FXMLLoader loader = new FXMLLoader(App.class.getResource("validate_reg.fxml"));
                    Parent root = null;
                    try {
                        root = loader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    Random random = new Random();
                    int ValidationCode = 100000 + random.nextInt(900000);

                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String gender = genderComboBox.getValue();
                    String email = emailField.getText();
                    int age = Integer.parseInt(ageField.getText());


                    ValidationPageController validationPageController = loader.getController();
                    validationPageController.setUserInfo(ValidationCode, username, email, password, gender, age);

                    Stage stage = new Stage();
                    stage.setTitle("Validate Your Account");
                    stage.setScene(new Scene(root));
                    stage.show();
                    String title = "Account Validation Code Mama-Resturant";
                    String body = "Hello " + usernameField.getText() + ",\n\n"
                            + "Thank you for registering with us!\n\n"
                            + "Your account validation code is: " + ValidationCode + "\n\n"
                            + "Please enter this code on the validation page to complete your registration.\n\n"
                            + "If you did not request this, please ignore this email.\n\n"
                            + "Best regards,\n"
                            + "The Support Team";
                    EmailSender.sendEmail(title, body, emailField.getText());
                });
            }
            else {
                Platform.runLater(() -> {
                    errorMessageLabel.setStyle("-fx-text-fill: red;");
                    errorMessageLabel.setText("This username is already used!");
                });
            }
        }
    }



}
