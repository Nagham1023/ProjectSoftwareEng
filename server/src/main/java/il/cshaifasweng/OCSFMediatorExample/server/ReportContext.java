package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import org.hibernate.Session;

import java.time.LocalDate;

public class ReportContext {
    private ReportStrategy reportStrategy;

    public ReportContext(ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }

    public void setReportStrategy(ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }

    public String generateReport(LocalDate month, String restaurantName, TimeFrame timeFrame) {
        return reportStrategy.generate(month, restaurantName, timeFrame);
    }
}
