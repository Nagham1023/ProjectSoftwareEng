//package il.cshaifasweng.OCSFMediatorExample.client;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.*;
//import javafx.animation.PauseTransition;
//import javafx.animation.TranslateTransition;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.Button;
//import javafx.scene.control.Dialog;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.effect.ColorAdjust;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//import javafx.util.Duration;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import javafx.geometry.Insets;
//import org.hibernate.Hibernate;
//import org.hibernate.Session;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.animation.PauseTransition;
//import javafx.util.Duration;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.util.*;
//
//import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.*;
//
//public class menu_controller {
//
//    List<mealEvent> meals;
//    public static String branchName;
//
//
//    @FXML
//    private ImageView cartIcon1;
//    // Menu-related fields
//    @FXML
//    private VBox menuContainer; // Links to fx:id in FXML
//
//    private Map<String, Button> addToCartButtons = new HashMap<>();
//
//    @FXML
//    private ImageView scrollArrow;
//    @FXML
//    private ImageView cartIcon;
//    @FXML
//    private ScrollPane scrollPane;
//    @FXML
//    private TranslateTransition arrowAnimation;
//
//    @FXML
//    private StackPane stackPane; // Inject the AnchorPane from the FXML file
//
//    @FXML
//    private Label textmenu;
//
//    private List<String> restaurantNames;
//    private List<String> customizationNames;
//
//    @FXML
//    private Label cartItemCount;
//
//    @FXML
//    void Reset_Menu(ActionEvent event) throws IOException {
//        //startLoading();
//        List<String> list = new ArrayList<>();
//        list.add(branchName);
//        SimpleClient.getClient().sendToServer(new SearchOptions(list,branchName));
//    }
//
//    @FXML
//    void Sort_meals(ActionEvent event) {
//        // Show the search options UI
//        createDynamicCheckBoxes(); // Populate the checkboxes
//    }
//
//    @FXML
//    void Apply_filter(ActionEvent event) throws IOException {
//        /*Platform.runLater(()->{
//            startLoading();
//        });*/
//        List<String> selectedRestaurants = new ArrayList<>(); // List for selected restaurants
//        List<String> selectedCustomizations = new ArrayList<>(); // List for selected customizations
//
//        // If no filters are selected, show an error message
//        if (selectedRestaurants.isEmpty() && selectedCustomizations.isEmpty()) {
//            showAlert("Error", "Please select at least one filter.");
//        } else {
//            // Display the selected filters
//            System.out.println("Selected Restaurants: " + selectedRestaurants);
//            System.out.println("Selected Customizations: " + selectedCustomizations);
//
//            // Create a new SearchOptions object with separate restaurant and customization filters
//            SearchOptions searchOptions = new SearchOptions(selectedRestaurants, selectedCustomizations, branchName);
//
//            // Send the SearchOptions object to the server
//            SimpleClient.getClient().sendToServer(searchOptions);
//
//        }
//    }
//
//    @Subscribe
//    public void putSearchOptions(SearchOptions options) {
//        // Handle the event sent from the server
//        restaurantNames = options.getRestaurantNames();
//        customizationNames = options.getCustomizationNames();
//        createDynamicCheckBoxes();
//        //stopLoading();
//    }
//
//    private void createDynamicCheckBoxes() {
//        Platform.runLater(() -> {
//            //startLoading();
//            if (restaurantNames != null && !restaurantNames.isEmpty()) {
//                for (String option : restaurantNames) {
//                    // Add a tag "restaurant" to each restaurant checkbox
//                    CheckBox checkBox = new CheckBox(option);
//                    checkBox.setUserData("restaurant");  // Mark checkbox as restaurant
//                }
//            } else {
//                showAlert("Error", "No restaurants available.");
//            }
//
//            if (customizationNames != null && !customizationNames.isEmpty()) {
//                for (String option : customizationNames) {
//                    // Add a tag "customization" to each customization checkbox
//                    CheckBox checkBox = new CheckBox(option);
//                    checkBox.setUserData("customization");  // Mark checkbox as customization
//                }
//            } else {
//                showAlert("Error", "No customizations available.");
//            }
//            //stopLoading();
//        });
//
//
//    }
//
//    private void showAlert(String title, String message) {
//        //startLoading();
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//        //stopLoading();
//    }
//
//
//
//    @FXML
//    void backToHome(ActionEvent event) throws IOException {
//        //stopLoading();
//        App.setRoot("RestaurantList");
//    }
//
//
//    private void openMealPopup(Meal meal) {
//        try {
//            // Load popup FXML
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("meal_popup.fxml"));
//            Parent root = loader.load();
//
//            // Get the controller and set meal details
//            meal_popup_controller popupController = loader.getController();
//            popupController.setMealDetails(
//                    meal.getName(),
//                    meal.getDescription(),
//                    meal.getPrice(),
//                    meal.getImage(),
//                    meal.getCustomizations(),
//                    meal,
//                    this::updateCart
//            );
//
//
//            Stage mainStage = (Stage) stackPane.getScene().getWindow();
//            ColorAdjust blur = new ColorAdjust();
//            blur.setBrightness(-0.7);  // Simulate blur effect
//            mainStage.getScene().getRoot().setEffect(blur);
//
//
//            // Create a new popup stage (modal window)
//            Stage popupStage = new Stage();
//            popupStage.setTitle("Meal Details");
//            popupStage.setScene(new Scene(root));
//
//            // Make the popup window undecorated (no frame) BEFORE it is shown
//            popupStage.initStyle(StageStyle.UTILITY); // This must be done before showing the window
//
//            // Make popup modal (disable interaction with main window)
//            popupStage.initModality(Modality.APPLICATION_MODAL);
//            popupStage.initOwner(mainStage); // Link popup to the main window
//
//            // Remove blur effect when popup is closed
//            popupStage.setOnHiding(event -> mainStage.getScene().getRoot().setEffect(null));
//
//            // Show popup
//            popupStage.showAndWait();  // Wait until the popup is closed
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateCart() {
//        // Iterate through the list of meals to check for duplicates
//        for (int i = 0; i < listOfMeals.size(); i++) {
//            MealInTheCart currentMeal = listOfMeals.get(i);
//
//            // Check if this meal has already been merged with another
//            for (int j = i + 1; j < listOfMeals.size(); j++) {
//                MealInTheCart nextMeal = listOfMeals.get(j);
//
//                // If both meals have the same ID and identical customizations, merge them
//                if (currentMeal.getMeal().getMeal().getId() == nextMeal.getMeal().getMeal().getId() &&
//                        areCustomizationsEqual(currentMeal.getMeal().getCustomizationsList(), nextMeal.getMeal().getCustomizationsList())) {
//
//                    // Merge quantities
//                    currentMeal.setQuantity(currentMeal.getQuantity() + nextMeal.getQuantity());
//
//                    // Remove the duplicate meal
//                    listOfMeals.remove(j);
//                    j--; // Adjust the index to account for the removed item
//                }
//            }
//        }
//
//        // Update the cart badge if the number of meals has changed
//        if (numberOfMeals != listOfMeals.size()) {
//            numberOfMeals = listOfMeals.size();
//            updateCartBadge();
//        }
//    }
//
//
//    private boolean areCustomizationsEqual(List<CustomizationWithBoolean> list1, List<CustomizationWithBoolean> list2) {
//        if (list1.size() != list2.size()) {
//            return false; // Different lengths, cannot be equal
//        }
//
//        // Compare each customization and its selection status
//        for (CustomizationWithBoolean custom1 : list1) {
//            boolean foundMatch = false;
//            for (CustomizationWithBoolean custom2 : list2) {
//                if (custom1.getCustomization().getId() == custom2.getCustomization().getId() &&
//                        custom1.getValue().equals(custom2.getValue())) {
//                    foundMatch = true;
//                    break;
//                }
//            }
//            if (!foundMatch) {
//                return false; // If any customization doesn't match, return false
//            }
//        }
//        return true;
//    }
//
//
//
//    public void onAddMealClicked(Meal meal) {
//        // Create a new meal row (HBox)
//        HBox mealRow = new HBox(20);
//        mealRow.setStyle("-fx-background-color: #ffffff; -fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");
//
//        // Hidden Label to store meal ID
//        Label idLabel = new Label(String.valueOf(meal.getId()));
//        idLabel.setVisible(false); // Make it invisible
//        idLabel.setManaged(false); // Ensure it doesn't take layout space
//
//
//
//
//        // Meal Image
//        ImageView imageView = new ImageView();
//
//        if (meal.getImage() != null) {
//            Image image = new Image(new ByteArrayInputStream(meal.getImage()));
//            imageView = new ImageView(image);
//            // Add imageView to your UI
//        } else {
//            System.err.println("Image data is null for the meal.");
//            // Handle the case where there is no image
//        }
//        imageView.setFitHeight(80);
//        imageView.setFitWidth(80);
//        imageView.setPreserveRatio(true);
//
//        Rectangle clip = new Rectangle();
//        clip.setArcWidth(20);  // Set the roundness of the corners
//        clip.setArcHeight(20);
//        clip.setWidth(imageView.getFitWidth());
//        clip.setHeight(imageView.getFitHeight());
//        imageView.setClip(clip);
//
//        // Meal Details
//        VBox detailsBox = new VBox(5);
//        Label nameLabel = new Label(meal.getName());
//        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
//        Label descriptionLabel = new Label(meal.getDescription());
//        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
//        detailsBox.getChildren().addAll(nameLabel, descriptionLabel);
//
//        // Meal Price
//        Label priceLabel = new Label(meal.getPrice()+"₪");
//        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #b70236;");
//
//        // Button to Change Price
//        Button addToCartBTN = new Button("Add to Cart");
//        addToCartBTN.setStyle("-fx-background-color: #222222; -fx-text-fill: #f3f3f3; -fx-background-radius: 20px; -fx-padding: 10px 15px;");
//
//        //changePriceButton.setOnAction(event -> openChangePricePage(nameLabel.getText(), priceLabel,idLabel.getText()));
//        addToCartBTN.setOnAction(event ->
//                {
//                    /*if(Objects.equals(addToCartBTN.getText(), "Add to Cart")) {
//                        addToCartBTN.setText("Added!");
//                        addToCartBTN.setDisable(true);
//                        listOfMeals.add(meal);
//                        numberOfMeals++;
//                        updateCartBadge();
//                    }*/
//                    openMealPopup(meal);
//                }
//        );
//
//        // Add components to mealRow
//        mealRow.getChildren().addAll(imageView, detailsBox, priceLabel, addToCartBTN);
//
//        // Add mealRow to menuContainer
//        menuContainer.getChildren().add(mealRow);
//
//        addToCartButtons.put(String.valueOf(meal.getId()), addToCartBTN);
//        //mealPriceLabels.put(String.valueOf(meal.getId()), priceLabel);
//    }
//
//
//    private void updateCartBadge() {
//        if (numberOfMeals > 0) {
//            playCartSound();
//            cartItemCount.setVisible(true);
//            cartItemCount.setText(String.valueOf(numberOfMeals));
//
//            // Show the fire GIF animation
//            cartIcon1.setImage(new Image(getClass().getResourceAsStream("/images/fire.gif")));
//            cartIcon1.setVisible(true);
//
//            // Play sound effect
//
//            // Hide the GIF after 2 seconds
//            PauseTransition pause = new PauseTransition(Duration.seconds(1));
//            pause.setOnFinished(event -> cartIcon1.setVisible(false)); // Hide the GIF
//            pause.play();
//
//        } else {
//            cartItemCount.setVisible(false);
//            cartIcon1.setVisible(false); // Ensure the image is hidden when no items
//        }
//    }
//    private void initalizeCartBadge() {
//        if (numberOfMeals > 0) {
//            cartItemCount.setVisible(true);
//            cartItemCount.setText(String.valueOf(numberOfMeals));
//
//            // Show the fire GIF animation
//
//
//        } else {
//            cartItemCount.setVisible(false);
//            cartIcon1.setVisible(false); // Ensure the image is hidden when no items
//        }
//    }
//
//    private void playCartSound() {
//        try {
//            // Create a Media object for the sound file
//            String soundPath = getClass().getResource("/images/added.mp3").toString();
//            Media sound = new Media(soundPath);
//
//            // Create a MediaPlayer for playing the sound
//            MediaPlayer mediaPlayer = new MediaPlayer(sound);
//
//            // Set the starting time (in seconds)
//            mediaPlayer.setOnReady(() -> {
//                mediaPlayer.seek(Duration.seconds(0.7)); // Start the sound from 3 seconds
//                mediaPlayer.play();
//            });
//
//            // Optional: Adjust volume (0.0 to 1.0)
//            mediaPlayer.setVolume(0.5);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    private void openChangePricePage(String mealName, Label priceLabel,String Id){
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/update_menu.fxml"));
//            Stage stage = new Stage();
//            Scene scene = new Scene(loader.load());
//            stage.setScene(scene);
//
//            // Pass data to the Change Price Controller
//            update_menu_controller controller = loader.getController();
//            controller.setMealDetails(mealName, priceLabel,Id);
//
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    public void initialize() throws Exception {
//        EventBus.getDefault().register(this);
//        SimpleClient client = SimpleClient.getClient();
//        System.out.println("sending menu to "+branchName);
//        // Load the Loading.gif image
//        cartIcon.setImage(new Image(getClass().getResourceAsStream("/images/carticon.png")));
//        cartItemCount.setOnMouseClicked(event -> {
//            try {
//                App.setRoot("Cart_page");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        cartIcon.setOnMouseClicked(event -> {
//            try {
//                App.setRoot("Cart_page");
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        cartIcon1.setOnMouseClicked(event -> {
//            try {
//                App.setRoot("Cart_page");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        //startLoading();
//        initalizeCartBadge();
//        //initalizeCart();
//        client.sendToServer("menu"+branchName);
//
//        /*/
//
//        /*/
//
//
//        try {
//            SimpleClient.getClient().sendToServer("Fetching SearchBy Options");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        textmenu.setText("Menu of "+branchName);
//
//        //
//        // Set the arrow image
//        scrollArrow.setImage(new Image(getClass().getResourceAsStream("/images/downarrow.png"))); // Update path as needed
//
//        // Position the arrow
//        //scrollArrow.setTranslateY(20); // Adjust to place it in the middle-bottom area
//        //scrollArrow.setTranslateX(0); // Adjust to place it in the middle-bottom area
//        scrollArrow.setVisible(true);
//        //scrollArrow.set
//
//        // Create arrow animation
//        arrowAnimation = new TranslateTransition(Duration.millis(500), scrollArrow);
//        arrowAnimation.setFromY(0);
//        arrowAnimation.setToY(30); // Move down by 10px
//        arrowAnimation.setCycleCount(TranslateTransition.INDEFINITE);
//        arrowAnimation.setAutoReverse(true);
//        arrowAnimation.play();
//
//        // Simulate loading delay (or replace this with actual loading logic)
//        new Thread(() -> {
//            try {
//                Thread.sleep(10000); // Simulate a 3-second loading delay
//                Platform.runLater(() -> {
//                    //loadingGif.setVisible(false); // Hide the loading GIF
//                    scrollPane.setVisible(true); // Show the main content
//                });
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//        // Add scroll listener to hide the arrow when fully scrolled
//        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.doubleValue() == 1.0) {
//                arrowAnimation.stop();
//                scrollArrow.setVisible(false); // Hide the arrow when fully scrolled
//            } else {
//                arrowAnimation.play();
//                scrollArrow.setVisible(true); // Show the arrow when not fully scrolled
//            }
//        });
//
//
//        /*/
//    }
//
//    @Subscribe
//    public void Getmeals(MealsList avMeals) {
//
//        Platform.runLater(() -> {
//            menuContainer.getChildren().removeIf(node -> {
//                return node instanceof HBox && node != menuContainer.getChildren().get(0);
//            });
//
//            // Add new meals to the menu
//            if (avMeals.getMeals() != null && !avMeals.getMeals().isEmpty()) {
//                for (Meal meal : avMeals.getMeals()) {
//                    onAddMealClicked(meal);
//                }
//            } else {
//                System.out.println("No new meals to display.");
//            }
//            //stopLoading();
//
//
//        });
//    }
///*
//    @Subscribe
//    public void addnewmeal(mealEvent meal) {
//        System.out.println("adding new meal for this client");
//        System.out.println("Meal's id is " + meal.getId());
//        Platform.runLater(() -> {
//            onAddMealClicked(meal.getMealName(), meal.getMealDisc(), String.valueOf(meal.getPrice()), meal.getId(), meal.getImage());
//        });
//        System.out.println("added the new meal for this client");
//
//    }*/
//
//    /*@Subscribe
//    public void updateMealPrice(updatePrice updatePrice) {
//        //System.out.println("changing price now!");
//        String mealId = String.valueOf(updatePrice.getIdMeal());
//        String newPrice = String.valueOf(updatePrice.getNewPrice());
//        // Check if the mealId exists in the map
//        Label priceLabel = mealPriceLabels.get(mealId);
//        if (priceLabel != null) {
//            Platform.runLater(() -> {
//                priceLabel.setText(newPrice + "₪");
//            });
//        } else {
//            System.out.println("Meal with ID " + mealId + " not found.");
//        }
//    }*/
//
//
//        @FXML
//        void showSearchOptionsDialog(ActionEvent event) {
//            //startLoading();
//            // Create a new Dialog
//            Dialog<Void> dialog = new Dialog<>();
//            dialog.setTitle("Search Options");
//
//            // Set a minimum width for the dialog
//            dialog.getDialogPane().setMinWidth(400); // Adjust the width as needed
//
//            // Create a VBox to hold the search options
//            VBox dialogContent = new VBox(10);
//            dialogContent.setPadding(new Insets(20));
//
//            // Add checkboxes for restaurants
//            if (restaurantNames != null && !restaurantNames.isEmpty()) {
//                for (String option : restaurantNames) {
//                    CheckBox checkBox = new CheckBox(option);
//                    checkBox.setUserData("restaurant"); // Mark checkbox as restaurant
//                    checkBox.setWrapText(true); // Allow text to wrap
//                    dialogContent.getChildren().add(checkBox);
//                }
//            } else {
//                dialogContent.getChildren().add(new Label("No restaurants available."));
//            }
//
//            // Add checkboxes for customizations
//            if (customizationNames != null && !customizationNames.isEmpty()) {
//                for (String option : customizationNames) {
//                    CheckBox checkBox = new CheckBox(option);
//                    checkBox.setUserData("customization"); // Mark checkbox as customization
//                    checkBox.setWrapText(true); // Allow text to wrap
//                    dialogContent.getChildren().add(checkBox);
//                }
//            } else {
//                dialogContent.getChildren().add(new Label("No customizations available."));
//            }
//
//            // Add a ScrollPane to make the content scrollable
//            ScrollPane scrollPane = new ScrollPane(dialogContent);
//            scrollPane.setFitToWidth(true);
//            scrollPane.setPrefHeight(200); // Set a fixed height for the scrollable area
//
//            // Add the ScrollPane to the Dialog
//            dialog.getDialogPane().setContent(scrollPane);
//
//            // Add an "Apply Filter" button
//            ButtonType applyFilterButtonType = new ButtonType("Apply Filter", ButtonBar.ButtonData.OK_DONE);
//            dialog.getDialogPane().getButtonTypes().add(applyFilterButtonType);
//
//            // Handle the "Apply Filter" button click
//            dialog.setResultConverter(buttonType -> {
//                if (buttonType == applyFilterButtonType) {
//                    // Collect selected filters
//                    List<String> selectedRestaurants = new ArrayList<>();
//                    List<String> selectedCustomizations = new ArrayList<>();
//
//                    for (javafx.scene.Node node : dialogContent.getChildren()) {
//                        if (node instanceof CheckBox) {
//                            CheckBox checkBox = (CheckBox) node;
//                            if (checkBox.isSelected()) {
//                                if ("restaurant".equals(checkBox.getUserData())) {
//                                    selectedRestaurants.add(checkBox.getText());
//                                } else if ("customization".equals(checkBox.getUserData())) {
//                                    selectedCustomizations.add(checkBox.getText());
//                                }
//                            }
//                        }
//                    }
//
//                    // Send the selected filters to the server
//                    SearchOptions searchOptions = new SearchOptions(selectedRestaurants, selectedCustomizations, branchName);
//                    try {
//                        SimpleClient.getClient().sendToServer(searchOptions);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return null;
//            });
//
//            // Show the Dialog
//            dialog.showAndWait();
//            //stopLoading();
//        }
//
//    /*private void startLoading() {
//        Platform.runLater(() -> {
//            loadingGif.setVisible(true);
//            stackPane.setDisable(true); // Disable all UI components
//        });
//    }
//
//    // Hide loading animation and enable UI
//    private void stopLoading() {
//        Platform.runLater(() -> {
//            loadingGif.setVisible(false);
//            stackPane.setDisable(false); // Enable all UI components
//        });
//    }*/
//    }
