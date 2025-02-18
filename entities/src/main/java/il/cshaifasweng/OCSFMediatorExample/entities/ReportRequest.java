package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalDate;

import java.io.Serializable;
import java.time.LocalDate;

public class ReportRequest implements Serializable {
    private LocalDate date;         // The base date for the report
    private TimeFrame timeFrame;   // YEARLY or MONTHLY
    private String reportType;     // Type of report (e.g., "REVENUE", "SALES")
    private String targetRestaurant;   //The name of the restaurant

    // Constructor
    public ReportRequest(LocalDate date, TimeFrame timeFrame, String reportType) {
        this.date = date;
        this.timeFrame = timeFrame;
        this.reportType = reportType;
        this.targetRestaurant = "ALL";
    }
    public ReportRequest(LocalDate date, TimeFrame timeFrame, String reportType, String targetRestaurant) {
        this.date = date;
        this.timeFrame = timeFrame;
        this.reportType = reportType;
        this.targetRestaurant = targetRestaurant;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTargetRestaurant() {
        return targetRestaurant;
    }

    public void setTargetRestaurant(String targetRestaurant) {
        this.targetRestaurant = targetRestaurant;
    }

    @Override
    public String toString() {
        return "generateReport {" +
                "date=" + date +
                ", timeFrame=" + timeFrame +
                ", RestaurantName='" + targetRestaurant+
                ", reportType='" + reportType + '\'' +
                '}';
    }
}


