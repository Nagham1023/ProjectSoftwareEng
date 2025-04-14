package il.cshaifasweng.OCSFMediatorExample.client;

import com.google.protobuf.StringValue;
import il.cshaifasweng.OCSFMediatorExample.client.events.DeleteMealEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.UpdatePriceRequestEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RequestViewController {
    List<UpdatePriceRequestEvent> reqs;
    public boolean isModifyMode = false;
    public String currentWorker;

    @FXML
    private VBox requestsContainer;  // MUST match fx:id in FXML file
    private Map<String, HBox> mealNewPriceLabels = new HashMap<>();
    Map<String, List<HBox>> map = new HashMap<>();

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        SimpleClient client = SimpleClient.getClient();
        System.out.println("showing change price requests");
        try{
            if (client.getUser() != null) {
                currentWorker = client.getUser().getRole();
            } else {
                System.err.println("User is null. Cannot initialize WorkerController.");
            }
            client.sendToServer("show change price requests");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void getRequests(PCRequestsList reqs) {
        Platform.runLater(() -> {
            // Clear existing requests
            requestsContainer.getChildren().clear();

            if (reqs.getReqs() != null && !reqs.getReqs().isEmpty()) {
                for (MealUpdateRequest req : reqs.getReqs()) {
                    onAddMealClicked(req);
                }
            } else {
                Label noRequests = new Label("No pending price change requests");
                requestsContainer.getChildren().add(noRequests);
            }
        });
    }

    // Modified meal row creation with both buttons
    /*public void onAddMealClicked(MealUpdateRequest req) {
        String mealId = req.getMealId();
        String key = mealId + req.getNewPrice();
        HBox mealRow = new HBox(20);
        mealRow.setStyle("-fx-background-color: #ffdbe4; -fx-border-color: #881d3a; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden labels for data storage
        Label idLabel = new Label(req.getMealId());
        Label newPriceLabel = new Label(String.valueOf(req.getNewPrice()));
        Label oldPriceLabel = new Label(String.valueOf(req.getOldPrice()));
        Label newDiscountLabel = new Label(String.valueOf(req.getNewDiscount()));
        Label oldDiscountLabel = new Label(String.valueOf(req.getOldDiscount()));

        // Make them invisible
        idLabel.setVisible(false);
        newPriceLabel.setVisible(false);
        oldPriceLabel.setVisible(false);
        newDiscountLabel.setVisible(false);
        oldDiscountLabel.setVisible(false);

        // Image handling
        ImageView imageView = new ImageView();
        if (req.getImage() != null) {
            imageView.setImage(new Image(new ByteArrayInputStream(req.getImage())));
        } else {
            imageView.setImage(new Image("placeholder.png"));
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);

        // Price display VBox
        VBox priceBox = new VBox(5);

        // Old price display
        HBox oldPriceBox = new HBox(5);
        Label oldPriceText = new Label("Old Price:");
        Label oldPriceValue = new Label(String.format("%.2f₪", req.getOldPrice()));
        oldPriceValue.setStyle("-fx-text-fill: #757575;");
        if (req.getOldDiscount() > 0) {
            oldPriceValue.setStyle("-fx-text-fill: #757575; -fx-strikethrough: true;");
            Label oldDiscountBadge = new Label(String.format("(%.2f%%)", req.getOldDiscount()));
            oldDiscountBadge.setStyle("-fx-text-fill: #fe3b30; -fx-font-weight: bold;");
            oldPriceBox.getChildren().addAll(oldPriceText, oldPriceValue, oldDiscountBadge);
        } else {
            oldPriceBox.getChildren().addAll(oldPriceText, oldPriceValue);
        }

        // New price display
        HBox newPriceBox = new HBox(5);
        Label newPriceText = new Label("New Price:");
        Label newPriceValue = new Label(String.format("%.2f₪", req.getNewPrice()));
        newPriceValue.setStyle("-fx-text-fill: #bd2743; -fx-font-weight: bold;");
        if (req.getNewDiscount() > 0) {
            Label newDiscountBadge = new Label(String.format("(%.2f%%)", req.getNewDiscount()));
            newDiscountBadge.setStyle("-fx-text-fill: #fe3b30; -fx-font-weight: bold;");
            newPriceBox.getChildren().addAll(newPriceText, newPriceValue, newDiscountBadge);
        } else {
            newPriceBox.getChildren().addAll(newPriceText, newPriceValue);
        }

        priceBox.getChildren().addAll(oldPriceBox, newPriceBox);

        // Buttons container
        HBox buttonBox = new HBox(10);
        if (isModifyMode) {
            Button acceptButton = new Button("Accept");
            acceptButton.setStyle("-fx-background-color: #A8E6CF; -fx-text-fill: #2E7D32;");
            acceptButton.setOnAction(e -> handlePriceUpdate(
                    idLabel.getText(),
                    newPriceLabel.getText(),
                    newDiscountLabel.getText(),
                    true
            ));

            Button denyButton = new Button("Deny");
            denyButton.setStyle("-fx-background-color: #C75C5C; -fx-text-fill: #FFD9D1;");
            denyButton.setOnAction(e -> handlePriceUpdate(
                    idLabel.getText(),
                    newPriceLabel.getText(),
                    newDiscountLabel.getText(),
                    false
            ));

            buttonBox.getChildren().addAll(acceptButton, denyButton);
        }

        mealRow.getChildren().addAll(
                imageView,
                createDetailsBox(req),
                priceBox,
                buttonBox
        );

        requestsContainer.getChildren().add(mealRow);
        mealRow.getProperties().put("mealNewPriceKey", key);
        mealNewPriceLabels.put(key, mealRow);
        map.computeIfAbsent(req.getMealId(), k -> new ArrayList<>()).add(mealRow);
    }*/
    public void onAddMealClicked(MealUpdateRequest req) {
        String mealId = req.getMealId();
        String key = mealId + req.getNewPrice();
        HBox mealRow = new HBox(20);
        mealRow.setStyle("-fx-background-color: #ffdbe4; -fx-border-color: #881d3a; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");
        mealRow.setAlignment(Pos.CENTER_LEFT);

        // Hidden labels for data storage
        Label idLabel = new Label(req.getMealId());
        Label newPriceLabel = new Label(String.valueOf(req.getNewPrice()));
        Label oldPriceLabel = new Label(String.valueOf(req.getOldPrice()));
        Label newDiscountLabel = new Label(String.valueOf(req.getNewDiscount()));
        Label oldDiscountLabel = new Label(String.valueOf(req.getOldDiscount()));

        // Make them invisible
        idLabel.setVisible(false);
        newPriceLabel.setVisible(false);
        oldPriceLabel.setVisible(false);
        newDiscountLabel.setVisible(false);
        oldDiscountLabel.setVisible(false);

        // Image handling
        ImageView imageView = new ImageView();
        if (req.getImage() != null) {
            imageView.setImage(new Image(new ByteArrayInputStream(req.getImage())));
        } else {
            imageView.setImage(new Image("placeholder.png"));
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        // Price change display - using a GridPane for perfect alignment
        GridPane priceGrid = new GridPane();
        priceGrid.setAlignment(Pos.CENTER_LEFT);
        priceGrid.setHgap(5);
        priceGrid.setVgap(0);

        // Old price with discount
        HBox oldPriceBox = new HBox(2);
        oldPriceBox.setAlignment(Pos.CENTER_LEFT);
        Label oldPriceValue = new Label(String.format("%.2f₪", req.getOldPrice()));
        oldPriceValue.setStyle("-fx-text-fill: #757575;");

        if (req.getOldDiscount() > 0) {
            oldPriceValue.setStyle("-fx-text-fill: #757575; -fx-strikethrough: true;");
            Label oldDiscountBadge = new Label(String.format(" (%.2f%%)", req.getOldDiscount()));
            oldDiscountBadge.setStyle("-fx-text-fill: #fe3b30; -fx-font-weight: bold;");
            oldPriceBox.getChildren().addAll(oldPriceValue, oldDiscountBadge);
        } else {
            oldPriceBox.getChildren().add(oldPriceValue);
        }

        // Arrow with centered alignment
        Label arrow = new Label("→");
        arrow.setStyle("-fx-text-fill: #881d3a; -fx-font-weight: bold; -fx-padding: 0 5 0 5;");
        arrow.setAlignment(Pos.CENTER);
        arrow.setMinHeight(Control.USE_PREF_SIZE);

        // New price with discount
        HBox newPriceBox = new HBox(2);
        newPriceBox.setAlignment(Pos.CENTER_LEFT);
        Label newPriceValue = new Label(String.format("%.2f₪", req.getNewPrice()));
        newPriceValue.setStyle("-fx-text-fill: #bd2743; -fx-font-weight: bold;");

        if (req.getNewDiscount() > 0) {
            Label newDiscountBadge = new Label(String.format(" (%.2f%%)", req.getNewDiscount()));
            newDiscountBadge.setStyle("-fx-text-fill: #fe3b30; -fx-font-weight: bold;");
            newPriceBox.getChildren().addAll(newPriceValue, newDiscountBadge);
        } else {
            newPriceBox.getChildren().add(newPriceValue);
        }

        // Add components to grid (row 0 for perfect vertical alignment)
        priceGrid.add(oldPriceBox, 0, 0);
        priceGrid.add(arrow, 1, 0);
        priceGrid.add(newPriceBox, 2, 0);
        GridPane.setValignment(oldPriceBox, VPos.CENTER);
        GridPane.setValignment(arrow, VPos.CENTER);
        GridPane.setValignment(newPriceBox, VPos.CENTER);

        // Buttons container
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        if (isModifyMode) {
            Button acceptButton = new Button("Accept");
            acceptButton.setStyle("-fx-background-color: #A8E6CF; -fx-text-fill: #2E7D32;");
            acceptButton.setOnAction(e -> handlePriceUpdate(
                    idLabel.getText(),
                    newPriceLabel.getText(),
                    newDiscountLabel.getText(),
                    true
            ));

            Button denyButton = new Button("Deny");
            denyButton.setStyle("-fx-background-color: #C75C5C; -fx-text-fill: #FFD9D1;");
            denyButton.setOnAction(e -> handlePriceUpdate(
                    idLabel.getText(),
                    newPriceLabel.getText(),
                    newDiscountLabel.getText(),
                    false
            ));

            buttonBox.getChildren().addAll(acceptButton, denyButton);
        }

        mealRow.getChildren().addAll(
                imageView,
                createDetailsBox(req),
                priceGrid,
                buttonBox
        );

        requestsContainer.getChildren().add(mealRow);
        mealRow.getProperties().put("mealNewPriceKey", key);
        mealNewPriceLabels.put(key, mealRow);
        map.computeIfAbsent(req.getMealId(), k -> new ArrayList<>()).add(mealRow);
    }

    private VBox createDetailsBox(MealUpdateRequest req) {
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(req.getMealName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label descLabel = new Label(req.getMealDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #760b0b;");

        detailsBox.getChildren().addAll(nameLabel, descLabel);
        return detailsBox;
    }

    private void handlePriceUpdate(String mealId, String newPrice, String newDiscount, boolean approve) {
        try {
            if(approve) {
                updatePrice request = new updatePrice(Double.parseDouble(newPrice), Integer.parseInt(mealId),Double.parseDouble(newDiscount) ,"changing");
                SimpleClient.getClient().sendToServer(request);
            }
            else{
                updatePrice request = new updatePrice(Double.parseDouble(newPrice), Integer.parseInt(mealId),Double.parseDouble(newDiscount) ,"denying");
                SimpleClient.getClient().sendToServer(request);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void deleteRowRequest(UpdatePriceRequestEvent updatePrice) {
        //System.out.println("changing price now!");
        String mealId = String.valueOf(updatePrice.getRequest().getIdMeal());
        String newPrice = String.valueOf(updatePrice.getRequest().getNewPrice());
        // Check if the mealId exists in the map
        HBox current = mealNewPriceLabels.get(mealId+newPrice);
        if (current != null) {
            Platform.runLater(() -> {
                // 1. Remove from UI container
                requestsContainer.getChildren().remove(current);

                // 2. Remove from tracking map
                mealNewPriceLabels.remove(mealId + newPrice);
                List<HBox> hboxList = map.get(mealId); // Get the list of HBoxes

                if (hboxList != null) {
                    hboxList.remove(current); // Remove the specific HBox

                    if (hboxList.isEmpty()) {
                        map.remove(mealId); // Remove key if list is empty
                    }
                }

            });
        } else {
            System.out.println("Meal with ID " + mealId + " not found.");
        }
    }

    @Subscribe
    public void addNewRequest(MealUpdateRequest req) {
        /*System.out.println("adding new UpdatePrice request for this client");
        System.out.println("Meal's id is " + req.getMealId());
        Platform.runLater(() -> {
            onAddMealClicked(req);
        });
        System.out.println("added the new meal for this client");*/
        EventBus.getDefault().unregister(this);
        Platform.runLater(this::initialize);

    }


    @Subscribe
    public void deleteRowMealReq(DeleteMealEvent event) {
        String mealId = event.getId();
        List<HBox> hboxList = map.get(mealId);

        if (hboxList != null) {
            Platform.runLater(() -> {
                for (HBox item : hboxList) {
                    // Retrieve the key stored in the HBox's properties
                    String key = (String) item.getProperties().get("mealNewPriceKey");
                    if (key != null) {
                        mealNewPriceLabels.remove(key);
                    }
                    // Remove the HBox from the UI
                    requestsContainer.getChildren().remove(item);
                }
                // Remove the mealId entry from the map
                map.remove(mealId);
            });
        } else {
            System.out.println("Meal not found: " + mealId);
            refreshMenu();
        }
    }

    private void refreshMenu() {
        try {
            SimpleClient.getClient().sendToServer("show change price requests");
        } catch (IOException e) {
            System.err.println("Failed to refresh change price requests " + e.getMessage());
        }
    }



    // Rest of the existing methods...

    public boolean isModifyMode() {
        return isModifyMode;
    }

    public void setModifyMode(boolean modifyMode) {
        isModifyMode = modifyMode;
    }
}
