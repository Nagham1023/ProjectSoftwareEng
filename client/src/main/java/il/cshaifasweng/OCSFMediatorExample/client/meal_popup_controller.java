package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.MealInTheCart;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.listOfMeals;
import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.numberOfMeals;

public class meal_popup_controller {

    @FXML private ImageView mealImage;
    @FXML private Label mealName;
    @FXML private Label mealDescription;
    @FXML private Label mealPrice;
    @FXML private VBox customizationContainer;
    @FXML
    private ImageView addToCartImage;
    @FXML
    private VBox mealDetailsBox; // VBox containing the meal details


    Meal meal;
    private Runnable onAddToCartDone;

    // Initialize the popup window with meal details
    public void setMealDetails(String name, String description, double price, byte[] bytes, List<Customization> customizations, Meal meal, Runnable onAddToCartDone) {
        mealName.setText(name);
        mealDescription.setText(description);
        mealPrice.setText(String.format("₪ %.2f", price));
        mealPrice.setStyle("-fx-text-fill: #b70236; -fx-font-weight: bold;");
        Image image = new Image(new ByteArrayInputStream(bytes));
        mealImage.setImage(image);
        this.meal = meal;
        this.onAddToCartDone = onAddToCartDone;

        // Display each customization with a "delete" button
        for (Customization custom : customizations) {
            HBox customRow = new HBox(10);
            Label customLabel = new Label(custom.getName());
            //Button deleteBtn = new Button("X");
            //deleteBtn.setOnAction(e -> customizationContainer.getChildren().remove(customRow));
            //customRow.getChildren().addAll(customLabel, deleteBtn);
            customRow.getChildren().addAll(customLabel);
            customizationContainer.getChildren().add(customRow);
        }
    }


    // Handle "Add to Cart" button
    @FXML
    private void addToCart() {
        listOfMeals.add(new MealInTheCart(meal, 1));
        if (onAddToCartDone != null) {
            onAddToCartDone.run();
        }
        closePopup();
    }



    // Close the popup
    @FXML
    private void closePopup() {
        Stage stage = (Stage) mealImage.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void initialize() throws Exception {
        addToCartImage.setImage(new Image(getClass().getResourceAsStream("/images/white_cart.png")));
        mealImage.setFitHeight(150);
        mealImage.setFitWidth(150);
        mealImage.setPreserveRatio(true);


        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);  // Set the roundness of the corners
        clip.setArcHeight(20);
        clip.setWidth(mealImage.getFitWidth());
        clip.setHeight(mealImage.getFitHeight());
        mealImage.setClip(clip);

    }

}
