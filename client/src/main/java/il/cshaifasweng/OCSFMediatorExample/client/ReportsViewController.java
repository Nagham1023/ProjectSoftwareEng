package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.ReportResponseEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform; // Add this import
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.restaurantList;

public class ReportsViewController {

    @FXML
    private StackPane chartArea;

    @FXML
    private Button generate;

    @FXML
    private ComboBox<String> reportType;

    @FXML
    private ComboBox<String> restaurant_name;

    @FXML
    private ComboBox<String> timeStamp;

    private String currentWorker = "";
    private String currentRestaurant = "";
    private String nameValue = "";
    private String typeValue = "";
    private String timeValue = "";

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        if (currentWorker.equals("ChainManager"))
            restaurant_name.setVisible(false);
        SimpleClient client = SimpleClient.getClient();
        try {
            if(restaurantList == null)
                client.sendToServer("getAllRestaurants");
            else fillComboBox(restaurantList);
            //System.out.println("here first");
        } catch (Exception e) {
            e.printStackTrace(); // In a real application, log this error or show an error message to the user
        }

        // Add options to the ComboBox
        //restaurant_name.getItems().addAll("Option 1", "Nazareth", "ALL");
        // Add options to the ComboBox
        reportType.getItems().addAll("revenueReport", "deliveryReport", "pickupReport","allOrdersReport", "ComplainReport", "VisitorsReport");
        // Add options to the ComboBox
        timeStamp.getItems().addAll("MONTHLY", "YEARLY");

        // Set an event listener for selection changes
        restaurant_name.setOnAction(event -> {
            nameValue = restaurant_name.getValue();
        });
        // Set an event listener for selection changes
        timeStamp.setOnAction(event -> {
            timeValue = timeStamp.getValue();
        });
        // Set an event listener for selection changes
        reportType.setOnAction(event -> {
            typeValue = reportType.getValue();
        });
    }

    @Subscribe
    public void handleReportResponse(ReportResponseEvent event) {

        System.out.println("received report");
        String reportOutput = event.getReport();
        Platform.runLater(() -> loadChartData(reportOutput));
    }

    public void loadChartData(String reportOutput) {
        //System.out.println("Raw Report Data:\n" + reportOutput); // Debug log

        // Parse the report output
        String[] lines = reportOutput.split("\n");
        Map<String, Double> revenueData = new LinkedHashMap<>();
        LocalDate baseDate = LocalDate.now();
        boolean isMonthly = reportOutput.contains("Monthly");


        try {
            // Parse header for date context

            if (lines.length > 1 && lines[1].startsWith("Period: ")) {
                String[] dateParts = lines[1].split("Period: ")[1].split(" ");
                Month month = Month.valueOf(dateParts[0].toUpperCase());
                int year = Integer.parseInt(dateParts[1]);
                baseDate = LocalDate.of(year, month, 1);
            }
        } catch (Exception e) {
            System.err.println("Error parsing date header: " + e.getMessage());
        }

        // Parse data lines
        for (String line : lines) {
            try {
                if (line.startsWith("Day ") || line.startsWith("Month ")) {
                    String[] parts = line.split(": \\$");
                    String period = parts[0].split(" ")[1];
                    int revenue = Integer.parseInt(parts[1].trim());
                    revenueData.put(period, (double) revenue);
                }
            } catch (Exception e) {
                System.err.println("Error parsing line: " + line);
            }
        }

        // Create chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(lines[0]);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        String seriesName = switch (lines[0].split("-")[0]) {
            case "Revenue Report " -> "Revenue";
            case "Delivery Count Report " -> "Delivery Orders";
            case "Self PickUp Count Report " -> "Pickup Orders";
            case "ALL Count Report " -> "All Orders";
            case "Complaint Report " -> "Customer Complaints";
            case "Visitors Report " -> "Visitors";
            default -> "Report";
        };
        series.setName(seriesName);

        chart.getStylesheets().add(getClass().getResource("chart-style.css").toExternalForm());

        // Fill complete timeline
        if (isMonthly) {
            int daysInMonth = baseDate.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                String key = String.valueOf(day);
                series.getData().add(new XYChart.Data<>(
                        key,
                        revenueData.getOrDefault(key, 0.0)
                ));
            }
        } else {
            for (int month = 1; month <= 12; month++) {
                String key = String.valueOf(month);
                series.getData().add(new XYChart.Data<>(
                        Month.of(month).toString(),
                        revenueData.getOrDefault(key, 0.0)
                ));
            }
        }

        chart.getData().add(series);
        chartArea.getChildren().clear();
        chartArea.getChildren().add(chart);
    }

    @FXML
    private void handleCellClick(javafx.event.ActionEvent event) {
        ReportRequest req;

        if (timeValue == null) timeValue = timeStamp.getValue();
        if (typeValue == null) typeValue = reportType.getValue();
        if (nameValue == null && !currentWorker.equals("ChainManager")) {
            nameValue = restaurant_name.getValue();
        }
        if (timeValue == null || typeValue == null || (!currentWorker.equals("ChainManager") && nameValue == null)) {
            System.out.println("Please fill all fields before generating the report.");
            return;
        }

        // Create the ReportRequest object
        if(currentWorker.equals("ChainManager")) {
            req=new ReportRequest(
                    LocalDate.now(),          // Use the current date for now
                    TimeFrame.valueOf(timeValue), // Convert timeValue ("MONTHLY" or "YEARLY") to TimeFrame enum
                    typeValue,               // Report type
                    currentRestaurant                // Target restaurant name
            );
        } else{
            req = new ReportRequest(
                    LocalDate.now(),          // Use the current date for now (adjust as needed)
                    TimeFrame.valueOf(timeValue), // Convert timeValue ("MONTHLY" or "YEARLY") to TimeFrame enum
                    typeValue,               // Report type
                    nameValue                // Target restaurant name
            );}
        // Now we have to tell the server
        try {
            //System.out.println("Sending request: time=" + timeValue + ", type=" + typeValue + ", name=" + (currentWorker.equals("ChainManager") ? currentRestaurant : nameValue));
            SimpleClient.getClient().sendToServer(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void fillComboBox(RestaurantList restaurantList) {
        SimpleClient.restaurantList = restaurantList;
        restaurant_name.getItems().clear();
        //System.out.println("here");
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        List<String> restaurantNames = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            restaurant_name.getItems().add(restaurant.getRestaurantName()); // Add each restaurant name
        }
        restaurant_name.getItems().add("ALL");
    }

    public void setRole(String role) {
        this.currentWorker = role;
    }
    public void setBranch(String role) {
        this.currentRestaurant = role;
        System.out.println("Current restaurant: " + currentRestaurant);
        restaurant_name.setVisible(false);
    }
}