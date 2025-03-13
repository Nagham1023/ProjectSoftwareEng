package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    public void setSummary(Stage stage, String orderDetails, String totalAmount) {
        summaryStage = stage;
        orderItemsLabel.setText(orderDetails);
        totalAmountLabel.setText( totalAmount);
    }

    @FXML
    void CheckOut(ActionEvent event) {

    }
    public VBox getMealDetailsContainer() {
        return mealDetailsContainer;
    }
}
