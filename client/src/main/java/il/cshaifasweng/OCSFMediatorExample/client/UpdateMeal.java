package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateMeal {
    @FXML
    private ComboBox<String> costumazation_name;

    @FXML
    private Label mealNameLabel;

    @FXML
    private TextField mealDescriptionField;

    @FXML
    private VBox customizationContainer;

    @FXML
    private VBox dynamicCustomizationContainer;

    @FXML
    private Label errorLabel;

    @FXML
    private ChoiceBox<String> restaurant_name;
    private List<String> restaurantNames;
    private List<String> customizationNames;
    private List<String> customizationOriginal;
    private List<String> customizationInitialized = new ArrayList<>();
    private List<String> chosenCustomizationNames= new ArrayList<>();
    private String originalDescription;
    private List<String> originalCustomizations = new ArrayList<>();
    private String mealId;
    private Meal meal;
    private Map<String, HBox> customrowMap = new HashMap<>();
    private Map<String, String> existingCustomizations = new HashMap<>();

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
            // Store existing customizations for reference
            customizationNames.forEach(name -> existingCustomizations.put(name, name));

            costumazation_name.getItems().addAll(customizationNames);
            costumazation_name.getItems().add("Write Other");
            costumazation_name.setEditable(true);

            // Add "ALL" and "Write Other" options
            restaurant_name.getItems().add("ALL");
        });
    }
    public void setMealDetails(String name, String id, String description, List<String> customizations) {
        this.mealNameLabel.setText("Update: " + name);
        this.mealId = id;
        this.mealDescriptionField.setText(description);

        // Load customizations from parameters instead of server
        customizations.forEach(this::fillRow);
        customizationOriginal = customizations;
        this.originalDescription = description;
        //customizationOriginal.forEach(this::fillChoosen);
        System.out.println("Customizations: " + customizations);
    }

    private void fillChoosen(String s) {
        chosenCustomizationNames.add(s);
    }

    @Subscribe
    public void putMealDescription(UpdateMealEvent event) {
        Platform.runLater(() -> {
            this.meal = event.getMeal();
            this.originalDescription = meal.getDescription(); // Store original
            mealDescriptionField.setText(event.getMeal().getDescription());
            List<Customization> L = event.getMeal().getCustomizations();
            originalCustomizations.clear(); // Reset original list
            L.forEach(c -> originalCustomizations.add(c.getName()));

            // Clear and reload customizations
            refreshCustomizationDisplay();
        });
    }

    private void refreshCustomizationDisplay() {
        customrowMap.clear();
        chosenCustomizationNames.clear();

        // Clear only dynamic rows
        dynamicCustomizationContainer.getChildren().clear();

        // Repopulate from original data
        customizationOriginal.forEach(this::fillRow);
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
            showError("Please enter/select a customization!");
            return;
        }

        // Check duplicates
        if (customrowMap.containsKey(customizationName)) {
            showError("Customization already exists!");
            printMap(customrowMap);
            return;
        }


        // Add to UI and storage
        chosenCustomizationNames.add(customizationName);
        fillRow(customizationName);
        clearInput();
    }
    public static <K, V> void printMap(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            System.out.println("Map is empty.");
            return;
        }

        System.out.println("Map contents:");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }


    private void handleDeleteCustomization(String customizationName) {
        HBox row = customrowMap.get(customizationName);
        if (row != null) {
            // Remove from UI immediately
            customizationContainer.getChildren().remove(row);

            // Update state synchronously
            customrowMap.remove(customizationName);
            chosenCustomizationNames.remove(customizationName);
        }
    }

    private void clearInput() {
        costumazation_name.getSelectionModel().clearSelection();
        costumazation_name.getEditor().clear();
        errorLabel.setText("");
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #760b0b; -fx-font-weight: bold;");
        });
    }

    @FXML
    void handleReset(ActionEvent event) {
        // Reset description
        mealDescriptionField.setText(originalDescription);

        // Reset customizations
        refreshCustomizationDisplay();

        showError(""); // Clear any errors
    }

    @FXML
    void openAddMealPage(ActionEvent event) {
        String newDescription = mealDescriptionField.getText().trim();
        List<String> newCustomizations = new ArrayList<>(chosenCustomizationNames);

        // Validation
        if (newDescription.isEmpty()) {
            showError("Description cannot be empty!");
            return;
        }

        if (chosenCustomizationNames.isEmpty()) {
            showError("At least one customization required!");
            return;
        }

        // Create update object
        UpdateMealRequest updateRequest = new UpdateMealRequest(
                mealId,
                newDescription,
                chosenCustomizationNames,
                restaurant_name.getValue()
        );

        try {
            SimpleClient.getClient().sendToServer(updateRequest);
            showError("Changes saved successfully!");
            mealDescriptionField.getScene().getWindow().hide();
            // Update original values after successful save
            this.originalDescription = newDescription;
            this.originalCustomizations = new ArrayList<>(newCustomizations);
        } catch (IOException e) {
            showError("Failed to save changes. Please try again.");
            e.printStackTrace();
        }
    }
}
