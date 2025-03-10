package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.geometry.Insets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class menu_controller {

    List<mealEvent> meals;
    public static String branchName;

    // Menu-related fields
    @FXML
    private VBox menuContainer; // Links to fx:id in FXML
    private Map<String, Label> mealPriceLabels = new HashMap<>();
    @FXML
    private ImageView scrollArrow;
    @FXML
    private ImageView loadingGif;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TranslateTransition arrowAnimation;

    @FXML
    private StackPane stackPane; // Inject the AnchorPane from the FXML file

    @FXML
    private Button Search_combo_box;
    @FXML
    private Button Reset_Button;

    @FXML
    private Label textmenu;

    // Search-related fields (moved from SearchByController)

    @FXML
    private Label Error_Label; // Label for error messages

    private List<String> restaurantNames;
    private List<String> customizationNames;

    @FXML
    void Reset_Menu(ActionEvent event) throws IOException {
        startLoading();
        List<String> list = new ArrayList<>();
        list.add(branchName);
        SimpleClient.getClient().sendToServer(new SearchOptions(list,branchName));
    }

    @FXML
    void Sort_meals(ActionEvent event) {
        // Show the search options UI
        createDynamicCheckBoxes(); // Populate the checkboxes
    }

    @FXML
    void Apply_filter(ActionEvent event) throws IOException {
        Platform.runLater(()->{
            startLoading();
        });
        List<String> selectedRestaurants = new ArrayList<>(); // List for selected restaurants
        List<String> selectedCustomizations = new ArrayList<>(); // List for selected customizations

        // If no filters are selected, show an error message
        if (selectedRestaurants.isEmpty() && selectedCustomizations.isEmpty()) {
            showAlert("Error", "Please select at least one filter.");
        } else {
            // Display the selected filters
            System.out.println("Selected Restaurants: " + selectedRestaurants);
            System.out.println("Selected Customizations: " + selectedCustomizations);

            // Create a new SearchOptions object with separate restaurant and customization filters
            SearchOptions searchOptions = new SearchOptions(selectedRestaurants, selectedCustomizations, branchName);

            // Send the SearchOptions object to the server
            SimpleClient.getClient().sendToServer(searchOptions);

        }
    }

    @Subscribe
    public void putSearchOptions(SearchOptions options) {
        // Handle the event sent from the server
        restaurantNames = options.getRestaurantNames();
        customizationNames = options.getCustomizationNames();
        createDynamicCheckBoxes();
        stopLoading();
    }

    private void createDynamicCheckBoxes() {
        Platform.runLater(() -> {
            startLoading();
            if (restaurantNames != null && !restaurantNames.isEmpty()) {
                for (String option : restaurantNames) {
                    // Add a tag "restaurant" to each restaurant checkbox
                    CheckBox checkBox = new CheckBox(option);
                    checkBox.setUserData("restaurant");  // Mark checkbox as restaurant
                }
            } else {
                showAlert("Error", "No restaurants available.");
            }

            if (customizationNames != null && !customizationNames.isEmpty()) {
                for (String option : customizationNames) {
                    // Add a tag "customization" to each customization checkbox
                    CheckBox checkBox = new CheckBox(option);
                    checkBox.setUserData("customization");  // Mark checkbox as customization
                }
            } else {
                showAlert("Error", "No customizations available.");
            }
            stopLoading();
        });


    }

    private void showAlert(String title, String message) {
        startLoading();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        stopLoading();
    }

    @FXML
    void backToHome(ActionEvent event) throws IOException {
        stopLoading();
        App.setRoot("RestaurantList");
    }

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

    private void openChangePricePage(String mealName, Label priceLabel,String Id){
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

    @FXML
    public void initialize() throws Exception {
        EventBus.getDefault().register(this);
        SimpleClient client = SimpleClient.getClient();
        System.out.println("sending menu to "+branchName);
        // Load the Loading.gif image
        loadingGif.setImage(new Image(getClass().getResourceAsStream("/images/Loading.gif")));
        startLoading();
        client.sendToServer("menu"+branchName);

        if(meals == null)
            System.out.println("No meals found");
        else
            for (mealEvent meal : meals) {
                onAddMealClicked(meal.getMealName(), meal.getMealDisc(), String.valueOf(meal.getPrice()),meal.getId(),meal.getImage());
            }

        try {
            SimpleClient.getClient().sendToServer("Fetching SearchBy Options");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        textmenu.setText("Menu of "+branchName);

        /******/
        // Set the arrow image
        scrollArrow.setImage(new Image(getClass().getResourceAsStream("/images/downarrow.png"))); // Update path as needed

        // Position the arrow
        //scrollArrow.setTranslateY(20); // Adjust to place it in the middle-bottom area
        //scrollArrow.setTranslateX(0); // Adjust to place it in the middle-bottom area
        scrollArrow.setVisible(true);
        //scrollArrow.set

        // Create arrow animation
        arrowAnimation = new TranslateTransition(Duration.millis(500), scrollArrow);
        arrowAnimation.setFromY(0);
        arrowAnimation.setToY(30); // Move down by 10px
        arrowAnimation.setCycleCount(TranslateTransition.INDEFINITE);
        arrowAnimation.setAutoReverse(true);
        arrowAnimation.play();

        // Simulate loading delay (or replace this with actual loading logic)
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Simulate a 3-second loading delay
                Platform.runLater(() -> {
                    loadingGif.setVisible(false); // Hide the loading GIF
                    scrollPane.setVisible(true); // Show the main content
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        // Add scroll listener to hide the arrow when fully scrolled
        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                arrowAnimation.stop();
                scrollArrow.setVisible(false); // Hide the arrow when fully scrolled
            } else {
                arrowAnimation.play();
                scrollArrow.setVisible(true); // Show the arrow when not fully scrolled
            }
        });


        /*****/
    }

    @Subscribe
    public void Getmeals(MealsList avMeals) {

        Platform.runLater(() -> {
            menuContainer.getChildren().removeIf(node -> {
                return node instanceof HBox && node != menuContainer.getChildren().get(0);
            });

            // Add new meals to the menu
            if (avMeals.getMeals() != null && !avMeals.getMeals().isEmpty()) {
                for (Meal meal : avMeals.getMeals()) {
                    onAddMealClicked(
                            meal.getName(),
                            meal.getDescription(),
                            String.valueOf(meal.getPrice()),
                            String.valueOf(meal.getId()),
                            meal.getImage()
                    );
                }
            } else {
                System.out.println("No new meals to display.");
            }
            stopLoading();
        });
    }

    @Subscribe
    public void addnewmeal(mealEvent meal) {
        System.out.println("adding new meal for this client");
        System.out.println("Meal's id is " + meal.getId());
        Platform.runLater(() -> {
            onAddMealClicked(meal.getMealName(), meal.getMealDisc(), String.valueOf(meal.getPrice()), meal.getId(), meal.getImage());
        });
        System.out.println("added the new meal for this client");

    }

    @Subscribe
    public void updateMealPrice(updatePrice updatePrice) {
        //System.out.println("changing price now!");
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
    void showSearchOptionsDialog(ActionEvent event) {
        startLoading();
        // Create a new Dialog
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Options");

        // Set a minimum width for the dialog
        dialog.getDialogPane().setMinWidth(400); // Adjust the width as needed

        // Create a VBox to hold the search options
        VBox dialogContent = new VBox(10);
        dialogContent.setPadding(new Insets(20));

        // Add checkboxes for restaurants
        if (restaurantNames != null && !restaurantNames.isEmpty()) {
            for (String option : restaurantNames) {
                CheckBox checkBox = new CheckBox(option);
                checkBox.setUserData("restaurant"); // Mark checkbox as restaurant
                checkBox.setWrapText(true); // Allow text to wrap
                dialogContent.getChildren().add(checkBox);
            }
        } else {
            dialogContent.getChildren().add(new Label("No restaurants available."));
        }

        // Add checkboxes for customizations
        if (customizationNames != null && !customizationNames.isEmpty()) {
            for (String option : customizationNames) {
                CheckBox checkBox = new CheckBox(option);
                checkBox.setUserData("customization"); // Mark checkbox as customization
                checkBox.setWrapText(true); // Allow text to wrap
                dialogContent.getChildren().add(checkBox);
            }
        } else {
            dialogContent.getChildren().add(new Label("No customizations available."));
        }

        // Add a ScrollPane to make the content scrollable
        ScrollPane scrollPane = new ScrollPane(dialogContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200); // Set a fixed height for the scrollable area

        // Add the ScrollPane to the Dialog
        dialog.getDialogPane().setContent(scrollPane);

        // Add an "Apply Filter" button
        ButtonType applyFilterButtonType = new ButtonType("Apply Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(applyFilterButtonType);

        // Handle the "Apply Filter" button click
        dialog.setResultConverter(buttonType -> {
            if (buttonType == applyFilterButtonType) {
                // Collect selected filters
                List<String> selectedRestaurants = new ArrayList<>();
                List<String> selectedCustomizations = new ArrayList<>();

                for (javafx.scene.Node node : dialogContent.getChildren()) {
                    if (node instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) node;
                        if (checkBox.isSelected()) {
                            if ("restaurant".equals(checkBox.getUserData())) {
                                selectedRestaurants.add(checkBox.getText());
                            } else if ("customization".equals(checkBox.getUserData())) {
                                selectedCustomizations.add(checkBox.getText());
                            }
                        }
                    }
                }

                // Send the selected filters to the server
                SearchOptions searchOptions = new SearchOptions(selectedRestaurants, selectedCustomizations, branchName);
                try {
                    SimpleClient.getClient().sendToServer(searchOptions);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });

        // Show the Dialog
        dialog.showAndWait();
        stopLoading();
    }

    private void startLoading() {
        Platform.runLater(() -> {
            loadingGif.setVisible(true);
            stackPane.setDisable(true); // Disable all UI components
        });
    }

    // Hide loading animation and enable UI
    private void stopLoading() {
        Platform.runLater(() -> {
            loadingGif.setVisible(false);
            stackPane.setDisable(false); // Enable all UI components
        });
    }
}