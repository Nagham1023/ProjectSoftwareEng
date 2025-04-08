package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.listOfMeals;

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

    private Set<CustomizationWithBoolean> customizationSelections = new HashSet<>();


    Meal meal;
    personal_Meal ps ;
    private Runnable onAddToCartDone;

    // Initialize the popup window with meal details
    public void setMealDetails(String name, String description, double price, byte[] bytes, Set<Customization> customizations, Meal meal, Runnable onAddToCartDone) {
        mealName.setText(name);
        mealDescription.setText(description);
        mealPrice.setText(String.format("₪ %.2f", price));
        mealPrice.setStyle("-fx-text-fill: #b70236; -fx-font-weight: bold;");
        Image image = new Image(new ByteArrayInputStream(bytes));
        mealImage.setImage(image);
        this.meal = meal;
        this.onAddToCartDone = onAddToCartDone;

        // Clear previous customizations
        customizationContainer.getChildren().clear();
        customizationSelections.clear();  // Reset the selection list

        // Load images for selection
        Image unselectedImage = new Image(getClass().getResourceAsStream("/images/unchecked.png"));
        Image selectedImage = new Image(getClass().getResourceAsStream("/images/checked.png"));

        for (Customization custom : customizations) {
            HBox customRow = new HBox(10);

            // Create a label for the customization name
            Label customLabel = new Label(custom.getName());

            // ImageView to represent selection state
            ImageView imageView = new ImageView(selectedImage);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setPreserveRatio(true);

            // Track customization with its selection state (default: selected)
            CustomizationWithBoolean customWithState = new CustomizationWithBoolean(custom, true);
            customizationSelections.add(customWithState);

            // Toggle selection on click
            imageView.setOnMouseClicked(event -> {
                boolean isSelected = customWithState.getValue();  // Get current state

                if (isSelected) {
                    imageView.setImage(unselectedImage);
                    customWithState.setValue(false); // Update selection to false
                } else {
                    imageView.setImage(selectedImage);
                    customWithState.setValue(true);  // Update selection to true
                }
            });

            // Add the customization label and image to the row
            customRow.getChildren().addAll(customLabel, imageView);
            customizationContainer.getChildren().add(customRow);
        }
    }





    @FXML
    private void addToCart() {
        listOfMeals.add(new MealInTheCart(new personal_Meal(meal,customizationSelections), 1));
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
