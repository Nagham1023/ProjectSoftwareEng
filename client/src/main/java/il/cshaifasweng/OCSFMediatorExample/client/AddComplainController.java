package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import il.cshaifasweng.OCSFMediatorExample.entities.complainEvent;
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
            e.printStackTrace(); // In a real application, log this error or show an error message to the user
        }
    }

    @FXML
    private void ComplainButton(ActionEvent event) throws IOException {
        ComplainKind = "Complaint";
        ComplainButton.setStyle("-fx-background-color: #C76A58;");
        FeedbackButton.setStyle("-fx-background-color: #832018;");
        SuggestionButton.setStyle("-fx-background-color:  #832018;");
        textFieldOrderNum.setVisible(true);

    }
    @FXML
    private void FeedbackButton(ActionEvent event) throws IOException {
        ComplainKind = "Feedback";
        ComplainButton.setStyle("-fx-background-color: #832018;");
        FeedbackButton.setStyle("-fx-background-color: #C76A58;");
        SuggestionButton.setStyle("-fx-background-color:  #832018;");
        textFieldOrderNum.setVisible(false);

    }
    @FXML
    private void SuggestionButton(ActionEvent event) throws IOException {
        ComplainKind = "Suggestion";
        ComplainButton.setStyle("-fx-background-color: #832018;");
        FeedbackButton.setStyle("-fx-background-color: #832018;");
        SuggestionButton.setStyle("-fx-background-color:  #C76A58;");
        textFieldOrderNum.setVisible(false);

    }

    @FXML
    private void sendButton(ActionEvent event) throws IOException {

        if(ComplainKind==null||textFieldName.getText()==null||textFieldEmail.getText()==null||textAreaTellUs.getText()==null||date==null||nameValue==null||(orderNumValue==null && ComplainKind=="Complaint")){
            checkLabel.setText("There is at least one field empty!");
        }
        else {
            try {
                sendAndSaveComplain();
                checkLabel.setText("Sent Successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Subscribe
    private void sendAndSaveComplain() throws IOException {
        getRestaurantByName(restaurantList);
        LocalTime now = LocalTime.now(); // Get the current time
        time = Time.valueOf(now);        // Convert LocalTime to java.sql.Time
        getRestaurantByName(restaurantList);
        complainEvent complainEvent = new complainEvent(ComplainKind,textFieldName.getText(),textFieldEmail.getText(),textAreaTellUs.getText(),date,time,restaurant_chosen,"Do",response,textFieldOrderNum.getText(),refundVal);
        SimpleClient client;
        client = SimpleClient.getClient();
        client.sendToServer(complainEvent);
        checkLabel.setText("Sent Successfully");
    }
    @FXML
    private void backButton(ActionEvent event)throws IOException {
        App.setRoot("mainScreen");
    }

    @Subscribe
    public void fillComboBox(RestaurantList restaurantList) {
        branchesList.getItems().clear();
        System.out.println("here");
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        List<String> branches_List = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            branchesList.getItems().add(restaurant.getRestaurantName()); // Add each restaurant name
        }
    }

    @Subscribe
    public void getRestaurantByName(RestaurantList restaurantList) {

        nameValue=branchesList.getValue();
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getRestaurantName().equals(nameValue)) {
                restaurant_chosen = restaurant;
            }
        }

    }



}