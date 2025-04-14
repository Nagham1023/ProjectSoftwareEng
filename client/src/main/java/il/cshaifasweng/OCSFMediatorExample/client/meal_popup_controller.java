package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    public void setMealDetails(String name, String description, double price, byte[] bytes,
                               Set<Customization> customizations, Meal meal, Runnable onAddToCartDone) {
        mealName.setText(name);
        mealDescription.setText(description);

        // Handle price display based on discount
        double discount = meal.getDiscount_percentage();
        if (discount > 0) {
            double discountedPrice = price * (1 - discount / 100);

            // Create a container for the price information
            VBox priceContainer = new VBox(5);

            // Original price with strikethrough - using Text for reliable strikethrough
            HBox originalPriceRow = new HBox(5);
            Label originalPriceLabel = new Label("Original price:");

            Text originalPriceText = new Text(String.format("₪ %.2f", price));
            originalPriceText.setStyle("-fx-strikethrough: true; -fx-font-size: 14px; -fx-fill: #999999;");

            // Wrap in StackPane for proper layout
            StackPane originalPricePane = new StackPane(originalPriceText);
            originalPricePane.setAlignment(Pos.CENTER_LEFT);

            originalPriceRow.getChildren().addAll(originalPriceLabel, originalPricePane);

            // Discounted price
            HBox discountedPriceRow = new HBox(5);
            Label discountedPriceLabel = new Label("Discounted price:");
            Label discountedPriceValue = new Label(String.format("₪ %.2f", discountedPrice));
            discountedPriceValue.setStyle("-fx-font-size: 16px; -fx-text-fill: #b70236; -fx-font-weight: bold;");
            discountedPriceRow.getChildren().addAll(discountedPriceLabel, discountedPriceValue);

            // Discount badge
            Label discountBadge = new Label(discount + "% OFF");
            discountBadge.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white; " +
                    "-fx-background-color: #fe3b30; -fx-background-radius: 5; -fx-padding: 2 5;");

            priceContainer.getChildren().addAll(originalPriceRow, discountedPriceRow, discountBadge);

            // Find and replace the price label in the parent VBox
            VBox parentContainer = (VBox) mealPrice.getParent();
            int priceIndex = parentContainer.getChildren().indexOf(mealPrice);
            parentContainer.getChildren().set(priceIndex, priceContainer);
        } else {
            // Regular price display
            mealPrice.setText(String.format("₪ %.2f", price));
            mealPrice.setStyle("-fx-font-size: 18px; -fx-text-fill: #b70236; -fx-font-weight: bold;");
        }

        // Load meal image
        if (bytes != null) {
            try {
                Image image = new Image(new ByteArrayInputStream(bytes));
                mealImage.setImage(image);
            } catch (Exception e) {
                System.err.println("Error loading meal image: " + e.getMessage());
            }
        }

        this.meal = meal;
        this.onAddToCartDone = onAddToCartDone;

        // Clear previous customizations
        customizationContainer.getChildren().clear();
        customizationSelections.clear();

        // Add "Customizations:" label if there are customizations
        if (!customizations.isEmpty()) {
            Label customizationsLabel = new Label("Customizations:");
            customizationsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            customizationContainer.getChildren().add(customizationsLabel);
        }

        // Load images for selection
        Image unselectedImage = new Image(getClass().getResourceAsStream("/images/unchecked.png"));
        Image selectedImage = new Image(getClass().getResourceAsStream("/images/checked.png"));

        for (Customization custom : customizations) {
            HBox customRow = new HBox(10);
            customRow.setAlignment(Pos.CENTER_LEFT);

            // Create a label for the customization name
            Label customLabel = new Label(custom.getName());
            customLabel.setStyle("-fx-font-size: 14px;");

            // ImageView to represent selection state
            ImageView imageView = new ImageView(selectedImage);
            imageView.setFitHeight(20);
            imageView.setFitWidth(20);
            imageView.setPreserveRatio(true);

            // Track customization with its selection state
            CustomizationWithBoolean customWithState = new CustomizationWithBoolean(custom, true);
            customizationSelections.add(customWithState);

            // Toggle selection on click
            imageView.setOnMouseClicked(event -> {
                boolean isSelected = customWithState.getValue();
                if (isSelected) {
                    imageView.setImage(unselectedImage);
                    customWithState.setValue(false);
                } else {
                    imageView.setImage(selectedImage);
                    customWithState.setValue(true);
                }
            });

            customRow.getChildren().addAll(imageView, customLabel);
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
