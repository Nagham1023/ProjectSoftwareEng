package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;

import java.time.LocalDate;

public class VisitorsReport implements ReportStrategy{
    @Override
    public String generate(LocalDate month, String restaurantName, TimeFrame timeFrame, String note) {
        ReportDB reportDB = new ReportDB();
        return reportDB.generate_visitors_report(month, restaurantName, timeFrame, note);
    }
}
