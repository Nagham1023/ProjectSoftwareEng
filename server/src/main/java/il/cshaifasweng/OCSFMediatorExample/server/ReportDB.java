package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Complain;
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


    public String generate_revenue(LocalDate month, String restaurantName, TimeFrame timeFrame, String note) {
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

    public String generate_order_type_report(LocalDate month, String restaurantName, TimeFrame timeFrame, String orderTypeFilter) {
        System.out.println("Generating order type report for restaurant: " + restaurantName + " (Filter: " + orderTypeFilter + ")");
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
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
            Root<Order> root = query.from(Order.class);

            // Period selection (MONTH for YEARLY, DAY for MONTHLY)
            Expression<Integer> periodExpression;
            if (timeFrame == TimeFrame.YEARLY) {
                periodExpression = builder.function("MONTH", Integer.class, root.get("date"));
            } else { // MONTHLY
                periodExpression = builder.function("DAY", Integer.class, root.get("date"));
            }

            Expression<String> orderTypeExpression = root.get("orderType");
            Expression<Long> orderCount = builder.count(root);

            Predicate yearPredicate = builder.equal(
                    builder.function("YEAR", Integer.class, root.get("date")),
                    month.getYear()
            );

            Predicate finalPredicate = yearPredicate;
            if (timeFrame == TimeFrame.MONTHLY) {
                Predicate monthPredicate = builder.equal(
                        builder.function("MONTH", Integer.class, root.get("date")),
                        month.getMonthValue()
                );
                finalPredicate = builder.and(yearPredicate, monthPredicate);
            }

            Predicate restaurantPredicate = builder.equal(root.get("restaurantName"), restaurantName);
            finalPredicate = builder.and(finalPredicate, restaurantPredicate);

            // **NEW: Order Type Filtering**
            if (orderTypeFilter != null && !orderTypeFilter.equalsIgnoreCase("ALL")) {
                Predicate orderTypePredicate = builder.equal(root.get("orderType"), orderTypeFilter);
                finalPredicate = builder.and(finalPredicate, orderTypePredicate);
            }

            query.multiselect(periodExpression, orderTypeExpression, orderCount);
            query.where(finalPredicate);
            query.groupBy(periodExpression, orderTypeExpression);
            query.orderBy(builder.asc(periodExpression));

            List<Object[]> results = session.createQuery(query).getResultList();

            reportBuilder.append("Order Type Report - ");
            reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Daily\n\n" : "Yearly\n\n");

            if (results.isEmpty()) {
                reportBuilder.append("No data found for the specified criteria.\n");
            } else {
                for (Object[] row : results) {
                    reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Day " : "Month ")
                            .append(row[0]).append(", Order Type: ").append(row[1])
                            .append(", Orders Count: ").append(row[2]).append("\n");
                }
            }

            session.getTransaction().commit();

        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            return "Error generating report: " + e.getMessage();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return reportBuilder.toString();
    }

    public String generate_complain_report(LocalDate month, String restaurantName, TimeFrame timeFrame, String note) {
        System.out.println("Generating complaint report for " + ("ONE".equals(note) ? "restaurant: " + restaurantName : "all restaurants"));
        StringBuilder reportBuilder = new StringBuilder();
        Session session = null;

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
            Root<Complain> root = query.from(Complain.class);

            // Determine period grouping (month for yearly, day for monthly)
            Expression<Integer> periodExpression = timeFrame == TimeFrame.YEARLY
                    ? builder.function("MONTH", Integer.class, root.get("date_complain"))
                    : builder.function("DAY", Integer.class, root.get("date_complain"));

            // Date predicates based on timeframe
            Predicate yearPredicate = builder.equal(
                    builder.function("YEAR", Integer.class, root.get("date_complain")),
                    month.getYear()
            );

            Predicate finalPredicate = yearPredicate;
            if (timeFrame == TimeFrame.MONTHLY) {
                Predicate monthPredicate = builder.equal(
                        builder.function("MONTH", Integer.class, root.get("date_complain")),
                        month.getMonthValue()
                );
                finalPredicate = builder.and(yearPredicate, monthPredicate);
            }

            // Always filter by "Complaint" type
            Predicate kindPredicate = builder.equal(root.get("kind_complain"), "Complaint");
            finalPredicate = builder.and(finalPredicate, kindPredicate);

            // Add restaurant filter only if note is "ONE"
            if ("ONE".equals(note)) {
                // Correct "name" to "restaurantName"
                Predicate restaurantPredicate = builder.equal(root.get("restaurant").get("restaurantName"), restaurantName);
                finalPredicate = builder.and(finalPredicate, restaurantPredicate);
            }

            // Build query
            query.multiselect(periodExpression, builder.count(root))
                    .where(finalPredicate)
                    .groupBy(periodExpression)
                    .orderBy(builder.asc(periodExpression));

            List<Object[]> results = session.createQuery(query).getResultList();

            // Format report header to match client expectations
            reportBuilder.append("Complaint Report - ");
            reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Daily\n\n" : "Yearly\n\n"); // Use "Yearly" instead of "Monthly"
            reportBuilder.append("Scope: ").append("ONE".equals(note) ? restaurantName : "All Restaurants").append("\n");

            if (results.isEmpty()) {
                reportBuilder.append("No complaints found.\n");
            } else {
                for (Object[] row : results) {
                    // Format lines like "Day 5: $3" to match revenue report parsing
                    reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Day " : "Month ")
                            .append(row[0]).append(": $").append(row[1]).append("\n");
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            return "Error: " + e.getMessage();
        } finally {
            if (session != null) session.close();
        }

        return reportBuilder.toString();
    }

}