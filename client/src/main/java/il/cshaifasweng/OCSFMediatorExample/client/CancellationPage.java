package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CancellationPage {
    @FXML
    private void openCancelOrderPage(){
        Platform.runLater(()->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/OrderCancellation.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    @FXML
    private void openCancelReservationPage(){
        Platform.runLater(()->{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/il/cshaifasweng/OCSFMediatorExample/client/ReservationCancellation.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
    @FXML
    void backToMainScreen(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("mainScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }




}
