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

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.deliveryPrice;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.getClient;

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
    private Complain complainToDo = null;
    private int caseToDo ; // 0 if add response 1 if show response 2 if show order
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

    private void openAddResponseView(Complain complain,Order order) {
        try {
            // Load popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addResponse.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            // Get the controller and set meal details
            AddResponseController addResponseController = loader.getController();
            String restName;
            if(complain.getRestaurant() != null)
                restName = complain.getRestaurant().getRestaurantName();
            else restName = "null";
            String orderStatus;
            if(order != null)
                orderStatus = order.getOrderStatus();
            else orderStatus = "null";
            addResponseController.setCompDetails(
                    complain.getName(),
                    complain.getTell(),
                    complain.getId(),
                    complain.getKind(),
                    complain.getEmail(),
                    complain.getOrderNum(),
                    restName,
                    complain.getTime(),
                    orderStatus
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

        // Complain ID
        Label idShownLabel = new Label("ID: " + complain.getId());
        idShownLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        // Restaurant
        Label restaurantLabel;
        if(complain.getRestaurant() != null)
             restaurantLabel = new Label("Restaurant: " + complain.getRestaurant().getRestaurantName());
        else restaurantLabel = new Label("Restaurant is null");
        restaurantLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        // Name & Tell
        Label nameLabel = new Label(complain.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label tellLabel = new Label(complain.getTell());
        tellLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

        detailsBox.getChildren().addAll(idShownLabel, restaurantLabel, nameLabel, tellLabel);


        // Complaint Date
        Label dateLabel = new Label(complain.getDate() + "");
        dateLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");

        // Response Button
        Button responseBTN = new Button("Response");
        responseBTN.setStyle("-fx-background-color: #222222; -fx-text-fill: #f3f3f3; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
        responseBTN.setOnAction(event ->

        {
            caseToDo = 0;
            try {
                controller(complain);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        );

        // Show Order Button (Only for 'Complaint' kind)
        Button showOrderBTN = new Button("Show Order");
        showOrderBTN.setStyle("-fx-background-color: #0044cc; -fx-text-fill: #ffffff; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
        showOrderBTN.setOnAction(event ->
                {
                  caseToDo = 2;
                    try {
                        controller(complain);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                //openOrderDetails(complain.getOrderNum())
        );

        // Show Response Button for all done statuses
        Button showResponseBTN = new Button("Show Response");
        showResponseBTN.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
        showResponseBTN.setOnAction(event ->
                {
                    caseToDo = 1;
                    try {
                        controller(complain);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                //openShowResponsePopup(complain)
        );


        // Add buttons and details based on status and kind
        if ("Do".equals(complain.getStatus())) {
            if ("Complaint".equals(complain.getKind())) {
                compRow.getChildren().addAll(detailsBox, dateLabel, responseBTN, showOrderBTN);
            } else {
                compRow.getChildren().addAll(detailsBox, dateLabel, responseBTN);
            }
        } else {
            if ("Complaint".equals(complain.getKind())) {
                compRow.getChildren().addAll(detailsBox, dateLabel, showOrderBTN, showResponseBTN);
            } else {
                compRow.getChildren().addAll(detailsBox, dateLabel, showResponseBTN);
            }
        }
        //complain.get
        // Add complaint row to the container
        ListCompContainer.getChildren().add(compRow);

        addResponseButtons.put(String.valueOf(complain.getId()), responseBTN);
    }
    private void controller(Complain complain) throws IOException {
        switch(caseToDo) {
            case 0: {
                if(complain.getKind().equals("Complaint")) {
                    complainToDo = complain;
                    getClient().sendToServer("showorder"+complain.getOrderNum());
                }
                else openAddResponseView(complain,null);
                break;
            }
            case 1:
            {
                if (complain.getKind().equals("Complaint")) {
                    complainToDo = complain;
                    getClient().sendToServer("showorder"+complain.getOrderNum());
                }
                else openShowResponsePopup(complain,null);
                break;
            }
            case 2:
            {
                complainToDo = complain;
                getClient().sendToServer("showorder"+complain.getOrderNum());
                break;
            }
        }
    }


    private void openShowResponsePopup(Complain complain,Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addResponse.fxml"));
            Parent root = loader.load();
            AddResponseController controller = loader.getController();



            controller.setShowMode(true); // to turn off editing and change button text
            String restName;
            if(complain.getRestaurant() != null)
                 restName = complain.getRestaurant().getRestaurantName();
            else restName = "null";
            // Set data to show
            String orderStatus;
            if(order != null)
                 orderStatus = order.getOrderStatus();
            else orderStatus = "null";
            controller.showResp(
                    complain.getName(),
                    complain.getTell(),
                    complain.getResponse(),
                    complain.getRefund(),
                    complain.getKind(),
                    complain.getEmail(),
                    complain.getId(),
                    restName,
                    complain.getTime(),
                    orderStatus
            );
            //complain.get

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Show Response");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openOrderDetails(Order order) {
        ShowTheMeals(order);
    }
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
                switch (caseToDo)
                {
                    case 0: {
                        openAddResponseView(complainToDo,order);
                        break;
                    }
                    case 1: {
                        openShowResponsePopup(complainToDo,order);
                        break;
                    }
                    case 2: {
                        openOrderDetails(order);
                        break;
                    }
                }
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
    private void ShowTheMeals(Order order) {
        StringBuilder orderDetails = new StringBuilder();
        double totalAmount = order.getTotal_price();

        String totalAmountText = String.format("Total: %.2f₪", totalAmount);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryWindow.fxml"));
            Stage summaryStage = new Stage();
            Scene scene = new Scene(loader.load());
            OrderSummaryController controller = loader.getController();

            controller.setSummary(summaryStage, orderDetails.toString(), totalAmountText);
            controller.getCheckOutbtn().setVisible(false);

            VBox mealDetailsContainer = controller.getMealDetailsContainer();

            for (MealInTheCart meal : order.getMeals()) {
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

                // Create container for all text content
                VBox textContainer = new VBox(5);

                // Add meal name and quantity
                HBox nameQuantityBox = new HBox(5);
                Text mealName = new Text(meal.getMeal().getMeal().getName() + " - ");
                Text boldX = new Text("X");
                boldX.setStyle("-fx-font-weight: bold;");
                Text quantity = new Text(String.valueOf(meal.getQuantity()));
                quantity.setStyle("-fx-font-weight: bold;");
                nameQuantityBox.getChildren().addAll(mealName, boldX, quantity);
                textContainer.getChildren().add(nameQuantityBox);

                // Calculate price with discount if applicable
                double discount = meal.getDiscount_percentage();
                double price = meal.getPrice();
                double finalPrice = discount > 0 ? price * (1 - discount / 100) : price;

                // Add price information
                if (discount > 0) {
                    // Original price with strikethrough
                    HBox originalPriceBox = new HBox(5);
                    Text originalPriceLabel = new Text("Original: ");
                    Text originalPrice = new Text(String.format("%.2f₪", price));
                    originalPrice.setStyle("-fx-strikethrough: true; -fx-fill: #999999;");
                    originalPriceBox.getChildren().addAll(originalPriceLabel, originalPrice);
                    textContainer.getChildren().add(originalPriceBox);

                    // Discounted price
                    HBox discountedPriceBox = new HBox(5);
                    Text discountedPriceLabel = new Text("Price: ");
                    Text discountedPrice = new Text(String.format("%.2f₪", finalPrice));
                    discountedPrice.setStyle("-fx-font-weight: bold; -fx-fill: black;");
                    discountedPriceBox.getChildren().addAll(discountedPriceLabel, discountedPrice);
                    textContainer.getChildren().add(discountedPriceBox);

                    // Discount badge
                    Text discountBadge = new Text(String.format("(%d%% OFF)", (int)discount));
                    discountBadge.setStyle("-fx-font-weight: bold; -fx-fill: #fe3b30;");
                    textContainer.getChildren().add(discountBadge);
                } else {
                    // Regular price
                    HBox priceBox = new HBox(5);
                    Text priceLabel = new Text("Price: ");
                    Text priceText = new Text(String.format("%.2f₪", price));
                    priceText.setStyle("-fx-font-weight: bold;");
                    priceBox.getChildren().addAll(priceLabel, priceText);
                    textContainer.getChildren().add(priceBox);
                }

                // Add meal description (if available)
                if (meal.getMeal().getMeal().getDescription() != null && !meal.getMeal().getMeal().getDescription().isEmpty()) {
                    Text description = new Text(meal.getMeal().getMeal().getDescription());
                    textContainer.getChildren().add(description);
                }

                // Add customizations (with check/uncheck images)
                if (meal.getMeal().getCustomizationsList() != null && !meal.getMeal().getCustomizationsList().isEmpty()) {
                    Text customizationsTitle = new Text("Customizations:");
                    customizationsTitle.setStyle("-fx-font-weight: bold;");
                    textContainer.getChildren().add(customizationsTitle);

                    Image checkedImage = new Image(getClass().getResourceAsStream("/images/checked.png"));
                    Image uncheckedImage = new Image(getClass().getResourceAsStream("/images/unchecked.png"));

                    for (CustomizationWithBoolean customWithBool : meal.getMeal().getCustomizationsList()) {
                        HBox customRow = new HBox(5);
                        Text customText = new Text(customWithBool.getCustomization().getName());
                        customText.setStyle("-fx-fill: black;");

                        ImageView checkImageView = new ImageView(customWithBool.getValue() ? checkedImage : uncheckedImage);
                        checkImageView.setFitWidth(20);
                        checkImageView.setFitHeight(20);
                        checkImageView.setPreserveRatio(true);

                        customRow.getChildren().addAll(checkImageView, customText);
                        textContainer.getChildren().add(customRow);
                    }
                }

                // Add the text container to the row
                mealRow.getChildren().add(textContainer);
                mealDetailsContainer.getChildren().add(mealRow);
            }

            if(order.getOrderType().equals("Delivery")) {
                HBox deliveryRow = new HBox(10);
                deliveryRow.setSpacing(10);

                Image delivery = new Image(getClass().getResourceAsStream("/images/delivery_icon.png"));

                ImageView imageView = new ImageView(delivery);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);

                Rectangle clip = new Rectangle(100, 100);
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                imageView.setClip(clip);

                deliveryRow.getChildren().add(imageView);

                TextFlow DeliveryInfo = new TextFlow();

                Text text = new Text("Delivery - ");

                Text deliveryprice = new Text("("+deliveryPrice+"₪)\n");
                deliveryprice.setStyle("-fx-font-weight: bold;");

                DeliveryInfo.getChildren().addAll(text, deliveryprice, new Text("\n"));

                deliveryRow.getChildren().add(DeliveryInfo);

                mealDetailsContainer.getChildren().add(deliveryRow);
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
