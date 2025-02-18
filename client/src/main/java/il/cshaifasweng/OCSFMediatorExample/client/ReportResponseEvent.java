package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.Serializable;
import java.time.LocalTime;

public class ReportResponseEvent implements Serializable {
    private String report;
    private LocalTime time;

    public ReportResponseEvent(String report) {
        this.report = report;
        this.time = LocalTime.now();
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
    @Override
    public String toString() {
        return  report;
    }
}
