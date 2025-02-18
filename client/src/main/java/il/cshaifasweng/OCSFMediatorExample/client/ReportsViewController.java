package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform; // Add this import
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
            client.sendToServer("getAllRestaurants");
        } catch (Exception e) {
            e.printStackTrace(); // In a real application, log this error or show an error message to the user
        }

        // Add options to the ComboBox
        //restaurant_name.getItems().addAll("Option 1", "Nazareth", "ALL");
        // Add options to the ComboBox
        reportType.getItems().addAll("revenueReport", "Option 2", "Option 3");
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
        String reportOutput = event.getReport();
        Platform.runLater(() -> loadChartData(reportOutput));
    }

    public void loadChartData(String reportOutput) {
        // Determine report type and time frame from content
        boolean isMonthlyReport = reportOutput.contains("Daily");
        boolean isYearlyReport = reportOutput.contains("Yearly");

        // Parse the report output
        String[] lines = reportOutput.split("\n");
        Map<String, Double> revenueData = new LinkedHashMap<>();  // Maintain insertion order
        LocalDate baseDate = LocalDate.now();  // Fallback to current date

        try {
            // Ensure "-" exists before splitting
            if (lines[0].contains("-")) {
                String dateHeader = lines[0].split("- ")[1];

                if (isMonthlyReport) {
                    String[] monthYear = lines[1].split(" ");
                    baseDate = LocalDate.of(baseDate.getYear(), baseDate.getMonthValue(), 1);
                } else if (isYearlyReport) {
                    int year = baseDate.getYear();
                    baseDate = LocalDate.of(year, 1, 1);
                }
            } else {
                System.err.println("Report header does not contain a date.");
                baseDate = LocalDate.now(); // Default to today if parsing fails
            }
        }
        catch (Exception e) {
            System.err.println("Error parsing report date: " + e.getMessage());
        }


        // Parse revenue data
        for (String line : lines) {
            if (line.startsWith("Day ") || line.startsWith("Month ")) {
                String[] parts = line.split(": \\$");
                String period = parts[0].split(" ")[1];
                double revenue = Double.parseDouble(parts[1].trim());
                revenueData.put(period, revenue);
            }
        }

        // Create chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(lines[0]);  // Use the actual report header

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");

        // Fill complete date range
        if (isMonthlyReport) {
            LocalDate startDate = baseDate.withDayOfMonth(1);
            LocalDate endDate = baseDate.withDayOfMonth(baseDate.lengthOfMonth());

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String day = String.valueOf(date.getDayOfMonth());
                series.getData().add(new XYChart.Data<>(
                        day,
                        revenueData.getOrDefault(day, 0.0)
                ));
            }
        } else if (isYearlyReport) {
            for (int month = 1; month <= 12; month++) {
                String monthStr = String.valueOf(month);
                series.getData().add(new XYChart.Data<>(
                        Month.of(month).toString(),
                        revenueData.getOrDefault(monthStr, 0.0)
                ));
            }
        }

        barChart.getData().add(series);

        // Style and display
        try {
            barChart.getStylesheets().add(
                    getClass().getResource("chart-style.css").toExternalForm()
            );
        } catch (NullPointerException e) {
            System.err.println("Chart stylesheet not found");
        }

        chartArea.getChildren().clear();
        chartArea.getChildren().add(barChart);
    }

    @FXML
    private void handleCellClick(javafx.event.ActionEvent event) {
        // Create the ReportRequest object
        ReportRequest req = new ReportRequest(
                LocalDate.now(),          // Use the current date for now (adjust as needed)
                TimeFrame.valueOf(timeValue), // Convert timeValue ("MONTHLY" or "YEARLY") to TimeFrame enum
                typeValue,               // Report type
                nameValue                // Target restaurant name
        );
        // Now we have to tell the server
        try {
            SimpleClient.getClient().sendToServer(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Subscribe
    public void fillComboBox(RestaurantList restaurantList) {
        restaurant_name.getItems().clear();
        List<Restaurant> restaurants = restaurantList.getRestaurantList();
        List<String> restaurantNames = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            restaurant_name.getItems().add(restaurant.getRestaurantName()); // Add each restaurant name
        }

    }

    public void setRole(String role) {
        this.currentWorker = role;
    }
}