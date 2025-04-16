package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.CancelOrderEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import javafx.animation.FadeTransition;


public class OrderCancellationController {

    @FXML
    private Button CancelButton;

    @FXML
    private TextField ID;

    @FXML
    private TextField emailField;

    @FXML
    private Label idErrorLabel;

    @FXML
    public void initialize(){
        EventBus.getDefault().register(this);
        idErrorLabel.setText("");
    }

    @FXML
    void CancelOrder(ActionEvent event) {
        String orderNumber = ID.getText();
        String customerEmail = emailField.getText();
        if (orderNumber == null || orderNumber.trim().isEmpty() || customerEmail == null || customerEmail.trim().isEmpty()) {
            idErrorLabel.setText("No field cannot be empty !");
            idErrorLabel.setVisible(true);
        } else if (!orderNumber.matches("\\d+")) {
            idErrorLabel.setText("Order number must contain only numbers !");
            idErrorLabel.setVisible(true);
        } else {  //if the order number entered is correct
            CancelOrderEvent cancelOrder = new CancelOrderEvent(orderNumber, customerEmail);
            try {
                SimpleClient.getClient().sendToServer(cancelOrder);
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
            if (cancelOrderEvent.getStatus().startsWith("Order not found with email:")) {
                Platform.runLater(() -> {
                    try {
                        idErrorLabel.setVisible(true);
                        idErrorLabel.setText(cancelOrderEvent.getStatus());}
                    catch (Exception e) {
                        e.printStackTrace();}
                });
            } else if (cancelOrderEvent.getStatus().startsWith("Order found")) {

            //display a success message
            Platform.runLater(() -> {
                try {
                    String subject = "Refund from Mamas-Restaurant ";
                    String body = "Hi,\n\nWe've received a request to cancel your order. "
                            + "Your refund is : " + String.format("%.2f", refundAmount) + "\n\n"
                            + "If you didn't request this, please ignore this email.\n\n"
                            + "Best regards,\nMamas-Restaurant Team";


                    EmailSender.sendEmail(subject,body, cancelOrderEvent.getOrder().getCustomerEmail());
                    CancelButton.getScene().getWindow().hide();

                    showSavedPopup();

                } catch (Exception e) {
                    // Handle email sending failure
                    System.out.println("handleCancelOrderResponse");
                    e.printStackTrace();
                }
            });
            }
            else {
                Platform.runLater(() -> {
                    try {
                        idErrorLabel.setVisible(true);
                        idErrorLabel.setText("this order has been cancelled before");}
                    catch (Exception e) {
                        e.printStackTrace();}
                });
            }
        } else {
            // Handle case when order was not found
            Warning errorMessage = new Warning("Order not found.");
            Platform.runLater(() -> {
                try {
            idErrorLabel.setVisible(true);
            idErrorLabel.setText("Order not found.");}
                catch (Exception e) {
                e.printStackTrace();
                }
            });

        }
        System.out.println("calculateRefund");
    }

    private void showSavedPopup() {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.TRANSPARENT);

        Label savedLabel = new Label("✔ Mail Sent");
        savedLabel.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 30;");
        StackPane root = new StackPane(savedLabel);
        root.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(root);
        scene.setFill(null);
        popupStage.setScene(scene);
        popupStage.setAlwaysOnTop(true);

        // Center on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        popupStage.setX(screenBounds.getMinX() + screenBounds.getWidth() / 2 - 100);
        popupStage.setY(screenBounds.getMinY() + screenBounds.getHeight() / 2 - 50);

        popupStage.show();

        // Fade out transition using fully qualified javafx.util.Duration
        FadeTransition fade = new FadeTransition(javafx.util.Duration.seconds(2), root);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(javafx.util.Duration.seconds(1));
        fade.setOnFinished(event -> popupStage.close());
        fade.play();
    }


    private double calculateRefund(Order order) {
        LocalDateTime deliveryTime = order.getOrderTime(); // Time client wants to receive order
        LocalDateTime now = LocalDateTime.now();

        // Calculate time remaining until delivery
        Duration duration = Duration.between(now, deliveryTime);
        long hoursRemaining = duration.toHours();
        System.out.println("delivery time is : "+deliveryTime);


        System.out.println("Hours remaining until delivery: " + hoursRemaining);

        if (hoursRemaining >= 3) {
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
