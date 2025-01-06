package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static il.cshaifasweng.OCSFMediatorExample.client.PrimaryController.meals;


public class menu_controller {

    @FXML
    private VBox menuContainer; // Links to fx:id in FXML
    private Map<String, Label> mealPriceLabels = new HashMap<>();

    // Method to handle Add Meal button click
    //@FXML
    public void onAddMealClicked(String mealName,String mealDescription,String mealPrice,String mealId,byte[] imageData) {
        // Create a new meal row (HBox)
        HBox mealRow = new HBox(20);
        mealRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        // Hidden Label to store meal ID
        Label idLabel = new Label(mealId);
        idLabel.setVisible(false); // Make it invisible
        idLabel.setManaged(false); // Ensure it doesn't take layout space




        // Meal Image
        ImageView imageView = new ImageView();

        if (imageData != null) {
            Image image = new Image(new ByteArrayInputStream(imageData));
            imageView = new ImageView(image);
            // Add imageView to your UI
        } else {
            System.err.println("Image data is null for the meal.");
            // Handle the case where there is no image
        }
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        // Meal Details
        VBox detailsBox = new VBox(5);
        Label nameLabel = new Label(mealName);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label descriptionLabel = new Label(mealDescription);
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        detailsBox.getChildren().addAll(nameLabel, descriptionLabel);

        // Meal Price
        Label priceLabel = new Label(mealPrice+"₪");
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF9800;");

        // Button to Change Price
        Button changePriceButton = new Button("Change Price");
        changePriceButton.setOnAction(event -> openChangePricePage(nameLabel.getText(), priceLabel,idLabel.getText()));

        // Add components to mealRow
        mealRow.getChildren().addAll(imageView, detailsBox, priceLabel, changePriceButton);

        // Add mealRow to menuContainer
        menuContainer.getChildren().add(mealRow);

        mealPriceLabels.put(mealId, priceLabel);
    }
    // Method to open the Change Price page
    private void openChangePricePage(String mealName, Label priceLabel,String Id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/update_menu.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            // Pass data to the Change Price Controller
            update_menu_controller controller = loader.getController();
            controller.setMealDetails(mealName, priceLabel,Id);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Subscribe
    public void updateMealPrice(updatePrice updatePrice) {
        System.out.println("changing price now!");
        String mealId = String.valueOf(updatePrice.getIdMeal());
        String newPrice = String.valueOf(updatePrice.getNewPrice());
        // Check if the mealId exists in the map
        Label priceLabel = mealPriceLabels.get(mealId);
        if (priceLabel != null) {
            Platform.runLater(() -> {
                priceLabel.setText(newPrice + "₪");
            });
        } else {
            System.out.println("Meal with ID " + mealId + " not found.");
        }
    }

    @FXML
    public void initialize() throws Exception {
        EventBus.getDefault().register(this);
        if(meals == null)
            System.out.println("No meals found");
        else
            System.out.println(meals.toString());
        for (mealEvent meal : meals) {
            onAddMealClicked(meal.getMealName(), meal.getMealDisc(), String.valueOf(meal.getPrice()),meal.getId(),meal.getImage());
        }
    }
    @FXML
    void backToHome(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }
}