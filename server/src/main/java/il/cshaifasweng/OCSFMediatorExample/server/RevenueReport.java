package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import il.cshaifasweng.OCSFMediatorExample.server.ReportDB;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;


public class RevenueReport implements ReportStrategy {

    @Override
    public String generate(LocalDate month, String restaurantName, TimeFrame timeFrame) {
        ReportDB reportDB = new ReportDB();
        return reportDB.generate_revenue(month, restaurantName, timeFrame);

    }
}