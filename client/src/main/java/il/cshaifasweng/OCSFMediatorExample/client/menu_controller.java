package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static il.cshaifasweng.OCSFMediatorExample.client.PrimaryController.meals;


public class menu_controller {

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
    private Button Search_combo_box;
    @FXML
    private Button Reset_Button;
    @FXML
    void Reset_Menu(ActionEvent event) throws IOException {
        SimpleClient.getClient().sendToServer("Sort Reset");
    }

    @FXML
    void Sort_meals(ActionEvent event) {
        openSearchByPage();
    }

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
    // Method to open the Search By page
    private void openSearchByPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/Search_by.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to open the Change Price page

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
    public void ShowNewMeals(List<Meal> avMeals) {

        System.out.println("Refreshing menu content...");

        Platform.runLater(() -> {
            menuContainer.getChildren().removeIf(node -> {
                return node instanceof HBox && node != menuContainer.getChildren().get(0);
            });

            // Add new meals to the menu
            if (avMeals != null && !avMeals.isEmpty()) {
                for (Meal meal : avMeals) {
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
        });
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
        // Load the Loading.gif image
        loadingGif.setImage(new Image(getClass().getResourceAsStream("/images/Loading.gif")));
        loadingGif.setVisible(true); // Initially show the loading GIF

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
    @FXML
    void backToHome(ActionEvent event) throws IOException {
            App.setRoot("primary");
    }
}