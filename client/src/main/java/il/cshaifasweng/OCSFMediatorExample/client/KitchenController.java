package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class KitchenController {

    @FXML private VBox ordersContainer;
    @FXML private HBox row1;
    @FXML private HBox row2;
    @FXML private ScrollPane row1ScrollPane;
    @FXML private ScrollPane row2ScrollPane;
    @FXML private Slider orderSlider;

    private ObservableList<Order> orders = FXCollections.observableArrayList();
    private double orderWidth = 470;
    private String branchName;


    @FXML
    public void initialize() {
        try {
            branchName = "Nazareth";
            EventBus.getDefault().register(this);
            configureSlider();
            setupSliderListener();
            requestOrders();
        } catch (Exception e) {
            showAlert("Initialization Error", "Failed to initialize kitchen controller: " + e.getMessage());
        }
    }
    public void setBranch(String branch) {
        this.branchName = branch;
    }

    private void requestOrders() throws IOException {
        SimpleClient.getClient().sendToServer("getOrders"+branchName);
    }

    private void setupSliderListener() {
        orderSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (row1.getWidth() > 0 && row2.getWidth() > 0) {
                    double scrollPosition = (newVal.doubleValue() / 100) *
                            (row1.getWidth() - row1ScrollPane.getWidth());
                    row1ScrollPane.setHvalue(scrollPosition / row1.getWidth());
                    row2ScrollPane.setHvalue(scrollPosition / row2.getWidth());
                }
            });
        });
    }

    @Subscribe
    public void handleOrders(List<Order> receivedOrders) {
        // Process data in background thread
        List<Order> sortedOrders = sortOrdersByDate(receivedOrders);

        // Update UI on FX thread
        Platform.runLater(() -> {
            clearOrderRows();
            displayOrders(sortedOrders);
            configureSlider();
        });
    }

    @Subscribe
    public void handleNewOrder(newOrderAdded receivedOrder) {
        if(receivedOrder.getOrder() != null)
            if (receivedOrder.getOrder().getRestaurantName().equals(branchName))
            {
                System.out.println("New Order!!");
                shutdown();
                initialize();
            }
    }
    @Subscribe
    public void handleDoneOrder(String str) {
        if(str.equals("orderisdone"))
        {
            System.out.println("Done Order!!");
            shutdown();
            initialize();
        }
    }

    private List<Order> sortOrdersByDate(List<Order> orders) {
        if (orders == null) return new ArrayList<>();
        // Create a new list to avoid modifying the original
        List<Order> sorted = new ArrayList<>(orders);
        sorted.sort(Comparator.comparing(Order::getOrderTime));
        return sorted;
    }

    private void clearOrderRows() {
        row1.getChildren().clear();
        row2.getChildren().clear();
    }

    private void displayOrders(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            showAlert("No Orders", "There are currently no orders to display.");
            return;
        }

        for (int i = 0; i < orders.size(); i++) {
            try {
                Order order = orders.get(i);
                VBox orderCard = createOrderCard(order);

                // Distribute orders between rows
                if (i % 2 == 0) {
                    row1.getChildren().add(orderCard);
                } else {
                    row2.getChildren().add(orderCard);
                }
            } catch (Exception e) {
                System.err.println("Error displaying order #" + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private VBox createOrderCard(Order order) {
        VBox container = new VBox();
        container.getStyleClass().add("order-container");
        container.setPrefWidth(450);

        // Main content area with scroll
        VBox content = new VBox(10);
        content.getStyleClass().add("order-content");

        // Order header
        Label orderHeader = new Label(String.format("Order #%d - %s",
                order.getId(),
                order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        orderHeader.getStyleClass().add("order-header");
        content.getChildren().add(orderHeader);

        // Meals list in scroll pane
        VBox mealsBox = new VBox(5);
        for (MealInTheCart mealInCart : order.getMeals()) {
            Meal meal = mealInCart.getMeal().getMeal();
            Label mealName = new Label("• " + meal.getName() + " (x" + mealInCart.getQuantity() + ")");
            mealName.getStyleClass().add("meal-name");
            mealsBox.getChildren().add(mealName);

            // Handle customizations with selection status
            Set<CustomizationWithBoolean> customizations = mealInCart.getMeal().getCustomizationsList();
            if (customizations != null && !customizations.isEmpty()) {
                VBox customizationsBox = new VBox(2);
                customizationsBox.getStyleClass().add("customizations-box");

                for (CustomizationWithBoolean cwb : customizations) {
                    HBox customizationItem = new HBox(5);
                    customizationItem.getStyleClass().add("customization-item");

                    // Selection indicator
                    Label selectionIndicator = new Label(cwb.getValue() ? "✓" : "✗");
                    selectionIndicator.getStyleClass().add(cwb.getValue() ? "selected-indicator" : "not-selected-indicator");

                    // Customization name
                    Label customizationName = new Label(cwb.getCustomization().getName());
                    customizationName.getStyleClass().add("customization-text");

                    customizationItem.getChildren().addAll(selectionIndicator, customizationName);
                    customizationsBox.getChildren().add(customizationItem);
                }

                mealsBox.getChildren().add(customizationsBox);
            }
        }

        ScrollPane mealsScroll = new ScrollPane(mealsBox);
        mealsScroll.getStyleClass().add("meals-scroll");
        mealsScroll.setFitToWidth(true);
        content.getChildren().add(mealsScroll);

        // Ready button
        Button readyButton = new Button("Mark as Ready");
        readyButton.getStyleClass().add("ready-button");
        readyButton.setOnAction(e -> handleOrderCompletion(order, container, readyButton));
        content.getChildren().add(readyButton);

        container.getChildren().add(content);
        return container;
    }

    private void handleOrderCompletion(Order order, VBox container, Button readyButton) {
        try {
            // Mark order as completed in the UI
            container.getStyleClass().add("completed-order");
            readyButton.setDisable(true);
            readyButton.setText("Completed");

            // Send completion notification to server
            SimpleClient.getClient().sendToServer("doneOrder"+order.getId());

        } catch (IOException e) {
            Platform.runLater(() ->
                    showAlert("Network Error", "Failed to update order status: " + e.getMessage()));
        }
    }

    private void configureSlider() {
        Platform.runLater(() -> {
            int orderCount = Math.max(row1.getChildren().size(), row2.getChildren().size());
            double totalWidth = orderCount * orderWidth;

            boolean needsSlider = totalWidth > row1ScrollPane.getWidth();
            orderSlider.setDisable(!needsSlider);
            orderSlider.setVisible(needsSlider);

            if (needsSlider) {
                orderSlider.setMax(100);
                orderSlider.setValue(0);
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void shutdown() {
        EventBus.getDefault().unregister(this);
    }
}