package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import java.time.LocalDate;

public class ComplainReport implements ReportStrategy {
    @Override
    public String generate(LocalDate month, String restaurantName, TimeFrame timeFrame, String note) {
        ReportDB reportDB = new ReportDB();
        return reportDB.generate_complain_report(month, restaurantName, timeFrame, note);
    }
}