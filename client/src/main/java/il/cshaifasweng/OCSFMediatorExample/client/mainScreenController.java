package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.WarningEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class mainScreenController {
    @FXML
    private ImageView imageView;  // Ensure this ImageView is the one defined in your FXML
    @FXML
    private ImageView restaurantLogo;
    @FXML
    private ImageView instaIcon;

    @FXML
    public void initialize() throws IOException{
        //restaurantLogo.setImage(new Image(getClass().getResourceAsStream("/il/cshaifasweng/OCSFMediatorExample/client/images/mamas_kitchen_final.png")));
        //instaIcon.setImage(new Image(getClass().getResourceAsStream("/il/cshaifasweng/OCSFMediatorExample/client/images/instaIcon.png")));
        SimpleClient client;
        boolean temp = SimpleClient.isClientConnected();
        EventBus.getDefault().register(this);
        client = SimpleClient.getClient();
        if (!temp) {
            try {
                client.sendToServer("add client");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
//        Platform.runLater(() ->
//        {
//            SimpleClient client;
//            boolean temp = SimpleClient.isClientConnected();
//            EventBus.getDefault().register(this);
//            client = SimpleClient.getClient();
//            if (!temp) {
//                try {
//                    client.sendToServer("add client");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            //if (SimpleClient.isLog()) {
//               // UserCheck user = SimpleClient.getUser();
//               // copyr.setText("logged into " + user.getUsername() + " with role " + user.getRole());
//            //}
//        });
        // Load the image
//        imageView.setImage(new Image(getClass().getResourceAsStream("/images/mamas_kitchen_final.png"))); // Update path as needed

    }
    @FXML
    void openLink(ActionEvent event) throws URISyntaxException, IOException {
        System.out.println("link clicked");
        Desktop.getDesktop().browse(new URI("https://www.instagram.com/mamas_kitchen2025/"));
    }
    @FXML
    void clickedOnComplaints(ActionEvent event)throws IOException {
        App.setRoot("addcomplain");
    }

    @FXML
    // Method to handle the button click for transitioning to the restaurant list
    public void handleChooseBranchClick(ActionEvent event) throws IOException {
        /*
        try {
            // Initialize the loader with the correct FXML path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/RestaurantList.fxml"));

            // Load the FXML and get the root node in one step
            Parent root = loader.load(); // This line must come before getting the controller

            // Now retrieve the controller
            RestaurantListController controller = loader.getController();

            // Check if the controller is not null, then set the server
            if (controller != null) {
                controller.setRestaurantServer(new RestaurantServer());
                controller.updateRestaurantList();  // Manually call to refresh the list
            } else {
                System.out.println("Controller is null, check FXML configuration.");
            }

            // Create the scene with the loaded root node
            Scene scene = new Scene(root);

            // Get the current window from the event source, or create a new stage if needed
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace or handle the exception appropriately
        }*/
        App.setRoot("RestaurantList");
    }
    @FXML
    void goToLogin(ActionEvent event) throws IOException {
        App.setRoot("login");
    }
    @Subscribe  // Add this annotation
    public void handleEvent(WarningEvent event) {  // Replace with actual event type
        // Handle event here
    }
    @FXML
    void goToReservation(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("Reservation");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    @FXML
    void goToReservationCancel(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("ReservationCancellation");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}