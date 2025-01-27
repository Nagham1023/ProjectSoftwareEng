package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ResourceBundle;
import il.cshaifasweng.OCSFMediatorExample.entities.Resturant;
import il.cshaifasweng.OCSFMediatorExample.entities.complainEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static il.cshaifasweng.OCSFMediatorExample.client.AddMealController.imageToByteArray;

public class AddComplainController {
    String ComplainKind=null;
    String name;
    String email;
    String tell;
    Date date;
    Time time;
    Resturant restaurant;

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
    private ChoiceBox<?> branchesList;

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
    private TextField textFieldName;

    @FXML
    void initialize() {
        assert ComplainButton != null : "fx:id=\"ComplainButton\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert FeedbackButton != null : "fx:id=\"FeedbackButton\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert SuggestionButton != null : "fx:id=\"SuggestionButton\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert backButton != null : "fx:id=\"backButton\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert branchesList != null : "fx:id=\"branchesList\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert checkLabel != null : "fx:id=\"checkLabel\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert datePicker != null : "fx:id=\"datePicker\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert logoImage != null : "fx:id=\"logoImage\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert sendButton != null : "fx:id=\"sendButton\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert textAreaTellUs != null : "fx:id=\"textAreaTellUs\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert textFieldEmail != null : "fx:id=\"textFieldEmail\" was not injected: check your FXML file 'addcomplain.fxml'.";
        assert textFieldName != null : "fx:id=\"textFieldName\" was not injected: check your FXML file 'addcomplain.fxml'.";
    }


    @FXML
    void ComplainButton(ActionEvent event) {
        ComplainKind = "Complaint";
        FeedbackButton.setDisable(true);
        SuggestionButton.setDisable(true);
    }
    @FXML
    void FeedbackButton(ActionEvent event) {
        ComplainKind = "Feedback";
        ComplainButton.setDisable(true);
        SuggestionButton.setDisable(true);
    }
    @FXML
    void SuggestionButton(ActionEvent event) {
        ComplainKind = "Suggestion";
        ComplainButton.setDisable(true);
        FeedbackButton.setDisable(true);
    }

    @FXML
    void sendButton(ActionEvent event) {
        if(ComplainKind==null||textFieldName.getText()==null||textFieldEmail.getText()==null||textAreaTellUs.getText()==null||date==null){
            checkLabel.setText("There is at least one field empty!");
        }
        else {
            checkLabel.setText("Sent Successfully");
            try {
                sendAndSaveComplain();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML
    public void sendAndSaveComplain() throws IOException {

        LocalTime now = LocalTime.now(); // Get the current time
        time = Time.valueOf(now);        // Convert LocalTime to java.sql.Time

        complainEvent complainEvent = new complainEvent(ComplainKind,textFieldName.getText(),textFieldEmail.getText(),textAreaTellUs.getText(),date,time,restaurant);

        SimpleClient client;
        client = SimpleClient.getClient();
        client.sendToServer(complainEvent);
    }
    @FXML
    void backButton(ActionEvent event) {
        // switch to main screen  App.setRoot("mainScreen");
    }
}







