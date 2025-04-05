package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MealInTheCart;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javassist.Loader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.done_Order;
import static il.cshaifasweng.OCSFMediatorExample.client.menu_controller.branchName;

public class OrderSummaryController {

    @FXML
    private Label orderSummaryTitle;
    @FXML
    private Button CheckOutbtn;
    @FXML
    private VBox mealDetailsContainer;  // VBox to hold meal details
    @FXML
    private Label orderItemsLabel;
    @FXML
    private Label totalAmountLabel;

    private Stage summaryStage;

    public List<MealInTheCart> meals;

    public void setSummary(Stage stage, String orderDetails, String totalAmount) {
        summaryStage = stage;
        orderItemsLabel.setText(orderDetails);
        totalAmountLabel.setText( totalAmount);
    }

    public void setMeals(List<MealInTheCart> meals) {
        this.meals = meals;
    }
    public List<MealInTheCart> getMeals() {
        return meals;
    }

    @FXML
    void CheckOut(ActionEvent event) {

        System.out.println("CheckOut");
        Order newOrder = new Order();
        newOrder.setRestaurantName(branchName);
        newOrder.setTotal_price(calculateTotalPrice(meals));
        newOrder.setOrderStatus("pending");


        // Set the Restaurant Name for each meal in the cart
        for (MealInTheCart mealInTheCart : meals) {
            mealInTheCart.setRestaurantName(branchName); // Ensure this is set
            mealInTheCart.setOrder(newOrder); // Associate MealInTheCart with the Order
        }

        newOrder.setMeals(meals); // Set the meals in the order
        done_Order = newOrder;
        try {
            if (summaryStage != null) {
                summaryStage.close();
            }
            App.setRoot("PersonalDetailsPage");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int calculateTotalPrice(List<MealInTheCart> meals) {
        int totalPrice = 0;

        for (MealInTheCart mealInTheCart : meals) {
            int mealPrice = (int) mealInTheCart.getMeal().getMeal().getPrice();  // Make sure to cast to the appropriate type
            int quantity = mealInTheCart.getQuantity();
            totalPrice += mealPrice * quantity; // Multiply price by quantity and add to total
        }

        return totalPrice;
    }
    public VBox getMealDetailsContainer() {
        return mealDetailsContainer;
    }

    public Button getCheckOutbtn() {
        return CheckOutbtn;
    }

}