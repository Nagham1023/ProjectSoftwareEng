package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.ReConfirmEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.RestaurantList;
import il.cshaifasweng.OCSFMediatorExample.entities.TableNode;
import il.cshaifasweng.OCSFMediatorExample.entities.tablesStatus;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantMapController {
    boolean offline = false;
    @FXML
    public Button backButton;

    @FXML
    private ComboBox<String> restaurantsComboBox;

    @FXML
    private ScrollPane mapScrollPane; // Scrollable pane for the map

    @FXML
    private AnchorPane mapContainer; // Container for the map elements

    @FXML
    private ImageView restaurantMapImage; // Image of the restaurant map

    @FXML
    private HBox topHBox; // HBox containing the ComboBox and Label

    @FXML
    private Label restaurantLabel;

    private Timeline refreshTimeline;

    @FXML
    public void initialize() throws IOException {
        EventBus.getDefault().register(this);
        setupAutoRefresh();
        // Fetch all restaurants
        SimpleClient.getClient().sendToServer("getAllRestaurants");

        // Add an event listener to the ComboBox
        restaurantsComboBox.setOnAction(event -> handleRestaurantSelection());

        // Add the status description label at the top
        addStatusDescriptionLabel();
    }

    /**
     * Adds a label at the top to describe the table status colors using colored shapes.
     */
    private void addStatusDescriptionLabel() {
        // Create an HBox to hold the status indicators
        HBox statusHBox = new HBox(10); // 10 is the spacing between elements
        statusHBox.setLayoutX(20);
        statusHBox.setLayoutY(10);
        statusHBox.setStyle("-fx-padding: 10px;");

        // Add "Available" indicator
        HBox availablePane = createStatusIndicator("#2ecc71", "Available");
        statusHBox.getChildren().add(availablePane);

        // Add "Reserved" indicator
        HBox reservedPane = createStatusIndicator("#f1c40f", "Reserved");
        statusHBox.getChildren().add(reservedPane);

        // Add "Occupied" indicator
        HBox occupiedPane = createStatusIndicator("#e74c3c", "Occupied");
        statusHBox.getChildren().add(occupiedPane);

        // Add the HBox to the map container
        mapContainer.getChildren().add(statusHBox);
    }

    /**
     * Helper method to create a status indicator with a colored circle and text.
     */
    private HBox createStatusIndicator(String color, String text) {
        // Create a colored circle
        Circle circle = new Circle(8); // Radius of 8
        circle.setFill(Color.web(color));

        // Create a label for the text
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Use an HBox to arrange the circle and label horizontally with spacing
        HBox hbox = new HBox(5); // 5 is the spacing between elements
        hbox.setAlignment(Pos.CENTER_LEFT); // Align elements to the left
        hbox.getChildren().addAll(circle, label);

        return hbox;
    }


    private void handleRestaurantSelection() {
        Platform.runLater(() -> {
            String selectedRestaurant = restaurantsComboBox.getValue();
            if (selectedRestaurant != null && !selectedRestaurant.equals("Pick a Restaurant")) {
                // Clear the map and related elements
                //mapContainer.getChildren().clear();

                // Re-add the status description label
                addStatusDescriptionLabel();

                // Send a message to the server with the selected restaurant
                try {
                    SimpleClient.getClient().sendToServer("getTablesForRestaurant: " + selectedRestaurant);
                } catch (IOException e) {
                    System.err.println("Failed to send message to the server: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe
    public void putResturants(RestaurantList restaurants) {
        Platform.runLater(() -> {
            System.out.println("Received restaurant list: " + restaurants);

            // Clear previous items (except "Pick a Restaurant")
            restaurantsComboBox.getItems().clear();
            restaurantsComboBox.getItems().add("Pick a Restaurant"); // Add the default item

            // Populate the combo box with restaurant names
            restaurants.getRestaurantList().forEach(restaurant ->
                    restaurantsComboBox.getItems().add(restaurant.getRestaurantName()) // Assuming Restaurant has getRestaurantName()
            );

            // Select the default item
            restaurantsComboBox.getSelectionModel().selectFirst();
        });
    }

    private void handleOfflineChanges(tablesStatus tablesStatus) {
        Platform.runLater(() -> {
            List<TableNode> tables = tablesStatus.getTables();
            List<String> statuses = tablesStatus.getStatuses();

            // Create a map of table IDs to their new statuses for quick lookup
            Map<Integer, String> tableStatusMap = new HashMap<>();
            for (int i = 0; i < tables.size(); i++) {
                tableStatusMap.put(tables.get(i).getTableID(), statuses.get(i));
            }

            // Iterate through the existing children of the mapContainer
            for (javafx.scene.Node node : mapContainer.getChildren()) {
                if (node instanceof Label) {
                    Label tableLabel = (Label) node;
                    String labelText = tableLabel.getText();

                    // Check if the label is a table label (e.g., "Table 1")
                    if (labelText.startsWith("Table ")) {
                        // Extract the table ID from the label's text
                        int tableID = Integer.parseInt(labelText.replace("Table ", ""));
                        String id = labelText.replaceAll("[^0-9]", "");
                        // Find the button associated with this table label
                        Button tableButton = findButtonForTableLabel(id);
                        if (tableButton != null) {
                            // Get the new status for this table
                            String newStatus = tableStatusMap.get(tableID);
                            if (newStatus != null) {
                                // Get the current button color
                                String currentColor = tableButton.getStyle().contains("-fx-background-color: ") ?
                                        tableButton.getStyle().split("-fx-background-color: ")[1].split(";")[0] : "";

                                // Get the new color for the status
                                String newColor = getButtonColorForStatus(newStatus);

                                // Compare the current color with the new color
                                if (!newColor.equals(currentColor)) {
                                    // Update the button color
                                    tableButton.setStyle("-fx-background-color: " + newColor + "; -fx-text-fill: white;");
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Helper method to find the button associated with a table label.
     * This assumes the button is positioned near the label in the Pane.
     */
    private Button findButtonForTableLabel(String tableLabel) {
        // Iterate through the children to find a button near the label
        for (javafx.scene.Node node : mapContainer.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if(button.getAccessibleText().equals(tableLabel)){
                    return button;
                }
            }
        }
        return null;
    }

    @Subscribe
    public void addTablesToMap(tablesStatus tablesStatus) {
        if (offline) {
            handleOfflineChanges(tablesStatus);
            return;
        }
        Platform.runLater(() -> {
            List<TableNode> tables = tablesStatus.getTables();
            List<String> statuses = tablesStatus.getStatuses();

            System.out.println("The number of tables in this restaurant are: " + tables.size());
            mapContainer.getChildren().clear(); // Clear previous tables

            // Re-add the status description label
            addStatusDescriptionLabel();

            Image tableImage = new Image(getClass().getResourceAsStream("/images/table.png"));

            // Define table sizes and spacing
            double tableWidth = 100, tableHeight = 100;
            double horizontalSpacing = 20, verticalSpacing = 100;
            double startX = 50, startY = 80; // Adjusted startY to account for the label

            // Separate inside and outside tables and their corresponding statuses
            List<TableNode> insideTables = new ArrayList<>();
            List<String> insideStatuses = new ArrayList<>();
            List<TableNode> outsideTables = new ArrayList<>();
            List<String> outsideStatuses = new ArrayList<>();

            for (int i = 0; i < tables.size(); i++) {
                TableNode table = tables.get(i);
                String status = statuses.get(i);

                if (table.isInside()) {
                    insideTables.add(table);
                    insideStatuses.add(status);
                } else {
                    outsideTables.add(table);
                    outsideStatuses.add(status);
                }
            }

            // Track the current Y position
            double currentY = startY;

            // Add inside tables first
            if (!insideTables.isEmpty()) {
                currentY = addTablesToUI("Inside Tables", insideTables, insideStatuses, tableImage, startX, currentY, tableWidth, tableHeight, horizontalSpacing, verticalSpacing);
            }

            // Add some extra space before outside tables
            currentY += 80;

            // Add outside tables
            if (!outsideTables.isEmpty()) {
                addTablesToUI("Outside Tables", outsideTables, outsideStatuses, tableImage, startX, currentY, tableWidth, tableHeight, horizontalSpacing, verticalSpacing);
            }
        });
    }

    private double addTablesToUI(String title, List<TableNode> tables, List<String> statuses, Image tableImage,
                                 double startX, double startY, double tableWidth, double tableHeight,
                                 double horizontalSpacing, double verticalSpacing) {

        if (tables.isEmpty()) return startY; // Skip if no tables

        System.out.println("Adding section: " + title);

        // Create section label
        Label sectionLabel = new Label(title);
        sectionLabel.setLayoutX(startX);
        sectionLabel.setLayoutY(startY);
        sectionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
        mapContainer.getChildren().add(sectionLabel);

        double yOffset = startY + 80; // Space below the label

        // Calculate the number of rows needed
        int numRows = (int) Math.ceil((double) tables.size() / 5);

        for (int i = 0; i < tables.size(); i++) {
            TableNode table = tables.get(i);
            String status = statuses.get(i); // Get the status for the current table
            int row = i / 5, col = i % 5;
            double x = startX + col * (tableWidth + horizontalSpacing);
            double y = yOffset + row * (tableHeight + verticalSpacing);

            // Debugging: Ensure outside tables have different Y positions
            System.out.println(title + " - Table ID: " + table.getTableID() + " at (X=" + x + ", Y=" + y + ")");

            // Create ImageView
            ImageView tableView = new ImageView(tableImage);
            tableView.setFitWidth(tableWidth);
            tableView.setFitHeight(tableHeight);
            tableView.setLayoutX(x);
            tableView.setLayoutY(y);

            // Label for table ID
            Label tableLabel = new Label("Table " + table.getTableID());
            tableLabel.setLayoutX(x + 10);
            tableLabel.setLayoutY(y - 25);
            tableLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-background-color: rgba(255,255,255,0.8);");

            // Button for table details
            Button tableButton = new Button("Details");
            tableButton.setLayoutX(x + 10);
            tableButton.setLayoutY(y + tableHeight + 10);
            tableButton.setAccessibleText(""+table.getTableID());

            // Set button color based on table status
            String buttonColor = getButtonColorForStatus(status);
            tableButton.setStyle("-fx-background-color: " + buttonColor + "; -fx-text-fill: white;");
            tableButton.setOnAction(event -> showTableDetails(table));

            mapContainer.getChildren().addAll(tableView, tableLabel, tableButton);
        }

        // Calculate the total height used by this section
        double sectionHeight = 40 + (numRows * (tableHeight + verticalSpacing));

        // Return the updated Y position for the next section
        return startY + sectionHeight;
    }

    private String getButtonColorForStatus(String status) {
        switch (status.toLowerCase()) {
            case "available":
                return "#2ecc71"; // Green
            case "reserved":
                return "#f1c40f"; // Yellow
            case "occupied":
                return "#e74c3c"; // Red
            default:
                return "#3498db"; // Default blue
        }
    }

    @Subscribe
    public void handleTableDetailsResponse(String tableDetails) {
        // Call the showTableDetails method with the received table details
        showDetails(tableDetails);
    }

    @Subscribe
    public void reLoadTables(ReConfirmEvent event) {
        System.out.println("Reloading tables");
        offline = true;
        handleRestaurantSelection();
    }

    private void showDetails(String tableDetails) {
        Platform.runLater(() -> {
            // Create a dialog to show the table details
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Table Details");
            alert.setHeaderText("Table Details");

            // Set the content text (the table details string)
            alert.setContentText(tableDetails);

            // Show the dialog
            alert.showAndWait();
        });
    }

    @FXML
    void backToHome(ActionEvent event) throws IOException {
        Platform.runLater(() -> {
            try {
                EventBus.getDefault().unregister(this);
                App.setRoot("worker_screen"); // Navigate back to the main screen
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showTableDetails(TableNode table) {
        // Send a message to the server to fetch the table details
        try {
            SimpleClient.getClient().sendToServer(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupAutoRefresh() {
        // Get current time
        LocalTime now = LocalTime.now();

        // Calculate minutes past the last 15-minute interval
        int minutesPast = now.getMinute() % 15;

        // Calculate seconds past the last 15-minute interval
        int secondsPast = now.getSecond();

        // Calculate milliseconds past the last 15-minute interval
        int millisPast = now.getNano() / 1_000_000;

        // Calculate delay until next 15-minute mark
        long initialDelayMillis = ((15 - minutesPast) * 60 - secondsPast) * 1000L - millisPast;

        // Create initial delay
        PauseTransition initialDelay = new PauseTransition(Duration.millis(initialDelayMillis));
        initialDelay.setOnFinished(event -> {
            // First refresh at the next 15-minute mark
            refreshTables();

            // Then set up regular 15-minute refreshes
            refreshTimeline = new Timeline(
                    new KeyFrame(Duration.minutes(15), e -> refreshTables())
            );
            refreshTimeline.setCycleCount(Timeline.INDEFINITE);
            refreshTimeline.play();
        });

        initialDelay.play();
    }

    private void refreshTables() {
        if ( restaurantsComboBox.getValue() != null && !restaurantsComboBox.getValue().isEmpty()) {
            Platform.runLater(() -> {
                try {
                    System.out.println("Auto-refreshing tables for: " + restaurantsComboBox.getValue());
                    SimpleClient.getClient().sendToServer("getTablesForRestaurant: " + restaurantsComboBox.getValue());
                } catch (IOException e) {
                    System.err.println("Failed to send refresh message: " + e.getMessage());
                }
            });
        }
    }
}