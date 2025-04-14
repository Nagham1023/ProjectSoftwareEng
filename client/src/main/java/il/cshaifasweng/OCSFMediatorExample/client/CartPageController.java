package il.cshaifasweng.OCSFMediatorExample.client;
import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.CustomizationWithBoolean;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.MealInTheCart;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.mode;

public class CartPageController {

    public static List<MealInTheCart> listOfMeals = new ArrayList<>();
    public static int numberOfMeals = 0;

    @FXML
    private Button backButton;

    @FXML
    private Label cartTitle;


    @FXML
    private Button clearCartButton;

    @FXML
    private StackPane stackPane;
    @FXML
    private Label mealsNum;


    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button continueButton;

    @FXML
    private StackPane emptyCartPane;

    @FXML
    private ImageView emptyCartImage;
    @FXML
    private VBox cartItemsContainer;

    private static Map<Integer, HBox> mealRowMap = new HashMap<>();
    //            mealsNum.setText("("+Integer.toString(numberOfMeals)+")");
    private void updateCart() {
        // Iterate through the list of meals to check for duplicates
        for (int i = 0; i < listOfMeals.size(); i++) {
            MealInTheCart currentMeal = listOfMeals.get(i);

            // Check if this meal has already been merged with another
            for (int j = i + 1; j < listOfMeals.size(); j++) {
                MealInTheCart nextMeal = listOfMeals.get(j);

                // If both meals have the same ID and identical customizations, merge them
                if (currentMeal.getMeal().getMeal().getId() == nextMeal.getMeal().getMeal().getId() &&
                        areCustomizationsEqual(currentMeal.getMeal().getCustomizationsList(), nextMeal.getMeal().getCustomizationsList())) {

                    // Merge quantities
                    currentMeal.setQuantity(currentMeal.getQuantity() + nextMeal.getQuantity());

                    // Remove the duplicate meal
                    listOfMeals.remove(j);
                    j--; // Adjust the index to account for the removed item
                }
            }
        }

        // Update the cart badge if the number of meals has changed
        if (numberOfMeals != listOfMeals.size()) {
            numberOfMeals = listOfMeals.size();
            mealsNum.setText("("+Integer.toString(numberOfMeals)+")");
            //updateCartBadge();
        }
        clearCartRows();
        initialize();

    }

    private void clearCartRows() {
        cartItemsContainer.getChildren().clear(); // This will remove all child nodes in the container
    }







    private boolean areCustomizationsEqual(Set<CustomizationWithBoolean> list1, Set<CustomizationWithBoolean> list2) {
        if (list1.size() != list2.size()) {
            return false; // Different lengths, cannot be equal
        }

        // Compare each customization and its selection status
        for (CustomizationWithBoolean custom1 : list1) {
            boolean foundMatch = false;
            for (CustomizationWithBoolean custom2 : list2) {
                if (custom1.getCustomization().getId() == custom2.getCustomization().getId() &&
                        custom1.getValue().equals(custom2.getValue())) {
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                return false; // If any customization doesn't match, return false
            }
        }
        return true;
    }

    public void addMealToCart(MealInTheCart meal) {
        Platform.runLater(() -> {
            // Create a new meal row (HBox) with internal padding
            HBox mealRow = new HBox(20);
            mealRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10;");
            mealRow.setPadding(new Insets(10));

            // Meal Image
            ImageView mealImage = new ImageView();
            if (meal.getMeal().getMeal().getImage() != null) {
                Image image = new Image(new ByteArrayInputStream(meal.getMeal().getMeal().getImage()));
                mealImage.setImage(image);
            } else {
                System.err.println("Image data is null for the meal.");
            }
            mealImage.setFitHeight(80);
            mealImage.setFitWidth(80);

            // Meal Details
            VBox detailsBox = new VBox(5);
            Label nameLabel = new Label(meal.getMeal().getMeal().getName());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Label descriptionLabel = new Label(meal.getMeal().getMeal().getDescription());
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");

            // Get unselected customizations
            String withoutCustomizations = meal.getMeal().getCustomizationsList().stream()
                    .filter(customizationWithBoolean -> !customizationWithBoolean.getValue())
                    .map(customizationWithBoolean -> "Without " + customizationWithBoolean.getCustomization().getName())
                    .collect(Collectors.joining(", "));

            if (!withoutCustomizations.isEmpty()) {
                Label withoutLabel = new Label(withoutCustomizations);
                withoutLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000000;");
                detailsBox.getChildren().addAll(nameLabel, descriptionLabel, withoutLabel);
            } else {
                detailsBox.getChildren().addAll(nameLabel, descriptionLabel);
            }

            // Price Display - Handle discounted meals
            double discount = meal.getMeal().getMeal().getDiscount_percentage();
            double price = meal.getMeal().getMeal().getPrice();

            // Create price container
            HBox priceContainer = new HBox(10);
            priceContainer.setAlignment(Pos.CENTER_LEFT);

            if (discount > 0) {
                double discountedPrice = price * (1 - discount / 100);

                // Original price with strikethrough - Using Text for reliable strikethrough
                Text originalPriceText = new Text(String.format("₪ %.2f", price));
                originalPriceText.setStyle("-fx-strikethrough: true; -fx-font-size: 14px; -fx-fill: #999999;");

                // Wrap in StackPane for proper layout
                StackPane originalPricePane = new StackPane(originalPriceText);
                originalPricePane.setAlignment(Pos.CENTER_LEFT);

                // Discounted price
                Label discountedPriceLabel = new Label(String.format("₪ %.2f", discountedPrice));
                discountedPriceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");

                // Discount badge
                Label discountBadge = new Label(discount + "% OFF");
                discountBadge.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white; " +
                        "-fx-background-color: #fe3b30; -fx-background-radius: 5; -fx-padding: 2 5;");

                priceContainer.getChildren().addAll(originalPricePane, discountedPriceLabel, discountBadge);
            } else {
                // Regular price
                Label priceLabel = new Label(String.format("₪ %.2f", price));
                priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");
                priceContainer.getChildren().add(priceLabel);
            }

            // Quantity Control
            HBox quantityBox = new HBox(5);
            Button minusButton = new Button("-");
            TextField quantityField = new TextField(String.valueOf(meal.getQuantity()));
            Button plusButton = new Button("+");

            // Style for buttons
            minusButton.setStyle("-fx-background-color: #cd0338; -fx-text-fill: white; -fx-background-radius: 5;");
            plusButton.setStyle("-fx-background-color: #cd0338; -fx-text-fill: white; -fx-background-radius: 5;");

            // Configure quantity field
            quantityField.setPrefWidth(40);
            quantityField.setMaxWidth(40);
            quantityField.setStyle("-fx-alignment: center;");

            // Quantity validation
            quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    quantityField.setText(oldValue);
                } else if (!newValue.isEmpty() && Integer.parseInt(newValue) <= 0) {
                    quantityField.setText("1");
                }
                updateTotalPrice();
                meal.setQuantity(Integer.parseInt(quantityField.getText()));
            });

            // Quantity buttons
            minusButton.setOnAction(event -> {
                int currentQty = Integer.parseInt(quantityField.getText());
                if (currentQty > 1) {
                    quantityField.setText(String.valueOf(currentQty - 1));
                    meal.setQuantity(currentQty - 1);
                }
                updateTotalPrice();
            });

            plusButton.setOnAction(event -> {
                int currentQty = Integer.parseInt(quantityField.getText());
                quantityField.setText(String.valueOf(currentQty + 1));
                meal.setQuantity(currentQty + 1);
                updateTotalPrice();
            });

            quantityBox.getChildren().addAll(minusButton, quantityField, plusButton);

            // Delete Button
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #cd0338; -fx-text-fill: white; -fx-background-radius: 5;");
            deleteButton.setOnAction(event -> {
                playDeleteSound();
                cartItemsContainer.getChildren().remove(mealRow);
                listOfMeals.remove(meal);
                numberOfMeals--;
                mealsNum.setText("(" + numberOfMeals + ")");
                updateTotalPrice();
                if (numberOfMeals == 0) {
                    emptyCart();
                }
            });

            // Add components to mealRow
            mealRow.getChildren().addAll(mealImage, detailsBox, priceContainer, quantityBox, deleteButton);

            // Add spacing between items
            Region spacer = new Region();
            spacer.setPrefHeight(10);

            // Add to container
            cartItemsContainer.getChildren().addAll(mealRow, spacer);
            mealRowMap.put(meal.getMeal().getMeal().getId(), mealRow);

            // Update UI
            updateTotalPrice();
            emptyCartPane.setVisible(false);
        });
    }


    // Play the cart sound effect using MediaPlayer
    private void playDeleteSound() {
        try {
            // Create a Media object for the sound file
            String soundPath = getClass().getResource("/images/delete.mp3").toString();
            Media sound = new Media(soundPath);

            // Create a MediaPlayer for playing the sound
            MediaPlayer mediaPlayer = new MediaPlayer(sound);

            // Set the starting time (in seconds)
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.seek(Duration.seconds(0.9)); // Start the sound from 3 seconds
                mediaPlayer.play();
            });

            // Optional: Adjust volume (0.0 to 1.0)
            mediaPlayer.setVolume(0.5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotalPrice() {
        double total = 0.0;

        for (MealInTheCart meal : listOfMeals) {
            // Get price directly from meal object
            double price = meal.getMeal().getMeal().getPrice();
            double finalPrice = (1 - (double) meal.getMeal().getMeal().getDiscount_percentage() / 100) * price;
            int quantity = meal.getQuantity();
            total += finalPrice * quantity;
        }

        // Update the total price label
        totalPriceLabel.setText(String.format("Total: %.2f₪", total));

        // Show/hide empty cart message
        emptyCartPane.setVisible(listOfMeals.isEmpty());
    }


    private void emptyCart(){
        clearCartButton.setDisable(true);
        clearCartButton.setVisible(false);
        emptyCartPane.setVisible(true);
        emptyCartPane.setManaged(true);
        emptyCartImage.setImage(new Image(getClass().getResourceAsStream("/images/emptycart.png")));
        totalPriceLabel.setVisible(false);
        continueButton.setText("Explore");
        continueButton.setOnAction(event -> {
            try {
                System.out.println("Navigating to menu page..."); // Debug statement
                App.setRoot("menu");
            } catch (IOException e) {
                System.err.println("Failed to load the menu page: " + e.getMessage()); // Debug statement
                e.printStackTrace(); // Print the full stack trace
                // Show an alert to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Navigation Error");
                alert.setHeaderText("Failed to load the menu page.");
                alert.setContentText("Please check if the menu.fxml file exists and is valid.");
                alert.showAndWait();
            }
        });
    }
    @FXML
    public void initialize(){


        emptyCartPane.setVisible(false);
        emptyCartPane.setMouseTransparent(true); // Let clicks pass through
        mealsNum.setText("("+numberOfMeals+")");
        if(numberOfMeals == 0) {
            emptyCart();
        }
        else {
            clearCartButton.setDisable(false);
            clearCartButton.setVisible(true);
            continueButton.setOnAction(event -> openOrderSummary());
            for(MealInTheCart meal : listOfMeals)
            {
                addMealToCart(meal);
            }
        }


    }
    private void openOrderSummary() {
        StringBuilder orderDetails = new StringBuilder();
        double totalAmount = 0.0;

        for (MealInTheCart meal : listOfMeals) {
            double price = meal.getMeal().getMeal().getPrice();
            double discount = meal.getMeal().getMeal().getDiscount_percentage();
            double finalPrice = discount > 0 ? price * (1 - discount / 100) : price;
            totalAmount += finalPrice * meal.getQuantity();
            orderDetails.append("\n");
        }

        String totalAmountText = "Total: " + String.format("%.2f₪", totalAmount);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryWindow.fxml"));
            Stage summaryStage = new Stage();
            Scene scene = new Scene(loader.load());
            OrderSummaryController controller = loader.getController();

            controller.setSummary(summaryStage, orderDetails.toString(), totalAmountText);
            controller.setMeals(listOfMeals);

            VBox mealDetailsContainer = controller.getMealDetailsContainer();

            for (MealInTheCart meal : listOfMeals) {
                HBox mealRow = new HBox(10);
                mealRow.setSpacing(10);

                // Meal Image
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

                // Meal Info Container
                VBox mealInfoContainer = new VBox(5);

                // Meal Name and Quantity
                HBox nameQuantityBox = new HBox(5);
                Text mealName = new Text(meal.getMeal().getMeal().getName() + " - ");
                Text boldX = new Text("X");
                boldX.setStyle("-fx-font-weight: bold;");
                Text quantity = new Text(String.valueOf(meal.getQuantity()));
                quantity.setStyle("-fx-font-weight: bold;");
                nameQuantityBox.getChildren().addAll(mealName, boldX, quantity);

                // Price Display
                double price = meal.getMeal().getMeal().getPrice();
                double discount = meal.getMeal().getMeal().getDiscount_percentage();
                Node priceDisplay;

                if (discount > 0) {
                    VBox priceBox = new VBox(2);
                    double discountedPrice = price * (1 - discount / 100);

                    // Original price with strikethrough
                    Text originalPrice = new Text(String.format("Original: %.2f₪", price));
                    originalPrice.setStyle("-fx-strikethrough: true; -fx-fill: #999999;");

                    // Discounted price
                    Text discountedPriceText = new Text(String.format("Price: %.2f₪", discountedPrice));
                    discountedPriceText.setStyle("-fx-font-weight: bold; -fx-fill: #b70236;");

                    // Discount badge
                    Text discountBadge = new Text(String.format("(%d%% OFF)", (int)discount));
                    discountBadge.setStyle("-fx-font-weight: bold; -fx-fill: #fe3b30;");

                    priceBox.getChildren().addAll(originalPrice, discountedPriceText, discountBadge);
                    priceDisplay = priceBox;
                } else {
                    Text priceText = new Text(String.format("Price: %.2f₪", price));
                    priceText.setStyle("-fx-font-weight: bold;");
                    priceDisplay = priceText;
                }

                // Description
                TextFlow descriptionFlow = new TextFlow();
                if (meal.getMeal().getMeal().getDescription() != null && !meal.getMeal().getMeal().getDescription().isEmpty()) {
                    Text description = new Text(meal.getMeal().getMeal().getDescription() + "\n");
                    descriptionFlow.getChildren().add(description);
                }

                // Customizations
                if (meal.getMeal().getCustomizationsList() != null && !meal.getMeal().getCustomizationsList().isEmpty()) {
                    Text customizationsTitle = new Text("Customizations:\n");
                    customizationsTitle.setStyle("-fx-font-weight: bold;");
                    descriptionFlow.getChildren().add(customizationsTitle);

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

                        checkImageView.setOnMouseClicked(event -> {
                            boolean newValue = !customWithBool.getValue();
                            customWithBool.setValue(newValue);
                            checkImageView.setImage(newValue ? checkedImage : uncheckedImage);
                        });

                        descriptionFlow.getChildren().addAll(checkImageView, customText, new Text("\n"));
                    }
                }

                mealInfoContainer.getChildren().addAll(nameQuantityBox, priceDisplay, descriptionFlow);
                mealRow.getChildren().add(mealInfoContainer);
                mealDetailsContainer.getChildren().add(mealRow);
            }

            Stage mainStage = (Stage) stackPane.getScene().getWindow();
            ColorAdjust blur = new ColorAdjust();
            blur.setBrightness(-0.7);
            mainStage.getScene().getRoot().setEffect(blur);

            summaryStage.initModality(Modality.APPLICATION_MODAL);
            summaryStage.initOwner(mainStage);

            summaryStage.setOnHiding(event -> {
                mainStage.getScene().getRoot().setEffect(null);
                updateCart();
            });

            mode = "Order";
            summaryStage.setScene(scene);
            summaryStage.setTitle("Order Summary");
            summaryStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @FXML
    private void clearCart() {
        cartItemsContainer.getChildren().clear();
        emptyCart();
        mealsNum.setText("(0)");
        listOfMeals.clear();
        numberOfMeals=0;
        updateTotalPrice();
    }







    @FXML
    private void backToHome() {
        try {
            App.setRoot("menu");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
