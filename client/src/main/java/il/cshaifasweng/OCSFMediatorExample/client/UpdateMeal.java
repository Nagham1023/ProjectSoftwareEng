package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.*;

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
    private VBox dynamicRestaurantContainer;

    @FXML
    private Label errorLabel;

    @FXML
    private Label errorLabel1;

    @FXML
    private ChoiceBox<String> restaurant_name;
    private List<String> restaurantNames;
    private List<String> customizationNames;
    private List<String> customizationOriginal;
    private List<String> customizationInitialized = new ArrayList<>();
    private List<String> chosenCustomizationNames= new ArrayList<>();
    private List<String> chosenRestaurantsNames= new ArrayList<>();
    private String originalDescription;
    private List<String> originalCustomizations = new ArrayList<>();
    private List<String> originalRestaurantsNames= new ArrayList<>();
    private String mealId;
    private Meal meal;
    private Map<String, HBox> customrowMap = new HashMap<>();
    private Map<String, HBox> restaurantrowMap = new HashMap<>();
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
    public void setMealDetails(String name, String id, String description, List<String> customizations, List<String> restaurants) {
        this.mealNameLabel.setText("Update: " + name);
        this.mealId = id;
        this.mealDescriptionField.setText(description);

        // Load customizations from parameters instead of server
        customizations.forEach(this::fillRow);
        restaurants.forEach(this::fillRowRestaurants);
        customizationOriginal = customizations;
        originalRestaurantsNames =restaurants;
        this.originalDescription = description;
        //customizationOriginal.forEach(this::fillChoosen);
        System.out.println("Customizations: " + customizations);
    }

    private void fillChosen(List<String> chosen,String s) {
        chosen.add(s);
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
        // Repopulate from original data

    }

    private void refreshRestaurantsDisplay() {
        restaurantrowMap.clear();
        chosenRestaurantsNames.clear();

        // Clear only dynamic rows
        dynamicRestaurantContainer.getChildren().clear();

        // Repopulate from original data
        originalRestaurantsNames.forEach(this::fillRowRestaurants);
        // Repopulate from original data

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
            printMap(customrowMap);
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
            printMap(restaurantrowMap);
            return;
        }


        // Add to UI and storage
        chosenRestaurantsNames.add(restaurantName);
        fillRowRestaurants(restaurantName);
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

    @FXML
    void handleReset(ActionEvent event) {
        // Reset description
        mealDescriptionField.setText(originalDescription);

        // Reset customizations
        refreshCustomizationDisplay();
        refreshRestaurantsDisplay();

        showError(errorLabel,""); // Clear any errors
        showError(errorLabel1,"");
    }

    @FXML
    void openAddMealPage(ActionEvent event) {
        boolean isCompany= true;
        String newDescription = mealDescriptionField.getText().trim();
        List<String> newCustomizations = new ArrayList<>(chosenCustomizationNames);

        // Validation
        if (newDescription.isEmpty()) {
            showError(errorLabel,"Description cannot be empty!");
            return;
        }

        if (chosenCustomizationNames.isEmpty()) {
            showError(errorLabel,"At least one customization required!");
            return;
        }

        Iterator<String> iterator = chosenRestaurantsNames.iterator();

        if(!chosenRestaurantsNames.get(0).equals("ALL")) {
            while (iterator.hasNext()) {
                String restaurant = iterator.next();
                if (!restaurantNames.contains(restaurant)) {
                    isCompany = false;
                }
            }
        }

        // Create update object
        UpdateMealRequest updateRequest = new UpdateMealRequest(
                mealId,
                newDescription,
                chosenCustomizationNames,
                chosenRestaurantsNames
        );

        try {
            SimpleClient.getClient().sendToServer(updateRequest);
            showError(errorLabel,"Changes saved successfully!");
            mealDescriptionField.getScene().getWindow().hide();
            // Update original values after successful save
            this.originalDescription = newDescription;
            this.originalCustomizations = new ArrayList<>(newCustomizations);
        } catch (IOException e) {
            showError(errorLabel,"Failed to save changes. Please try again.");
            e.printStackTrace();
        }
    }
}
