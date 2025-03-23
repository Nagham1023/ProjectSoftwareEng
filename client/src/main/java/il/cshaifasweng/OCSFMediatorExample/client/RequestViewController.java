package il.cshaifasweng.OCSFMediatorExample.client;

import com.google.protobuf.StringValue;
import il.cshaifasweng.OCSFMediatorExample.client.events.DeleteMealEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.UpdatePriceRequestEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    public void onAddMealClicked(MealUpdateRequest req) {
        String mealId = req.getMealId();
        String newPrice = String.valueOf(req.getNewPrice());
        String key = mealId + newPrice;
        HBox mealRow = new HBox(20);
        mealRow.setStyle("-fx-background-color: #ffdbe4; -fx-border-color: #881d3a; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden labels for data storage
        Label idLabel = new Label(req.getMealId());
        Label newPriceLabel = new Label(String.valueOf(req.getNewPrice()));  // Hidden new price
        Label oldPriceLabel = new Label(String.valueOf(req.getOldPrice()));  // Hidden old price

        // Make them invisible
        idLabel.setVisible(false);
        newPriceLabel.setVisible(false);
        oldPriceLabel.setVisible(false);

        // Image handling
        ImageView imageView = new ImageView();
        if (req.getImage() != null) {
            imageView.setImage(new Image(new ByteArrayInputStream(req.getImage())));
        } else {
            imageView.setImage(new Image("placeholder.png")); // Add default image
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);

        // Price display
        Label priceLabel = new Label(String.format("%.2f₪ ← %.2f₪",
                req.getNewPrice(),
                req.getOldPrice()
        ));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #bd2743;");

        // Buttons container
        HBox buttonBox = new HBox(10);
        if (isModifyMode) {
            Button acceptButton = new Button("Accept");
            acceptButton.setStyle("-fx-background-color: #A8E6CF; -fx-text-fill: #2E7D32;");
            acceptButton.setOnAction(e -> handlePriceUpdate(idLabel.getText(), newPriceLabel.getText(), oldPriceLabel.getText(), true));

            Button denyButton = new Button("Deny");
            denyButton.setStyle("-fx-background-color: #C75C5C; -fx-text-fill: #FFD9D1;");
            denyButton.setOnAction(e -> handlePriceUpdate(idLabel.getText(), newPriceLabel.getText(), oldPriceLabel.getText(), false));

            buttonBox.getChildren().addAll(acceptButton, denyButton);
        }

        mealRow.getChildren().addAll(
                imageView,
                createDetailsBox(req),
                priceLabel,
                buttonBox
        );

        requestsContainer.getChildren().add(mealRow);
        System.out.println("showing change price requests for"+ req.getMealId()+req.getNewPrice());
        mealRow.getProperties().put("mealNewPriceKey", key);
        mealNewPriceLabels.put((req.getMealId()+req.getNewPrice()), mealRow);
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

    private void handlePriceUpdate(String mealId, String newPrice, String oldPrice, boolean approve) {
        try {
            if(approve) {
                updatePrice request = new updatePrice(Double.parseDouble(newPrice), Integer.parseInt(mealId) ,"changing");
                SimpleClient.getClient().sendToServer(request);
            }
            else{
                updatePrice request = new updatePrice(Double.parseDouble(newPrice), Integer.parseInt(mealId) ,"denying");
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
        System.out.println("adding new UpdatePrice request for this client");
        System.out.println("Meal's id is " + req.getMealId());
        Platform.runLater(() -> {
            onAddMealClicked(req);
        });
        System.out.println("added the new meal for this client");

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
