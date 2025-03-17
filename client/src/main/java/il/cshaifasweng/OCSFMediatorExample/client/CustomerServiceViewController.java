/**
 * Sample Skeleton for 'customerServiceView.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CustomerServiceViewController {

    public static String statusVal = null;
    public static String kindVal = null;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private VBox ListCompContainer;
    private Map<String, Button> addResponseButtons = new HashMap<>();
    private ComplainList complainList = new ComplainList();
    private ListComplainList listComplainList;


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

    private List<Complain> list1= new ArrayList<>();
    private List<Complain> list2= new ArrayList<>();
    private List<Complain> list3= new ArrayList<>();
    private List<Complain> list4= new ArrayList<>();
    private List<Complain> list5= new ArrayList<>();
    private List<Complain> list6= new ArrayList<>();


    @FXML
    void DoButton(ActionEvent event) {
        statusVal = "Do";
        doButton.setStyle("-fx-background-color: #C76A58;");
        doneButton.setStyle("-fx-background-color: #832018;");
    }

    @FXML
    void DoneButton(ActionEvent event) {
        statusVal = "Done";
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
        kindVal = "Feedback";
        compliantButton.setStyle("-fx-background-color: #832018;");
        feedbackButton.setStyle("-fx-background-color: #C76A58;");
        suggestionButton.setStyle("-fx-background-color: #832018;");
    }

    @FXML
    void SuggestionButton(ActionEvent event) {
        kindVal = "Suggestion";
        compliantButton.setStyle("-fx-background-color: #832018;");
        feedbackButton.setStyle("-fx-background-color: #832018;");
        suggestionButton.setStyle("-fx-background-color: #C76A58;");
    }
    @FXML
    void GenerateButton(ActionEvent event) throws IOException {
        if(statusVal==null||kindVal==null)
            checkLabel.setText("Need To Choose Kind And Status!");
        else{
            checkLabel.setText("Genelating..");
        if("Complaint".equals(kindVal) && "Do".equals(statusVal)){
           GetTheComplains(list1);
        }
        if("Complaint".equals(kindVal) && "Done".equals(statusVal)){
            GetTheComplains(list2);
        }
       if("Feedback".equals(kindVal) && "Do".equals(statusVal)){
           GetTheComplains(list3);
        }
        if("Feedback".equals(kindVal) && "Done".equals(statusVal)){
            GetTheComplains(list4);
        }
        if("Suggestion".equals(kindVal) && "Do".equals(statusVal)){
            GetTheComplains(list5);
        }
        if("Suggestion".equals(kindVal) && "Done".equals(statusVal)){
            GetTheComplains(list6);
        }}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        System.out.println("EventBus registered successfully.");

        SimpleClient client;
        client = SimpleClient.getClient();
        client.sendToServer("getAllComplaints6");

        assert checkLabel != null : "fx:id=\"checkLabel\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert compliantButton != null : "fx:id=\"compliantButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert doButton != null : "fx:id=\"doButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert doneButton != null : "fx:id=\"doneButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert feedbackButton != null : "fx:id=\"feedbackButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert generateButton != null : "fx:id=\"generateButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";
        assert suggestionButton != null : "fx:id=\"suggestionButton\" was not injected: check your FXML file 'customerServiceView.fxml'.";

    }
    public void restartThePage()  {

        EventBus.getDefault().register(this);
        System.out.println("EventBus registered successfully.");

        SimpleClient client;
        client = SimpleClient.getClient();
        try {
            client.sendToServer("getAllComplaints6");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(statusVal==null||kindVal==null)
            checkLabel.setText("Need To Choose Kind And Status!");
        else{
            checkLabel.setText("Genelating..");
            if("Complaint".equals(kindVal) && "Do".equals(statusVal)){
                GetTheComplains(list1);
            }
            if("Complaint".equals(kindVal) && "Done".equals(statusVal)){
                GetTheComplains(list2);
            }
            if("Feedback".equals(kindVal) && "Do".equals(statusVal)){
                GetTheComplains(list3);
            }
            if("Feedback".equals(kindVal) && "Done".equals(statusVal)){
                GetTheComplains(list4);
            }
            if("Suggestion".equals(kindVal) && "Do".equals(statusVal)){
                GetTheComplains(list5);
            }
            if("Suggestion".equals(kindVal) && "Done".equals(statusVal)){
                GetTheComplains(list6);
            }}
    }

    @Subscribe
    public void handleComplainListEvent(ListComplainList event) {
        this.listComplainList = event;
        event.tooString();
        list1 = listComplainList.getComplaincd();
        list2 = listComplainList.getComplaincn();
        list3 = listComplainList.getComplainfd();
        list4 = listComplainList.getComplainfn();
        list5 = listComplainList.getComplainsd();
        list6 = listComplainList.getComplainsn();
    }

    public void GetTheComplains(List<Complain> compList) {

        Platform.runLater(() -> {
            ListCompContainer.getChildren().clear();
            ListCompContainer.getChildren().removeIf(node -> {
                return node instanceof HBox && node != ListCompContainer.getChildren().get(0);
            });

            // Add new meals to the menu
            if (compList != null && !compList.isEmpty()) {
                for (Complain complain : compList) {
                    onResponseClicked(complain);
                }
            } else {
                System.out.println("No new complains to display.");
            }
            //stopLoading();
        });
    }

    private void openAddResponseView(Complain complain) {
        try {
            // Load popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addResponse.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Get the controller and set meal details
            AddResponseController addResponseController = loader.getController();
            addResponseController.setCompDetails(
                    complain.getName(),
                    complain.getTell(),
                    complain.getId(),
                    complain.getKind(),
                    complain.getEmail(),
                    complain.getOrderNum()
            );
            stage.show();



//            Stage mainStage = (Stage) stackPane.getScene().getWindow();
//            ColorAdjust blur = new ColorAdjust();
//            blur.setBrightness(-0.7);  // Simulate blur effect
//            mainStage.getScene().getRoot().setEffect(blur);
//
//            // Create a new popup stage (modal window)
//            Stage popupStage = new Stage();
//            popupStage.setTitle("Meal Details");
//            popupStage.setScene(new Scene(root));
//
//            // Make the popup window undecorated (no frame) BEFORE it is shown
//            popupStage.initStyle(StageStyle.UTILITY); // This must be done before showing the window
//
//            // Make popup modal (disable interaction with main window)
//            popupStage.initModality(Modality.APPLICATION_MODAL);
//            popupStage.initOwner(mainStage); // Link popup to the main window
//
//            // Remove blur effect when popup is closed
//            popupStage.setOnHiding(event -> mainStage.getScene().getRoot().setEffect(null));
//
//            // Show popup
//            popupStage.showAndWait();  // Wait until the popup is closed

        } catch (IOException e) {
            e.printStackTrace();
        }
            restartThePage();

    }
    @Subscribe
    public void handelComplain(complainEvent event) throws IOException {
        restartThePage();
    }

    public void onResponseClicked(Complain complain) { //when click on add response for some complain row
        // Create a new meal row (HBox)
        HBox compRow = new HBox(20);
        compRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden Label to store meal ID
        Label idLabel = new Label(String.valueOf(complain.getId()));
        idLabel.setVisible(false); // Make it invisible
        idLabel.setManaged(false); // Ensure it doesn't take layout space

        // Meal Details
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(complain.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label tellLabel = new Label(complain.getTell());
        tellLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        detailsBox.getChildren().addAll(nameLabel, tellLabel);

        // Meal Date
        Label dateLabel = new Label(complain.getDate()+"");
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");

        // Button to add responce
        Button responseBTN = new Button("Response");
        responseBTN.setStyle("-fx-background-color: #222222; -fx-text-fill: #f3f3f3; -fx-background-radius: 20px; -fx-padding: 10px 15px;");

        //changePriceButton.setOnAction(event -> openChangePricePage(nameLabel.getText(), priceLabel,idLabel.getText()));
        responseBTN.setOnAction(event ->
                {
                    /*if(Objects.equals(addToCartBTN.getText(), "Add to Cart")) {
                        addToCartBTN.setText("Added!");
                        addToCartBTN.setDisable(true);
                        listOfMeals.add(meal);
                        numberOfMeals++;
                        updateCartBadge();
                    }*/
                    openAddResponseView(complain);
                }
        );

        // Add components to mealRow
        if (complain.getStatus().equals("Do")) {
            compRow.getChildren().addAll(detailsBox, dateLabel, responseBTN);
        } else {
            compRow.getChildren().addAll(detailsBox, dateLabel);
        }

        // Add mealRow to menuContainer
        ListCompContainer.getChildren().add(compRow);

        addResponseButtons.put(String.valueOf(complain.getId()), responseBTN);
        //mealPriceLabels.put(String.valueOf(meal.getId()), priceLabel);
    }






}
