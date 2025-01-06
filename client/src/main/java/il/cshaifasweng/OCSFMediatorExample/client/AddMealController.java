package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class AddMealController {

    @FXML
    private TextField mealNameField; // Correct type for FXML mapping

    @FXML
    private TextArea mealDescriptionField;

    @FXML
    private TextField mealPriceField;

    @FXML
    private ImageView mealImageView;
    private File selectedImageFile;


    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    public void onAddMealClicked() throws IOException {
//        //mealEvent newmeal = new mealEvent(mealNameField.getText(),mealDescriptionField.getText(),mealPriceField.getText());
//        try {
//            //SimpleClient.getClient().sendToServer(newmeal);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Failed to send data to the server!");
//        }
        //SimpleClient.getClient().sendToServer("newmeal");
        //System.out.println("newmeal : " + newmeal.toString());
        /*
        String name = mealNameField.getText();
        String description = mealDescriptionField.getText();
        String price = mealPriceField.getText();

        if (selectedImageFile == null) {
            System.out.println("Please select an image!");
            return;
        }

        // TODO: Implement the logic to add the meal (e.g., update the menu or save to database)
        System.out.println("Meal added:");
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("Price: " + price);
        System.out.println("Image Path: " + selectedImageFile.getAbsolutePath());*/
    }
    @FXML
    public void onSelectImageClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Meal Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        // Show the file chooser and get the selected file
        Stage stage = (Stage) mealImageView.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            // Display the selected image in the ImageView
            Image image = new Image(selectedImageFile.toURI().toString());
            mealImageView.setImage(image);
        }
    }
    @FXML
    void backToHome(ActionEvent event) throws IOException {
        App.setRoot("primary");
    }


}