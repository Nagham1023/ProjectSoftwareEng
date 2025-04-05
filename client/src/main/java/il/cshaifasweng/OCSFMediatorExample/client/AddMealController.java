package il.cshaifasweng.OCSFMediatorExample.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;


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
    private ChoiceBox<String> restaurant_name;

    @FXML
    private VBox dynamicCustomizationContainer;

    @FXML
    private VBox dynamicRestaurantContainer;

    @FXML
    private Label errorLabel;

    @FXML
    private Label errorLabel1;

    @FXML
    private ImageView mealImageView;
    private File selectedImageFile;

    private List<String> restaurantNames;
    private List<String> customizationNames;
    private List<String> chosenCustomizationNames= new ArrayList<>();
    private List<String> chosenRestaurantsNames= new ArrayList<>();
    private Map<String, HBox> customrowMap = new HashMap<>();
    private Map<String, HBox> restaurantrowMap = new HashMap<>();


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
        MealEventUpgraded ME= new MealEventUpgraded(mealNameField.getText(), mealDescriptionField.getText(), mealPriceField.getText(), imageBytes, restaurant_name.getValue().equals("ALL"), chosenCustomizationNames, chosenRestaurantsNames);

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
                feedbackLabel.getScene().getWindow().hide();
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
        Platform.runLater(() -> {
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

    @FXML
    public void addingRestaurants() {
        String selectedValue = restaurant_name.getValue();

        if (selectedValue == null || selectedValue.isEmpty()) {
            // Handle empty selection if needed
            return;
        }

        // Check if the value is not already in the list
        if (!chosenRestaurantsNames.contains(selectedValue)) {
            chosenRestaurantsNames.add(selectedValue);
        }
    }
    public void fillRow(String customizationName) {
        Platform.runLater(() -> {
            String key = customizationName;
            HBox mealRow = new HBox(20);
            mealRow.setStyle("-fx-background-color: #ffdbe4; -fx-border-color: #881d3a; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

            //labels for data show
            Label customization = new Label(customizationName);

            customization.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #881d3a;");

            // Buttons container
            HBox buttonBox = new HBox(10);

            Button deleteB = new Button("DELETE");
            deleteB.setStyle("-fx-background-color: #C75C5C; -fx-text-fill: #FFD9D1;");
            deleteB.setOnAction(e -> handleDeleteCustomization(customizationName));

            buttonBox.getChildren().addAll(deleteB);


            mealRow.getChildren().addAll(
                    customization,
                    buttonBox
            );


            dynamicCustomizationContainer.getChildren().add(mealRow);
            System.out.println("showing customization for meal");
            chosenCustomizationNames.add(customizationName);
            customrowMap.put(key, mealRow);
        });
    }

    public void fillRowRestaurants(String restaurantName) {
        Platform.runLater(() -> {
            String key = restaurantName;
            HBox mealRow = new HBox(20);
            mealRow.setStyle("-fx-background-color: #ffdbe4; -fx-border-color: #881d3a; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

            //labels for data show
            Label restaurant = new Label(restaurantName);

            restaurant.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #881d3a;");

            // Buttons container
            HBox buttonBox = new HBox(10);

            Button deleteB = new Button("DELETE");
            deleteB.setStyle("-fx-background-color: #C75C5C; -fx-text-fill: #FFD9D1;");
            deleteB.setOnAction(e -> handleDeleteRestaurant(restaurantName));

            buttonBox.getChildren().addAll(deleteB);


            mealRow.getChildren().addAll(
                    restaurant,
                    buttonBox
            );


            dynamicRestaurantContainer.getChildren().add(mealRow);
            System.out.println("showing customization for meal");
            chosenRestaurantsNames.add(restaurantName);
            restaurantrowMap.put(key, mealRow);
        });
    }
    public void addToListCustom(ActionEvent actionEvent) {
        String selectedValue = costumazation_name.getValue();
        String customizationName;

        // Handle "Write Other" selection
        if ("Write Other".equals(selectedValue)) {
            customizationName = costumazation_name.getEditor().getText().trim();
        } else {
            customizationName = selectedValue.trim();
            System.out.println("customizationName: " + customizationName);
        }

        // Validation
        if (customizationName.isEmpty()) {
            showError(errorLabel,"Please enter/select a customization!");
            return;
        }

        // Check duplicates
        if (customrowMap.containsKey(customizationName)) {
            showError(errorLabel,"Customization already exists!");
            return;
        }


        // Add to UI and storage
        chosenCustomizationNames.add(customizationName);
        fillRow(customizationName);
        clearInput();
    }

    public void addToListRestaurant(ActionEvent actionEvent) {
        String selectedValue = restaurant_name.getValue();
        String restaurantName;

        // Handle "All" selection
        if ("ALL".equals(selectedValue)) {
            Iterator<String> iterator = chosenRestaurantsNames.iterator();
            while (iterator.hasNext()) {
                String restaurant = iterator.next();
                HBox row = restaurantrowMap.get(restaurant);
                if (row != null) {
                    // Remove from UI immediately
                    dynamicRestaurantContainer.getChildren().remove(row);

                    // Update state synchronously
                    restaurantrowMap.remove(restaurant);
                    iterator.remove(); // Safe removal
                }
            }

        }
        restaurantName = selectedValue.trim();

        // Validation
        if (restaurantName.isEmpty()) {
            showError(errorLabel1,"Please enter/select a restaurant!");
            return;
        }

        // Check duplicates
        if (restaurantrowMap.containsKey(restaurantName)) {
            showError(errorLabel1, "restaurant already exists!");
            return;
        }


        // Add to UI and storage
        chosenRestaurantsNames.add(restaurantName);
        fillRowRestaurants(restaurantName);
        clearInput();
    }
    private void handleDeleteCustomization(String customizationName) {
        HBox row = customrowMap.get(customizationName);
        if (row != null) {
            // Remove from UI immediately
            dynamicCustomizationContainer.getChildren().remove(row);

            // Update state synchronously
            customrowMap.remove(customizationName);
            chosenCustomizationNames.remove(customizationName);
        }
    }

    private void handleDeleteRestaurant(String restaurantName) {
        HBox row = restaurantrowMap.get(restaurantName);
        if (row != null) {
            // Remove from UI immediately
            dynamicRestaurantContainer.getChildren().remove(row);

            // Update state synchronously
            restaurantrowMap.remove(restaurantName);
            chosenRestaurantsNames.remove(restaurantName);
        }
    }

    private void clearInput() {
        costumazation_name.getSelectionModel().clearSelection();
        costumazation_name.getEditor().clear();
        errorLabel.setText("");
    }

    private void showError(Label errorLabel, String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #760b0b; -fx-font-weight: bold;");
        });
    }


}