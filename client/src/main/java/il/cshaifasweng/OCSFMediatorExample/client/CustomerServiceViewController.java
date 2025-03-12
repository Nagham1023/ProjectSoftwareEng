/**
 * Sample Skeleton for 'customerServiceView.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class CustomerServiceViewController {

    public static String statusVal = "";
    public static String kindVal = "";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="checkLabel"
    private Label checkLabel; // Value injected by FXMLLoader

    @FXML // fx:id="compliantButton"
    private Button compliantButton; // Value injected by FXMLLoader

    @FXML // fx:id="doButton"
    private Button doButton; // Value injected by FXMLLoader

    @FXML // fx:id="doneButton"
    private Button doneButton; // Value injected by FXMLLoader

    @FXML // fx:id="feedbackButton"
    private Button feedbackButton; // Value injected by FXMLLoader

    @FXML // fx:id="generateButton"
    private Button generateButton; // Value injected by FXMLLoader

    @FXML // fx:id="suggestionButton"
    private Button suggestionButton; // Value injected by FXMLLoader



    @FXML
    void DoButton(ActionEvent event) {

        statusVal = " Do";
        doButton.setStyle("-fx-background-color: #C76A58;");
        doneButton.setStyle("-fx-background-color: #832018;");
    }

    @FXML
    void DoneButton(ActionEvent event) {

        statusVal = " Done";
        doButton.setStyle("-fx-background-color: #832018;");
        doneButton.setStyle("-fx-background-color: #C76A58;");

    }

    @FXML
    void CompliantButton(ActionEvent event) {

        kindVal = "Complaint";
        compliantButton.setStyle("-fx-background-color: #C76A58;");
        feedbackButton.setStyle("-fx-background-color: #832018;");
        suggestionButton.setStyle("-fx-background-color: #832018;");

    }

    @FXML
    void FeedbackButton(ActionEvent event) {

        kindVal = " Feedback";
        compliantButton.setStyle("-fx-background-color: #832018;");
        feedbackButton.setStyle("-fx-background-color: #C76A58;");
        suggestionButton.setStyle("-fx-background-color: #832018;");

    }

    @FXML
    void SuggestionButton(ActionEvent event) {

        kindVal = " Suggestion";
        compliantButton.setStyle("-fx-background-color: #832018;");
        feedbackButton.setStyle("-fx-background-color: #832018;");
        suggestionButton.setStyle("-fx-background-color: #C76A58;");

    }

    @FXML
    void GenerateButton(ActionEvent event) {
        if(statusVal==null||kindVal==null)
            checkLabel.setText("Need To Choose Kind And Status!");
        else
            checkLabel.setText("Genelating");

        if(kindVal=="Complaint" && statusVal=="Do"){
            // to do need to generate!!
        }

        if(kindVal=="Complaint" && statusVal=="Done"){
            // to do need to generate!!
        }

        if(kindVal=="Feedback" && statusVal=="Do"){
            // to do need to generate!!
        }

        if(kindVal=="Feedback" && statusVal=="Done"){
            // to do need to generate!!
        }

        if(kindVal=="Suggesting" && statusVal=="Do"){
            // to do need to generate!!
        }

        if(kindVal=="Suggesting" && statusVal=="Done"){
            // to do need to generate!!
        }





    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert checkLabel != null : "fx:id=\"checkLabel\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert compliantButton != null : "fx:id=\"compliantButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert doButton != null : "fx:id=\"doButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert feedbackButton != null : "fx:id=\"feedbackButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert generateButton != null : "fx:id=\"generateButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert suggestionButton != null : "fx:id=\"suggestionButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";

    }

}
