package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import org.hibernate.Session;

import java.time.LocalDate;

public interface ReportStrategy {
    String generate(LocalDate month, String restaurantName, TimeFrame timeFrame);
}

