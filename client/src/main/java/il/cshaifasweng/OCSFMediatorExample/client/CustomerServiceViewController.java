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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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


    @FXML
    private AnchorPane anchorPane;

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
    void GenerateButton() throws IOException {
        if(statusVal==null||kindVal==null)
            checkLabel.setText("Need To Choose Kind And Status!");
        else{
            checkLabel.setText("Generating..");
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
        System.out.println("Starting the init");

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
    @Subscribe
    public void handleComplainListEvent(ListComplainList event) {
        Platform.runLater(()->{
            this.listComplainList = event;
            event.tooString();
            list1 = listComplainList.getComplaincd();
            list2 = listComplainList.getComplaincn();
            list3 = listComplainList.getComplainfd();
            list4 = listComplainList.getComplainfn();
            list5 = listComplainList.getComplainsd();
            list6 = listComplainList.getComplainsn();
            try {
                GenerateButton();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void GetTheComplains(List<Complain> compList) {

        //System.out.println("GetTheComplains");
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



        } catch (IOException e) {
            e.printStackTrace();
        }


        checkLabel.setText("response finished!");

    }
    @Subscribe
    public void handelComplain(complainEvent event) {
        Platform.runLater(() -> {
            try {
                SimpleClient.getClient().sendToServer("getAllComplaints6");
                //GenerateButton();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });
    }

    /*
    public void onResponseClicked(Complain complain) {

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
    /*
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

    */

    public void onResponseClicked(Complain complain) {
        // Create a new complaint row (HBox)
        HBox compRow = new HBox(20);
        compRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden Label to store complaint ID
        Label idLabel = new Label(String.valueOf(complain.getId()));
        idLabel.setVisible(false); // Make it invisible
        idLabel.setManaged(false); // Ensure it doesn't take layout space

        // Complaint Details
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(complain.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label tellLabel = new Label(complain.getTell());
        tellLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        detailsBox.getChildren().addAll(nameLabel, tellLabel);

        // Complaint Date
        Label dateLabel = new Label(complain.getDate() + "");
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");

        // Response Button
        Button responseBTN = new Button("Response");
        responseBTN.setStyle("-fx-background-color: #222222; -fx-text-fill: #f3f3f3; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
        responseBTN.setOnAction(event -> openAddResponseView(complain));

        // Show Order Button (Only for 'Complaint' kind)
        Button showOrderBTN = new Button("Show Order");
        showOrderBTN.setStyle("-fx-background-color: #0044cc; -fx-text-fill: #ffffff; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
        showOrderBTN.setOnAction(event -> openOrderDetails(complain.getOrderNum()));

        // Add buttons and details based on status and kind
        if ("Do".equals(complain.getStatus())) {
            if ("Complaint".equals(complain.getKind())) {
                compRow.getChildren().addAll(detailsBox, dateLabel, responseBTN, showOrderBTN);
            } else {
                compRow.getChildren().addAll(detailsBox, dateLabel, responseBTN);
            }
        } else {
            if ("Complaint".equals(complain.getKind())) {
                compRow.getChildren().addAll(detailsBox, dateLabel, showOrderBTN);
            } else {
                compRow.getChildren().addAll(detailsBox, dateLabel);
            }
        }

        // Add complaint row to the container
        ListCompContainer.getChildren().add(compRow);

        addResponseButtons.put(String.valueOf(complain.getId()), responseBTN);
    }
    private void openOrderDetails(String orderNum) {
        try {
            System.out.println("openOrderDetails: " + orderNum);
            SimpleClient.getClient().sendToServer("showorder"+orderNum);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*@Subscribe
    public void onEvent(MealsList event){
        Platform.runLater(() -> {
            List<Meal> meals = event.getMeals();
            List<MealInTheCart> newmeals = new ArrayList<>();
            List<personal_Meal> pmeals = new ArrayList<>();
            List<CustomizationWithBoolean> customizations = new ArrayList<>();
            for (Meal meal : meals) {
                for(Customization Custo :meal.getCustomizations())
                {
                    CustomizationWithBoolean temp = new CustomizationWithBoolean(Custo,true) ;
                    customizations.add(temp);
                }
                personal_Meal temp = new personal_Meal(meal,customizations);
                pmeals.add(temp);
            }
            for(personal_Meal pmeal :pmeals)
            {
                MealInTheCart temp = new MealInTheCart(pmeal,2);
                newmeals.add(temp);
            }
            ShowTheMeals(newmeals);
        });
    }*/
    @Subscribe
    public void gotTheOrder(Order order){
        Platform.runLater(() -> {
            if (order == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Order Not Found");
                alert.setHeaderText(null);
                alert.setContentText("The order you are looking for does not exist!");
                alert.showAndWait();
            } else {
                System.out.println("gotTheOrder: " + order);
                ShowTheMeals(order.getMeals());
            }
        });

    }
    @Subscribe
    public void emptyOrder(String msg) {
        if(msg.equals("Order not found."))
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Order Not Found");
                alert.setHeaderText(null);
                alert.setContentText("The order you are looking for does not exist!");
                alert.showAndWait();
            });
    }
    private void ShowTheMeals(List<MealInTheCart> listOfMeals) {
        StringBuilder orderDetails = new StringBuilder();
        double totalAmount = 0.0;

        for (MealInTheCart meal : listOfMeals) {
            totalAmount += meal.getMeal().getMeal().getPrice() * meal.getQuantity();
            orderDetails.append("\n");
        }

        String totalAmountText = "Total: " + totalAmount + "₪";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryWindow.fxml"));
            Stage summaryStage = new Stage();
            Scene scene = new Scene(loader.load());
            OrderSummaryController controller = loader.getController();

            controller.setSummary(summaryStage, orderDetails.toString(), totalAmountText);
            controller.getCheckOutbtn().setVisible(false);

            VBox mealDetailsContainer = controller.getMealDetailsContainer();

            for (MealInTheCart meal : listOfMeals) {
                HBox mealRow = new HBox(10);
                mealRow.setSpacing(10);

                byte[] imageBytes = meal.getMeal().getMeal().getImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    Image mealImage = new Image(byteArrayInputStream);

                    ImageView imageView = new ImageView(mealImage);
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);

                    Rectangle clip = new Rectangle(100, 100);
                    clip.setArcWidth(20);
                    clip.setArcHeight(20);
                    imageView.setClip(clip);

                    mealRow.getChildren().add(imageView);
                }

                TextFlow mealInfoTextFlow = new TextFlow();

                Text mealName = new Text(meal.getMeal().getMeal().getName() + " - ");
                Text boldX = new Text("X");
                boldX.setStyle("-fx-font-weight: bold;");
                Text quantity = new Text(String.valueOf(meal.getQuantity()));
                quantity.setStyle("-fx-font-weight: bold;");

                Text mealPrice = new Text(" (" + meal.getMeal().getMeal().getPrice() + "₪)\n");
                mealPrice.setStyle("-fx-font-weight: bold;");

                mealInfoTextFlow.getChildren().addAll(mealName, boldX, quantity,mealPrice, new Text("\n"));

                if (meal.getMeal().getMeal().getDescription() != null && !meal.getMeal().getMeal().getDescription().isEmpty()) {
                    Text description = new Text(meal.getMeal().getMeal().getDescription() + "\n");
                    mealInfoTextFlow.getChildren().add(description);
                }

                // Customizations with check/uncheck images
                if (meal.getMeal().getCustomizationsList() != null && !meal.getMeal().getCustomizationsList().isEmpty()) {
                    Text customizationsTitle = new Text("Customizations:\n");
                    customizationsTitle.setStyle("-fx-font-weight: bold;");
                    mealInfoTextFlow.getChildren().add(customizationsTitle);

                    Image checkedImage = new Image(getClass().getResourceAsStream("/images/checked.png"));
                    Image uncheckedImage = new Image(getClass().getResourceAsStream("/images/unchecked.png"));

                    for (CustomizationWithBoolean customWithBool : meal.getMeal().getCustomizationsList()) {
                        HBox customRow = new HBox(5);
                        Label customLabel = new Label(customWithBool.getCustomization().getName());
                        customLabel.setStyle("-fx-text-fill: black;");

                        ImageView checkImageView = new ImageView(customWithBool.getValue() ? checkedImage : uncheckedImage);
                        checkImageView.setFitWidth(20);
                        checkImageView.setFitHeight(20);
                        checkImageView.setPreserveRatio(true);

                        customRow.getChildren().addAll(checkImageView, customLabel);
                        mealInfoTextFlow.getChildren().add(customRow);
                    }
                }

                mealRow.getChildren().add(mealInfoTextFlow);
                mealDetailsContainer.getChildren().add(mealRow);
            }

            Stage mainStage = (Stage) anchorPane.getScene().getWindow();
            ColorAdjust blur = new ColorAdjust();
            blur.setBrightness(-0.7);
            mainStage.getScene().getRoot().setEffect(blur);

            summaryStage.initModality(Modality.APPLICATION_MODAL);
            summaryStage.initOwner(mainStage);

            summaryStage.setOnHiding(event ->
                    {
                        mainStage.getScene().getRoot().setEffect(null);
//                        updateCart();
                    }
            );
            summaryStage.setScene(scene);
            summaryStage.setTitle("Order Summary");
            summaryStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
