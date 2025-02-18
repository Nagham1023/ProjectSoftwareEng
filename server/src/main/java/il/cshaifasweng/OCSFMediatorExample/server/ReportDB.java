package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class ReportDB {

    public ReportDB() {
    }


    public String generate_revenue(LocalDate month, String restaurantName, TimeFrame timeFrame) {
        System.out.println("Generating revenue report for restaurant: " + restaurantName);
        StringBuilder reportBuilder = new StringBuilder();
        Session session = null;

        try {
            // Open a new session
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            // Begin transaction
            session.beginTransaction();

            // CriteriaBuilder and Root setup
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class); // Use Object[] for array results
            Root<Order> root = query.from(Order.class);

            // Period selection (MONTH for YEARLY, DAY for MONTHLY)
            Expression<Integer> periodExpression;
            if (timeFrame == TimeFrame.YEARLY) {
                periodExpression = builder.function("MONTH", Integer.class, root.get("date")); // Group by month for yearly report
            } else { // MONTHLY
                periodExpression = builder.function("DAY", Integer.class, root.get("date")); // Group by day for monthly report
            }

            // Sum of revenue
            Expression<Double> revenueSum = builder.sum(root.get("total_price"));

            // Year predicate
            Predicate yearPredicate = builder.equal(
                    builder.function("YEAR", Integer.class, root.get("date")),
                    month.getYear()
            );

            // Month predicate (only for MONTHLY time frame)
            Predicate finalPredicate = yearPredicate;
            if (timeFrame == TimeFrame.MONTHLY) {
                Predicate monthPredicate = builder.equal(
                        builder.function("MONTH", Integer.class, root.get("date")),
                        month.getMonthValue()
                );
                finalPredicate = builder.and(yearPredicate, monthPredicate);
            }

            // Restaurant name predicate
            Predicate restaurantPredicate = builder.equal(root.get("restaurantName"), restaurantName);
            finalPredicate = builder.and(finalPredicate, restaurantPredicate);

            // Apply where clause
            query.select(builder.array(periodExpression, revenueSum)); // Select array of period and revenue
            query.where(finalPredicate);

            // Group and order
            query.groupBy(periodExpression);
            query.orderBy(builder.asc(periodExpression));

            // Execute query
            List<Object[]> results = session.createQuery(query).getResultList();

            // Build the report
            reportBuilder.append("Revenue Report - ");
            reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Daily\n\n" : "Yearly\n\n");

            if (results.isEmpty()) {
                reportBuilder.append("No data found for the specified criteria.\n");
            } else {
                for (Object[] row : results) {
                    reportBuilder
                            .append(timeFrame == TimeFrame.MONTHLY ? "Day " : "Month ")
                            .append(row[0]).append(": $").append(row[1]).append("\n");
                }

            }


            // Commit transaction
            session.getTransaction().commit();

        } catch (Exception e) {
            // Rollback transaction on error
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            System.err.println("Error generating revenue report: " + e.getMessage());
            e.printStackTrace();
            return "Error generating report: " + e.getMessage();
        } finally {
            // Close the session
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return reportBuilder.toString();
    }
}