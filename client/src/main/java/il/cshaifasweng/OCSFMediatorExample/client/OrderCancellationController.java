package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.CancelOrderEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class OrderCancellationController {

    @FXML
    private Button CancelButton;

    @FXML
    private TextField OrderNumber;

    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
    }

    @FXML
    void CancelOrder(ActionEvent event) {
        String orderNumber = OrderNumber.getText();
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            //show a warning if the order number field is empty
            //Warning warning1 = new Warning("Please enter a valid order number.");

        } else {  //if the order number entered is correct
            CancelOrderEvent cancelEvent = new CancelOrderEvent(orderNumber);
            try {
                SimpleClient.getClient().sendToServer(cancelEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void handleCancelOrderResponse(CancelOrderEvent cancelOrderEvent) {

        if (cancelOrderEvent.getOrder() != null) {
            //display refund amount
            double refundAmount = calculateRefund(cancelOrderEvent.getOrder());

            //display a success message
            Platform.runLater(() -> {
                try {
                    String subject = "Refund from Mamas-Restaurant ";
                    String body = "Hi,\n\nWe've received a request to cancel your order. "
                            + "Your refund is : " + refundAmount + "\n\n"
                            + "If you didn't request this, please ignore this email.\n\n"
                            + "Best regards,\nMamas-Restaurant Team";

                    // Call EmailSender to send the email
                    //EmailSender.sendEmail(response.getEmail(), subject, body);
                    EmailSender.sendEmail(subject,body, cancelOrderEvent.getOrder().getCustomerEmail());
                } catch (Exception e) {
                    // Handle email sending failure
                    System.out.println("handleCancelOrderResponse");
                    e.printStackTrace();
                }
            });
        } else {
            // Handle case when order was not found
            Warning errorMessage = new Warning("Order not found.");
            EventBus.getDefault().post(errorMessage);
        }
        System.out.println("calculateRefund");
    }

    private double calculateRefund(Order order) {
        LocalDateTime deliveryTime = order.getOrderTime(); // Time client wants to receive order
        LocalDateTime now = LocalDateTime.now();

        // Calculate time remaining until delivery
        Duration duration = Duration.between(now, deliveryTime);
        long hoursRemaining = duration.toHours();

        System.out.println("Hours remaining until delivery: " + hoursRemaining);

        if (hoursRemaining > 3) {
            // More than 3 hours remaining - full refund
            return order.getTotal_price();
        } else if (hoursRemaining > 1) {
            // 1-3 hours remaining - 50% refund
            return order.getTotal_price() * 0.5;
        } else {
            // Less than 1 hour remaining - no refund
            // Also handles negative values (delivery time already passed)
            return 0.0;
        }
    }
}
