package il.cshaifasweng.OCSFMediatorExample.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import il.cshaifasweng.OCSFMediatorExample.entities.SearchOptions;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AddMealController {
    @FXML
    private Label feedbackLabel;
    @FXML
    private TextField mealNameField; // Correct type for FXML mapping

    @FXML
    private TextArea mealDescriptionField;

    @FXML
    private TextField mealPriceField;

    @FXML
    private ComboBox<String> costumazation_name;

    @FXML
    private ComboBox<String> restaurant_name;

    @FXML
    private ImageView mealImageView;
    private File selectedImageFile;

    private List<String> restaurantNames;
    private List<String> customizationNames;
    private List<String> chosenCustomizationNames= new ArrayList<>();


    public static byte[] imageToByteArray(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        // Convert JavaFX Image to AWT BufferedImage
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage writableImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }

        // Encode BufferedImage as PNG and write to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    public void onAddMealClicked() throws IOException {
        byte[] imageBytes = imageToByteArray(mealImageView.getImage());
        //mealEvent ME = new mealEvent(mealNameField.getText(),mealDescriptionField.getText(),mealPriceField.getText(),imageBytes);
        mealEvent ME= new mealEvent(mealNameField.getText(), mealDescriptionField.getText(), mealPriceField.getText(), imageBytes, restaurant_name.getValue().equals("ALL"), chosenCustomizationNames, restaurant_name.getValue());

            SimpleClient client;
        client = SimpleClient.getClient();
        // Convert byte[] to InputStream
        client.sendToServer(ME);
    }

    @Subscribe
    public void successorfail(String result)
    {
        if(Objects.equals(result, "added"))
            Platform.runLater(() -> {
                feedbackLabel.setText("Successfully added");
                feedbackLabel.setStyle("-fx-text-fill: green;");
            });
        else
        {
            Platform.runLater(() -> {
                feedbackLabel.setText("Failed to add");
                feedbackLabel.setStyle("-fx-text-fill: red;");
            });
        }

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
    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        SimpleClient client = SimpleClient.getClient();
        try {
            SimpleClient.getClient().sendToServer("Fetching SearchBy Options");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Subscribe
    public void putSearchOptions(SearchOptions options) {
        // Handle the event sent from the server
        restaurantNames = options.getRestaurantNames();
        customizationNames = options.getCustomizationNames();
        Platform.runLater(() -> {
            // UI-related code here
            fillComboBox();
        });
    }

    public void fillComboBox() {
        Platform.runLater(() -> { // Wrap ALL UI operations here
            // Clear ComboBoxes
            restaurant_name.getItems().clear();
            costumazation_name.getItems().clear();

            // Add items to ComboBoxes
            for (String restaurant : restaurantNames) {
                restaurant_name.getItems().add(restaurant);
            }
            for (String customization : customizationNames) {
                costumazation_name.getItems().add(customization);
            }

            // Add "ALL" and "Write Other" options
            restaurant_name.getItems().add("ALL");
            costumazation_name.getItems().add("Write Other");

            // Make ComboBox editable
            costumazation_name.setEditable(true);
        });
    }

    @FXML
    public void addingCustomization() {
        String selectedValue = costumazation_name.getValue();

        if (selectedValue == null || selectedValue.isEmpty()) {
            // Handle empty selection if needed
            return;
        }

        // Check if the value is not already in the list
        if (!chosenCustomizationNames.contains(selectedValue)) {
            chosenCustomizationNames.add(selectedValue);
        }
    }


}