package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.client.events.UsersListEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.UserManagement;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class RegisterController {

    private Map<String, Label> userDescribitionLabels = new HashMap<>();
    private Map<String, HBox> userrowMap = new HashMap<>();
    private List<Users> usersList = new ArrayList<>();
    private Users currentEditingUser; // Track the user being edited
    private HBox updateCancelButtons; // Reference to dynamically added buttons

    @FXML
    private VBox dynamicUserNames;

    @FXML
    private TextField ageField;

    private Timeline timeline; // Animation refresh loop

//    @FXML
//    private PasswordField confirmPasswordField;
    @FXML
    private ImageView loadinggif;

    @FXML
    private TextField emailField;

    @FXML
    private Label errorMessageLabel;
//    @FXML
//    private ImageView imglogo;

    @FXML
    private PasswordField passwordField;


    @FXML
    private AnchorPane anchorpane;

    @FXML
    private TextField usernameField;

    private Stage loadingStage;

    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private ComboBox<String> restaurantsComboBox;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button registerButton;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    @FXML
    void RegisterButton(ActionEvent event) {
        if(usernameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty()  ||
                ageField.getText().isEmpty()) {
            errorMessageLabel.setText("Please fill all the fields");
            return;
        }

        if (genderComboBox.getValue() == null) {
            errorMessageLabel.setText("Please fill the gender field");
            return;
        }

        if (passwordField.getText().length() > 15) {
            errorMessageLabel.setText("The password is too long! Use less than 15 characters");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            errorMessageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (!isValidAge(ageField.getText())) {
            errorMessageLabel.setText("Type a correct age!");
            return;
        }
        if (!isValidComboBox()) {
            errorMessageLabel.setText("If the role is not \"CustomerService\", \"Dietation\", or \"CompanyManager\",  restaurant is needed");
            return;
        }


        UserCheck userCheck = new UserCheck(usernameField.getText(), 3);
        SimpleClient client = SimpleClient.getClient();
        try {
            client.sendToServer(userCheck);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    private boolean isValidComboBox() {
        String selectedRole = roleComboBox.getValue();
        String selectedRestaurant = restaurantsComboBox.getValue();

        // If the role is "CustomerService", "Dietation", or "CompanyManager", no restaurant is needed
        if (selectedRole.equals("CustomerService") || selectedRole.equals("Dietation") || selectedRole.equals("CompanyManager")) {
            return true;
        }

        // If the role is something else, the restaurant must be selected
        return selectedRestaurant != null;
    }


    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
        SimpleClient client = SimpleClient.getClient();
        try {
            SimpleClient.getClient().sendToServer("Fetching SearchBy Options");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        roleComboBox.getItems().addAll("Host","CustomerService","Dietation","CompanyManager","ChainManager");
        // Set up listener for roleComboBox
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            if ("CustomerService".equals(newValue)
                    || "Dietation".equals(newValue)
                    || "CompanyManager".equals(newValue)) {
                restaurantsComboBox.setVisible(false);
            } else {
                restaurantsComboBox.setVisible(true);
            }
        });
        genderComboBox.getSelectionModel().selectFirst(); // Add default selection
        roleComboBox.getSelectionModel().selectFirst();   // Add default selection
        try {
            SimpleClient.getClient().sendToServer("Get all users");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Initially hide restaurantsComboBox
        restaurantsComboBox.setVisible(false);

    }

    @Subscribe
    public void putSearchOptions(SearchOptions options) {
        // Handle the event sent from the server
        List<String> restaurantNames = options.getRestaurantNames();
        Platform.runLater(() -> {
            // UI-related code here
            restaurantsComboBox.getItems().clear();
            // Add items to ComboBoxes
            for (String restaurant : restaurantNames) {
                restaurantsComboBox.getItems().add(restaurant);
            }
        });
    }
//    @FXML
//    void toLogin() {
//            Platform.runLater(() -> {
//                try {
//                    App.setRoot("login");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//    }
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
        else if (response.isState() == 3 && response.getRespond().equals("Valid")) {

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
                    String role;
                    if(roleComboBox.getValue().equals("CustomerService") || roleComboBox.getValue().equals("Dietation") || roleComboBox.getValue().equals("CompanyManager"))
                        role = roleComboBox.getValue();
                    else role= roleComboBox.getValue() +" "+ restaurantsComboBox.getValue();


                    ValidationPageController validationPageController = loader.getController();
                    validationPageController.setUserInfo(ValidationCode, username, email, password, gender, age,role);

                    Stage stage = new Stage();
                    stage.setTitle("Validate Your Account");
                    stage.setScene(new Scene(root));
                    stage.show();
                    String title = "Account Validation Code Mama-Restaurant";
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
        }else if (response.isState() == 8 && response.getRespond().equals("Valid")) {

            if (response.getRespond().equals("Valid")) {
                Platform.runLater(() -> {
                    errorMessageLabel.setText("");
                    //Create updated user object
                    UserCheck updatedUser = new UserCheck();
                    UserCheck uc = new UserCheck();
                    uc.setState(9);
                    uc.setUsername(usernameField.getText());
                    updatedUser.setFirstName(currentEditingUser.getUsername());
                    updatedUser.setUsername(usernameField.getText());
                    updatedUser.setEmail(emailField.getText());
                    updatedUser.setAge(Integer.parseInt(ageField.getText()));
                    updatedUser.setGender(genderComboBox.getValue());

                    // Handle role + restaurant
                    String role = roleComboBox.getValue();
                    if (!role.equals("CustomerService") && !role.equals("Dietation") && !role.equals("CompanyManager")) {
                        role += " " + restaurantsComboBox.getValue();
                    }
                    updatedUser.setRole(role);
                    //updatedUser.setMethod("update");

                    // Send to server
                    try {
                        SimpleClient.getClient().sendToServer(updatedUser);
                    } catch (IOException e) {
                        errorMessageLabel.setText("Error sending update request");
                    }

                    toggleUpdateMode(false); // Return to normal mode
                    String title = "Account Update message Mama-Restaurant";
                    String body = "Hello " + usernameField.getText() + ",\n\n"
                            + "Your Details have been updated\n\n"
                            + "If you did not request this, please ignore this email.\n\n"
                            + "Best regards,\n"
                            + "The Support Team";
                    EmailSender.sendEmail(title, body, emailField.getText());
                });
            }
        }
        else {
            Platform.runLater(() -> {
                errorMessageLabel.setStyle("-fx-text-fill: red;");
                errorMessageLabel.setText("This username is already used!");
                });
        }
    }

    public void onAddUserClicked(Users user) {
        // Create a new user row (HBox)
        HBox userRow = new HBox(20);
        userRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden Label to store meal ID
        Label idLabel = new Label(String.valueOf(user.getId()));
        idLabel.setVisible(false); // Make it invisible
        idLabel.setManaged(false); // Ensure it doesn't take layout space
        Label passwordLabel = new Label(String.valueOf(user.getPassword()));
        passwordLabel.setVisible(false); // Make it invisible
        passwordLabel.setManaged(false); // Ensure it doesn't take layout space


        // user Details
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(user.getUsername());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label descriptionLabel = new Label();
        String descriptionText = String.format(
                "User Details:\n- Role: %s\n- Gender: %s\n- Email: %s",
                user.getRole(),
                user.getGender(),
                user.getEmail()
        );
        System.out.println(descriptionText);
        descriptionLabel.setText(descriptionText);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        detailsBox.getChildren().addAll(nameLabel, descriptionLabel);



            Button DeleteButton = new Button("Delete");
            DeleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: #ffffff; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
//            DeleteButton.setOnAction(event -> handleDeleteUserClicked(idLabel.getText()));
            DeleteButton.setUserData(user.getUsername()); // Attach username to the button
            DeleteButton.setOnAction(event -> handleDeleteUserClicked(DeleteButton.getUserData().toString()));

            Button UpdteButton = new Button("Update");
            UpdteButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: #ffffff; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
            UpdteButton.setOnAction(event -> handleUpdateUserClicked(nameLabel.getText(), idLabel.getText()));
            // Add components to mealRow
            userRow.getChildren().addAll(detailsBox, DeleteButton, UpdteButton);


        // Add UserRow to dynamicUserNames
        dynamicUserNames.getChildren().add(userRow);
        userDescribitionLabels.put(String.valueOf(user.getUsername()), descriptionLabel);
        userrowMap.put(String.valueOf(user.getUsername()), userRow);
    }


    private void handleUpdateUserClicked(String username, String userId) {
        // Find the user in the list by username
        currentEditingUser = usersList.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (currentEditingUser != null) {
            // Populate fields
            Platform.runLater(() -> {
                usernameField.setText(currentEditingUser.getUsername());
                emailField.setText(currentEditingUser.getEmail());
                ageField.setText(String.valueOf(currentEditingUser.getAge()));
                genderComboBox.setValue(currentEditingUser.getGender());

                // Handle role + restaurant (if applicable)
                String role = currentEditingUser.getRole();
                if (role.startsWith("Host") || role.startsWith("ChainManager")) {
                    String[] parts = role.split(" ", 2);
                    roleComboBox.setValue(parts[0]);
                    restaurantsComboBox.setValue(parts[1]);
                    restaurantsComboBox.setVisible(true);
                } else {
                    roleComboBox.setValue(role);
                    restaurantsComboBox.setVisible(false);
                }

                // Replace "Add Worker" with "Update" and "Cancel"
                toggleUpdateMode(true);
            });
        }
    }
    private void toggleUpdateMode(boolean isUpdateMode) {
        registerButton.setVisible(!isUpdateMode);

        if (isUpdateMode) {
            // Create Update and Cancel buttons
            Button updateBtn = new Button("Update");
            updateBtn.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
            updateBtn.setOnAction(e -> handleUpdateInDatabase());

            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white;");
            cancelBtn.setOnAction(e -> toggleUpdateMode(false));

            updateCancelButtons = new HBox(10, updateBtn, cancelBtn);
            AnchorPane.setLeftAnchor(updateCancelButtons, 150.0);
            AnchorPane.setTopAnchor(updateCancelButtons, 490.0);
            anchorpane.getChildren().add(updateCancelButtons);
        } else {
            // Clear fields and remove buttons
            clearFields();
            if (updateCancelButtons != null) {
                anchorpane.getChildren().remove(updateCancelButtons);
            }
        }
    }

    private void handleUpdateInDatabase() {
        if(usernameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                passwordField.getText().isEmpty()  ||
                ageField.getText().isEmpty()) {
            errorMessageLabel.setText("Please fill all the fields");
            return;
        }

        if (genderComboBox.getValue() == null) {
            errorMessageLabel.setText("Please fill the gender field");
            return;
        }

        if (passwordField.getText().length() > 15) {
            errorMessageLabel.setText("The password is too long! Use less than 15 characters");
            return;
        }

        if (!isValidEmail(emailField.getText())) {
            errorMessageLabel.setText("Please enter a valid email address.");
            return;
        }

        if (!isValidAge(ageField.getText())) {
            errorMessageLabel.setText("Type a correct age!");
            return;
        }
        if (!isValidComboBox()) {
            errorMessageLabel.setText("If the role is not \"CustomerService\", \"Dietation\", or \"CompanyManager\",  restaurant is needed");
            return;
        }


        if(!currentEditingUser.getUsername().equals(usernameField.getText())) {
            UserCheck userCheck = new UserCheck(usernameField.getText(), 8);
            SimpleClient client = SimpleClient.getClient();
            try {
                client.sendToServer(userCheck);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Platform.runLater(() -> {
                errorMessageLabel.setText("");
                // Create updated user object
                UserCheck updatedUser = new UserCheck();
                updatedUser.setState(9);
                updatedUser.setUsername(usernameField.getText());
                updatedUser.setFirstName(usernameField.getText());
                updatedUser.setEmail(emailField.getText());
                updatedUser.setAge(Integer.parseInt(ageField.getText()));
                updatedUser.setGender(genderComboBox.getValue());

                // Handle role + restaurant
                String role = roleComboBox.getValue();
                if (!role.equals("CustomerService") && !role.equals("Dietation") && !role.equals("CompanyManager")) {
                    role += " " + restaurantsComboBox.getValue();
                }
                updatedUser.setRole(role);

                // Send to server
                try {
                    SimpleClient.getClient().sendToServer(updatedUser);
                } catch (IOException e) {
                    errorMessageLabel.setText("Error sending update request");
                }

                toggleUpdateMode(false); // Return to normal mode
                String title = "Account Update message Mama-Restaurant";
                String body = "Hello " + usernameField.getText() + ",\n\n"
                        + "Your Details have been updated\n\n"
                        + "If you did not request this, please ignore this email.\n\n"
                        + "Best regards,\n"
                        + "The Support Team";
                EmailSender.sendEmail(title, body, emailField.getText());
            });
        }
    }

    private void clearFields() {
        usernameField.clear();
        emailField.clear();
        ageField.clear();
        //genderComboBox.getSelectionModel().clearSelection();
        //roleComboBox.getSelectionModel().clearSelection();
        restaurantsComboBox.getSelectionModel().clearSelection();
    }

    private void handleDeleteUserClicked(String username) {
        SimpleClient client;
        client = SimpleClient.getClient();
        UserManagement us = new UserManagement(username, "Delete"); // Pass username
        try {
            client.sendToServer(us);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("send request to server to delete meal");
    }

    //Fill the Vbox to present the users
    @Subscribe
    public void getAllUsers(UsersListEvent event) {
        usersList = event.getUsers();
        System.out.println("usersList: " + usersList);
        Platform.runLater(() -> {
            if (!dynamicUserNames.getChildren().isEmpty()) {
                dynamicUserNames.getChildren().clear();
            }

            userrowMap.clear();

            if (usersList != null) {
                // Add new meals after the header
                usersList.forEach(user ->
                        onAddUserClicked(user)
                );
            }
        });
    }

    @Subscribe
    public void addnewUser(String event) {
        if (event.equals("Registration completed successfully")){
            try{
                SimpleClient.getClient().sendToServer("Get all users");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
    @Subscribe
    public void updatePage(UserManagement event) {
        //if (event.equals("Registration completed successfully")){
            try{
                SimpleClient.getClient().sendToServer("Get all users");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        //}

    }

}
