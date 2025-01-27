package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB;
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

import java.io.IOException;
import java.util.List;

    public class RestaurantListController {
        @FXML
        private Button backButton;

        @FXML
        private VBox restaurantListContainer;

        @FXML
        private ScrollPane scrollPane;

        @FXML
        private RestaurantDB restaurantServer;


        // No-argument constructor for FXMLLoader
        public RestaurantListController() {
        }

        // Method to set the restaurant server
        public void setRestaurantServer(RestaurantDB restaurantServer) {
            this.restaurantServer = restaurantServer;
        }

        @FXML
        public void initialize() {
            try {
                setRestaurantServer(new RestaurantDB());
                updateRestaurantList();
            } catch (Exception e) {
                e.printStackTrace(); // In a real application, log this error or show an error message to the user
            }
        }

        @FXML
        public void updateRestaurantList() {
            if (restaurantServer != null) {
//                restaurantListContainer.getChildren().clear();  // Clear existing content
                List<Restaurant> restaurants = restaurantServer.getAllRestaurants();

                System.out.println("Number of restaurants: " + restaurants.size());

                for (Restaurant restaurant : restaurants) {
                    String restaurantDetails = "Restaurant" +
                            " ID='" + restaurant.getId() + '\'' +
                            ", RestaurantName='" + restaurant.getRestaurantName() + '\'' +
                            ", IMG='" + restaurant.getImagePath() + '\'' +
                            ", PhoneNumber='" + restaurant.getPhoneNumber() + '\'' +
                            '}';
                    System.out.println(restaurantDetails);
                    addRestaurantToUI(restaurant);
                }
            }
        }



        @FXML
        private void addRestaurantToUI(Restaurant restaurant) {
            HBox restaurantRow = new HBox(15); // 15px spacing between elements
            restaurantRow.setStyle("-fx-background-color: #fbebf4; -fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 20; -fx-arc-width: 500;");
            restaurantRow.setPrefWidth(Control.USE_COMPUTED_SIZE); // Use computed size for width
            restaurantRow.setFillHeight(true);

            // Ensure the image URL is valid or set a default
            String image_path = restaurant.getImagePath();
            if (image_path == null || image_path.isEmpty()) {
                image_path = "server/src/main/resources/images/restaurant.jpg"; // Specify a default image path
            }

            // Create an image view with error handling
            ImageView imageView = new ImageView();
            try {
                Image image = new Image(image_path, true); // true to load in background
                imageView.setImage(image);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
                // Set a default image if the original fails to load
                imageView.setImage(new Image("server/src/main/resources/images/restaurant.jpg"));
            }

            VBox detailsVBox = new VBox(5);
            detailsVBox.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(restaurant.getRestaurantName());
            nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #832018;");
            nameLabel.setOnMouseClicked(event -> nameLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: #832018;"));
            nameLabel.setOnMouseClicked(event -> nameLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: #832018;"));
            nameLabel.setWrapText(true);

            Label phoneLabel = new Label("Phone: " + restaurant.getPhoneNumber());
            phoneLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #832018;");

            detailsVBox.getChildren().addAll(nameLabel, phoneLabel);
            restaurantRow.getChildren().addAll(imageView, detailsVBox);
            restaurantListContainer.getChildren().add(restaurantRow);

            restaurantRow.setOnMouseClicked(event -> viewRestaurantMenu(restaurant));
        }
    @FXML
    private void viewRestaurantMenu(Restaurant restaurant) {   //to add when we doing the menu
        // go to restaurant's menu

        System.out.println("Opening menu for " + restaurant.getRestaurantName());
    }
//        @FXML
//        void backToHome(ActionEvent event) throws IOException {
//            Platform.runLater(() -> {
//                System.out.println("Back button pressed");
//                try {
//                    App.setRoot("mainScreen");
//                } catch (IOException e) {
//                    System.err.println("Failed to load the mainScreen.fxml");
//                    e.printStackTrace();
//                }
//            });
//        }
    @FXML
    void backToHome2() throws IOException {
            System.out.println("Back to Home");
        App.setRoot("mainScreen");
    }
}
