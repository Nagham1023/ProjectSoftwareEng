package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;


public class CancellationController {

    @FXML
    private Button CancelButton;

    @FXML
    private TextField ID;

    @FXML
    private Label idErroeLabel;

    @FXML
    void backToMainScreen(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("MainScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    void CancelOrder(ActionEvent event) {
        String orderNumber = ID.getText();
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            idErroeLabel.setText("Order number cannot be empty !");
            idErroeLabel.setVisible(true);
        } else if (!orderNumber.matches("\\d+")) {
            idErroeLabel.setText("Order number must contain only numbers !");
            idErroeLabel.setVisible(true);
        } else {  //if the order number entered is correct
            CancelOrderEvent cancelOrder = new CancelOrderEvent(orderNumber);
            try {
                SimpleClient.getClient().sendToServer(cancelOrder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void CancelReservation(ActionEvent event) {
        String reservationId = ID.getText();
        if (reservationId == null || reservationId.trim().isEmpty()) {
            idErroeLabel.setText("Order number cannot be empty !");
            idErroeLabel.setVisible(true);
        } else if (!reservationId.matches("\\d+")) {
            idErroeLabel.setText("Order number must contain only numbers !");
            idErroeLabel.setVisible(true);
        } else {
            CancelReservationEvent cancelReservation = new CancelReservationEvent(reservationId);
            try {
                SimpleClient.getClient().sendToServer(cancelReservation);
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
    }

    // Method to calculate the refund amount after order cancellation
    private double calculateRefund(Order order) {
        LocalDateTime orderTime = order.getOrderTime();
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilOrder = Duration.between(now, orderTime).toHours();

        if (hoursUntilOrder >= 3) {
            return order.getTotal_price(); // Full refund
        } else if (hoursUntilOrder >= 1) {
            return order.getTotal_price() * 0.5; // 50% refund
        } else {
            return 0.0; // No refund
        }
    }

    @Subscribe
    public void handleCancelReservationResponse(CancelReservationEvent cancelReservationEvent) {
        if (cancelReservationEvent.getReservationId() != null) {
            double cancellationfine = calculateFine(cancelReservationEvent.getReservation());
            //display a success message
            Platform.runLater(() -> {
                try {
                    if (cancellationfine > 0) {
                        String subject = "Your reservation in Mamas-Restaurant was cancelled ";
                        String body = "Hi,\n\nWe've received a request to cancel your reservation. "
                                + "Your reservation is successfully cancelled\n"
                                +"Cancellation fine is : " + cancellationfine + "\n\n"
                                + "If you didn't request this, please ignore this email.\n\n"
                                + "Best regards,\nMamas-Restaurant Team";
                        EmailSender.sendEmail(subject,body, cancelReservationEvent.getCustomerEmail());
                    } else {
                        String subject = "Your reservation in Mamas-Restaurant was cancelled ";
                        String body = "Hi,\n\nWe've received a request to cancel your reservation. "
                                + "Your reservation is successfully cancelled\n\n"
                                + "If you didn't request this, please ignore this email.\n\n"
                                + "Best regards,\nMamas-Restaurant Team";
                        EmailSender.sendEmail(subject,body, cancelReservationEvent.getCustomerEmail());
                    }
                } catch (Exception e) {
                    // Handle email sending failure
                    System.out.println("handleCancelReservationResponse");
                    e.printStackTrace();
                }
            });
        } else {
            // Handle case when order was not found
            Warning errorMessage = new Warning("Order not found.");
            EventBus.getDefault().post(errorMessage);
        }
    }

    public  double calculateFine(ReservationSave reservation) {
        int seats = reservation.getSeats();
        LocalDateTime reservationDateTime = reservation.getReservationDateTime();
        long hoursTillReservation = Duration.between(reservationDateTime, LocalDateTime.now()).toHours();

        if (hoursTillReservation <= 1) {
            double finePerSeat = 10;
            return seats * finePerSeat;
        }
        return 0.0;
    }
//    public double calculateFine(int seats, LocalDateTime reservationTime) {
//        long hoursTillReservation = Duration.between(reservationTime, LocalDateTime.now()).toHours();
//
//        if (hoursTillReservation <= 1) {
//            int finePerSeat = 10;
//            return seats * finePerSeat;
//        }
//        return 0.0;
//    }
}

