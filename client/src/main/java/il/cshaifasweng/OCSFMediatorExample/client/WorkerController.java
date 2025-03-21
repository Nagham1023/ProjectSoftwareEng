package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.ReportResponseEvent;
import javafx.application.Platform; // Add this import
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class WorkerController {
    @FXML
    private Button branches_managment;

    @FXML
    private StackPane chartArea;

    @FXML
    private Button complaint_center;

    @FXML
    private Button personalInf;

    @FXML
    private Button priceChange_requist;

    @FXML
    private Button reports_center;

    @FXML
    private Button tables_map;

    @FXML
    private Button tables_reservation;

    @FXML
    private Button update_meals;

    @FXML
    private Button Reservation;

    private String currentWorker = "";

    @FXML
    void initialize() {
        try {
            SimpleClient client = SimpleClient.getClient();
            if (client.getUser() != null) {
                currentWorker = client.getUser().getRole();
            } else {
                System.err.println("User is null. Cannot initialize WorkerController.");
            }

            // Set button visibility
            branches_managment.setVisible(false);
            complaint_center.setVisible(false);
            personalInf.setVisible(true);
            priceChange_requist.setVisible(false);
            reports_center.setVisible(false);
            tables_map.setVisible(true);
            tables_reservation.setVisible(true);
            update_meals.setVisible(false);

            switch (currentWorker) {
                case "Host":
                    tables_reservation.setVisible(true);
                    break;
                case "CustomerService":
                    complaint_center.setVisible(true);
                    break;
                case "Dietation":
                    update_meals.setVisible(true);
                    break;
                case "ChainManager":
                    reports_center.setVisible(true);
                    break;
                case "CompanyManager":
                    reports_center.setVisible(true);
                    branches_managment.setVisible(true);
                    priceChange_requist.setVisible(true);
                    break;
                default:
                    System.err.println("Unknown role: " + currentWorker);
                    break;
            }

            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during initialization: " + e.getMessage());
        }
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }

    public void setRole(String role) {
        this.currentWorker = role;
    }

//    @FXML
//    public void onReportsButtonClick() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReportsView.fxml"));
//            Node reportView = loader.load();
//
//            Platform.runLater(() -> {
//                if (chartArea != null) {
//                    chartArea.getChildren().clear();
//                    chartArea.getChildren().add(reportView);
//
//                    // Pass data to the ReportsViewController if needed
//                    ReportsViewController reportsController = loader.getController();
//                    reportsController.setRole(currentWorker);
//                } else {
//                    System.err.println("chartArea is null. Cannot load report view.");
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Failed to load ReportsView.fxml: " + e.getMessage());
//        }
//    }
    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node view = loader.load();

            Platform.runLater(() -> {
                if (chartArea != null) {
                    chartArea.getChildren().clear();
                    chartArea.getChildren().add(view);
                } else {
                    System.err.println("chartArea is null. Cannot load view.");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
        }
    }

    @FXML
    public void switchScreen(String screenName) {
        Platform.runLater(() -> {
            loadView(screenName + ".fxml");
        });
    }

    @FXML
    public void onButtonClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String screenName = clickedButton.getId(); // Get the fx:id of the button
        switch (screenName){
            case "complaint_center":
                switchScreen("customerServiceView");
            case "reports_center":
                switchScreen("ReportsView");



        }

    }
    @FXML
    public void goToComplainsView(ActionEvent event) {
        switchScreen("customerServiceView");
    }

    @Subscribe
    public void onEvent(ReportResponseEvent event) {
        Platform.runLater(() -> {
            System.out.println("Event received: " + event.toString());
            // Add any UI updates here
        });
    }

    @FXML
    void goToMap(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("RestaurantMap");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    @FXML
    void goToReservation(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                App.setRoot("Wroker-Reservation");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}