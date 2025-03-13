package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.MealInTheCart;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartPageController {

    public static List<MealInTheCart> listOfMeals = new ArrayList<>();
    public static int numberOfMeals = 0;

    @FXML
    private Button backButton;

    @FXML
    private Label cartTitle;

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

    public void addMealToCart(MealInTheCart meal) {
        Platform.runLater(() -> {
            // Create a new meal row (HBox) with internal padding
            HBox mealRow = new HBox(20);
            mealRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10;");
            mealRow.setPadding(new Insets(10)); // Add padding inside the HBox (top, right, bottom, left)

            // Meal Image
            //ImageView mealImage = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            ImageView mealImage = new ImageView();

            if (meal.getMeal().getImage() != null) {
                Image image = new Image(new ByteArrayInputStream(meal.getMeal().getImage()));
                mealImage = new ImageView(image);
                // Add imageView to your UI
            } else {
                System.err.println("Image data is null for the meal.");
                // Handle the case where there is no image
            }
            mealImage.setFitHeight(80);
            mealImage.setFitWidth(80);

            // Meal Details
            VBox detailsBox = new VBox(5);
            Label nameLabel = new Label(meal.getMeal().getName());
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label descriptionLabel = new Label(meal.getMeal().getDescription());
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            detailsBox.getChildren().addAll(nameLabel, descriptionLabel);

            // Meal Price
            Label priceLabel = new Label(meal.getMeal().getPrice() + "₪");
            priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");

            // Quantity Control
            HBox quantityBox = new HBox(5);
            Button minusButton = new Button("-");
            TextField quantityField = new TextField(String.valueOf(meal.getQuantity()));
            Button plusButton = new Button("+");

            // Style for buttons
            minusButton.setStyle("-fx-background-color: #cd0338; -fx-text-fill: white; -fx-background-radius: 5;");
            plusButton.setStyle("-fx-background-color: #cd0338; -fx-text-fill: white; -fx-background-radius: 5;");

            // Make the quantityField smaller
            quantityField.setPrefWidth(40);
            quantityField.setMaxWidth(40);
            quantityField.setStyle("-fx-alignment: center;");

            // Restrict quantityField to positive numbers only
            quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) { // Check if input is a number
                    quantityField.setText(oldValue); // Revert to last valid value
                } else if (!newValue.isEmpty() && Integer.parseInt(newValue) <= 0) {
                    quantityField.setText("1"); // Enforce positive numbers only
                }
                updateTotalPrice(); // Update total price when quantity changes
            });

            // Decrease quantity
            minusButton.setOnAction(event -> {
                int currentQty = Integer.parseInt(quantityField.getText());
                if (currentQty > 1) {
                    quantityField.setText(String.valueOf(currentQty - 1));
                    updateTotalPrice(); // Update total price when quantity changes
                    meal.setQuantity(currentQty - 1);
                }
            });

            // Increase quantity
            plusButton.setOnAction(event -> {
                int currentQty = Integer.parseInt(quantityField.getText());
                quantityField.setText(String.valueOf(currentQty + 1));
                updateTotalPrice(); // Update total price when quantity changes
                meal.setQuantity(currentQty + 1);
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
                mealsNum.setText("("+numberOfMeals+")");
                updateTotalPrice(); // Update total price when a meal is deleted
                if(numberOfMeals == 0) {
                    emptyCart();
                }
            });

            // Add components to mealRow
            mealRow.getChildren().addAll(mealImage, detailsBox, priceLabel, quantityBox, deleteButton);

            // Add a transparent spacer to simulate the gap (keeps the gray background)
            Region spacer = new Region();
            spacer.setPrefHeight(10); // Adjust height for spacing between rows

            // Add mealRow and spacer to cartContainer
            cartItemsContainer.getChildren().addAll(mealRow, spacer);

            mealRowMap.put(meal.getMeal().getId(), mealRow);


            // Update total price initially
            updateTotalPrice();

            // Hide the empty cart image if meals are added
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

    // Method to calculate and update the total price
    // Method to calculate and update the total price
    private void updateTotalPrice() {
        double total = 0;
        for (Node node : cartItemsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox mealRow = (HBox) node;
                // Extract price and quantity from the meal row
                Label priceLabel = (Label) mealRow.getChildren().get(2); // Price label is at index 2
                TextField quantityField = (TextField) ((HBox) mealRow.getChildren().get(3)).getChildren().get(1); // Quantity field is at index 3

                double price = Double.parseDouble(priceLabel.getText().replace("₪", ""));
                int quantity = Integer.parseInt(quantityField.getText());
                total += price * quantity;
            }
        }

        // Update the total price label
        totalPriceLabel.setText(String.format("Total: %.2f₪", total));

        // Show the empty cart image if the cart is empty
        if (cartItemsContainer.getChildren().isEmpty()) {
            emptyCartPane.setVisible(true);
        } else {
            emptyCartPane.setVisible(false);
        }
    }


    private void emptyCart(){
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
        if(numberOfMeals == 0)
            emptyCart();
        else {
            continueButton.setOnAction(event -> openOrderSummary());
            for(MealInTheCart meal : listOfMeals)
            {
                addMealToCart(meal);
            }
        }
    }
    private void openOrderSummary() {
        // Initialize order details string
        StringBuilder orderDetails = new StringBuilder();
        double totalAmount = 0.0;  // Variable to store the total amount

        // Loop through listOfMeals to gather details
        for (MealInTheCart meal : listOfMeals) {
            totalAmount += meal.getMeal().getPrice() * meal.getQuantity();
            orderDetails.append("\n");
        }

        // Format the total price for display
        String totalAmountText = "Total: " + totalAmount + "₪";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SummaryWindow.fxml"));
            Stage summaryStage = new Stage();
            Scene scene = new Scene(loader.load());
            OrderSummaryController controller = loader.getController();

            // Pass the meal details and total amount to the summary controller
            controller.setSummary(summaryStage, orderDetails.toString(), totalAmountText);

            // Get the VBox where meal details should be added
            VBox mealDetailsContainer = controller.getMealDetailsContainer();

            for (MealInTheCart meal : listOfMeals) {
                // Create a new HBox to hold the meal image and details
                HBox mealRow = new HBox();
                mealRow.setSpacing(10);

                // Convert byte[] to Image for the meal image
                byte[] imageBytes = meal.getMeal().getImage();
                if (imageBytes != null && imageBytes.length > 0) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                    Image mealImage = new Image(byteArrayInputStream);

                    // Create ImageView
                    ImageView imageView = new ImageView(mealImage);
                    imageView.setFitHeight(100);
                    imageView.setFitWidth(100);
                    imageView.setPreserveRatio(true);

                    // Add rounded corners
                    Rectangle clip = new Rectangle(100, 100);
                    clip.setArcWidth(20);
                    clip.setArcHeight(20);
                    imageView.setClip(clip);

                    // Add image to mealRow
                    mealRow.getChildren().add(imageView);
                }

                // 📝 Create a TextFlow to allow different text styles
                TextFlow mealInfoTextFlow = new TextFlow();

                // 🔹 First Line: Name and Bold "X" + Quantity
                Text mealName = new Text(meal.getMeal().getName() + " - ");
                Text boldX = new Text("X");  // Bold "X"
                boldX.setStyle("-fx-font-weight: bold;");
                Text quantity = new Text(String.valueOf(meal.getQuantity())); // Quantity in bold
                quantity.setStyle("-fx-font-weight: bold;");

                mealInfoTextFlow.getChildren().addAll(mealName, boldX, quantity, new Text("\n"));

                // 🔹 Second Line: Description
                if (meal.getMeal().getDescription() != null && !meal.getMeal().getDescription().isEmpty()) {
                    Text description = new Text(meal.getMeal().getDescription() + "\n");
                    mealInfoTextFlow.getChildren().add(description);
                }

                // 🔹 Third Line: Customizations
                if (meal.getMeal().getCustomizations() != null && !meal.getMeal().getCustomizations().isEmpty()) {
                    Text customizationsTitle = new Text("Customizations:\n");
                    customizationsTitle.setStyle("-fx-font-weight: bold;");

                    String customizationsList = meal.getMeal().getCustomizations().stream()
                            .map(Customization::getName)
                            .collect(Collectors.joining("\n")); // Each customization on a new line

                    Text customizations = new Text(customizationsList + "\n");

                    mealInfoTextFlow.getChildren().addAll(customizationsTitle, customizations);
                }

                // Add mealInfoTextFlow to mealRow
                mealRow.getChildren().add(mealInfoTextFlow);

                // Add mealRow to mealDetailsContainer
                mealDetailsContainer.getChildren().add(mealRow);
            }

            // Apply blur effect on the main window
            Stage mainStage = (Stage) stackPane.getScene().getWindow(); // Assuming you have a stackPane reference
            ColorAdjust blur = new ColorAdjust();
            blur.setBrightness(-0.7); // Darken the main window
            mainStage.getScene().getRoot().setEffect(blur);

            // Make the summary window modal (disables interaction with main window)
            summaryStage.initModality(Modality.APPLICATION_MODAL);
            summaryStage.initOwner(mainStage);

            // Remove blur effect when the summary window is closed
            summaryStage.setOnHiding(event -> mainStage.getScene().getRoot().setEffect(null));

            // Show summary window
            summaryStage.setScene(scene);
            summaryStage.setTitle("Order Summary");
            summaryStage.showAndWait(); // Wait until the summary window is closed

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
