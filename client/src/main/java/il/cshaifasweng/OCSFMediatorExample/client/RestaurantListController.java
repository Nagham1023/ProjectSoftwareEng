package il.cshaifasweng.OCSFMediatorExample.client;

import com.mysql.cj.xdevapi.Client;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.listOfMeals;
import static il.cshaifasweng.OCSFMediatorExample.client.CartPageController.numberOfMeals;
import static il.cshaifasweng.OCSFMediatorExample.client.menu_controller.branchName;

public class RestaurantListController {
    @FXML
    private VBox restaurantListContainer;

    @FXML
    private Button backButton;

    private HBox backButtonContainer; // To store the back button row


        // No-argument constructor for FXMLLoader
        public RestaurantListController() {
        }


        @FXML
        public void initialize() {
            backButtonContainer = (HBox) backButton.getParent();
            EventBus.getDefault().register(this);
            SimpleClient client = SimpleClient.getClient();
            try {
                client.sendToServer("getAllRestaurants");
            } catch (Exception e) {
                e.printStackTrace(); // In a real application, log this error or show an error message to the user
            }

            listOfMeals.clear();
            numberOfMeals = 0;
        }

        @Subscribe
        public void updateRestaurantList(RestaurantList restaurantList) {
            if (restaurantList != null) {
//                restaurantListContainer.getChildren().clear();  // Clear existing content
                List<Restaurant> restaurants = restaurantList.getRestaurantList();

                //System.out.println("Number of restaurants: " + restaurants.size());

                Platform.runLater(() -> {
                    restaurantListContainer.getChildren().clear();  // Clear existing content

                    for (Restaurant restaurant : restaurants) {
                        String restaurantDetails = "Restaurant" +
                                " ID='" + restaurant.getId() + '\'' +
                                ", RestaurantName='" + restaurant.getRestaurantName() + '\'' +
                                ", IMG='" + restaurant.getImagePath() + '\'' +
                                ", PhoneNumber='" + restaurant.getPhoneNumber() + '\'' +
                                '}';
                        //System.out.println(restaurantDetails);
                        addRestaurantToUI(restaurant);
                    }
                });
            }
        }

    private void addRestaurantToUI(Restaurant restaurant) {
        // Ensure the back button remains at the top
        if (!restaurantListContainer.getChildren().contains(backButtonContainer)) {
            restaurantListContainer.getChildren().add(0, backButtonContainer);
        }

        // Create a new HBox for the restaurant row
        HBox restaurantRow = new HBox(15);
        restaurantRow.setStyle("-fx-background-color: #fbebf4; -fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 20;");
        restaurantRow.setPrefWidth(Control.USE_COMPUTED_SIZE);
        restaurantRow.setFillHeight(true);

        // Load restaurant image
        String image_path = restaurant.getImagePath();
        if (image_path == null || image_path.isEmpty()) {
            image_path = "downarrow.png"; // Default image
        }

        ImageView imageView = new ImageView();
        try (InputStream inputStream = getClass().getResourceAsStream("/images/" + image_path)) {
            if (inputStream != null) {
                Image image = new Image(inputStream);
                imageView.setImage(image);
                imageView.setFitHeight(300);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
            } else {
                System.err.println("Image not found: " + image_path);
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/downarrow.png")));
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/downarrow.png")));
        }

        // Create a VBox for restaurant details
        VBox detailsVBox = new VBox(5);
        detailsVBox.setAlignment(Pos.CENTER_LEFT);

        // Add restaurant name
        Label nameLabel = new Label(restaurant.getRestaurantName());
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #832018;");
        nameLabel.setWrapText(true);

        // Add phone number
        Label phoneLabel = new Label("Phone: " + restaurant.getPhoneNumber());
        phoneLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #832018;");

        // Add details to the VBox
        detailsVBox.getChildren().addAll(nameLabel, phoneLabel);

        // Add image and details to the restaurant row
        restaurantRow.getChildren().addAll(imageView, detailsVBox);

        // Add the restaurant row to the restaurantListContainer
        restaurantListContainer.getChildren().add(restaurantRow);

        // Set click event for the restaurant row
        restaurantRow.setOnMouseClicked(event -> viewRestaurantMenu(restaurant));
    }

    private void viewRestaurantMenu(Restaurant restaurant) {
        SimpleClient client = SimpleClient.getClient();
        try {
            //client.sendToServer("menu"+restaurant.getRestaurantName());
            branchName = restaurant.getRestaurantName();
            App.setRoot("menu");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Opening menu for " + restaurant.getRestaurantName());
    }
    public static String getBranchName(){
            return branchName;
    }
    @FXML
    void backToHome2() throws IOException {
        App.setRoot("mainScreen");
    }
}
