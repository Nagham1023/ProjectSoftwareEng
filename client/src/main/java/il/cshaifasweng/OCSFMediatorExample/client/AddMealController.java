package il.cshaifasweng.OCSFMediatorExample.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.ByteBuffer;
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
    private ImageView mealImageView;
    private File selectedImageFile;


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
        mealEvent ME = new mealEvent(mealNameField.getText(),mealDescriptionField.getText(),mealPriceField.getText(),imageBytes);
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
    }


}