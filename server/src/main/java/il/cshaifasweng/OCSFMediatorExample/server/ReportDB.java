package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Complain;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservationSave;
import il.cshaifasweng.OCSFMediatorExample.entities.TimeFrame;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
            Root<Order> root = query.from(Order.class);

            // Period selection
            Expression<Integer> periodExpression = timeFrame == TimeFrame.YEARLY
                    ? builder.function("MONTH", Integer.class, root.get("date"))
                    : builder.function("DAY", Integer.class, root.get("date"));

            // Sum as Long for integer total_price
            Expression<Long> revenueSum = builder.sum(root.get("total_price"));

            // Date predicates
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

            // Handle "ALL" restaurants case
            if (restaurantName != null && !"ALL".equalsIgnoreCase(restaurantName)) {
                Predicate restaurantPredicate = builder.equal(root.get("restaurantName"), restaurantName);
                finalPredicate = builder.and(finalPredicate, restaurantPredicate);
            }

            // Build query
            query.multiselect(periodExpression, revenueSum)
                    .where(finalPredicate)
                    .groupBy(periodExpression)
                    .orderBy(builder.asc(periodExpression));

            List<Object[]> results = session.createQuery(query).getResultList();

            // Build report header
            reportBuilder.append("Revenue Report - ")
                    .append(timeFrame == TimeFrame.MONTHLY ? "Monthly\n" : "Yearly\n")
                    .append("Period: ").append(month.getMonth()).append(" ").append(month.getYear())
                    .append("\nRestaurant: ")
                    .append("ALL".equalsIgnoreCase(restaurantName) ? "All Restaurants" : restaurantName)
                    .append("\n\n");

            // Initialize complete period data
            Map<Integer, Long> periodData = new LinkedHashMap<>();
            if(timeFrame == TimeFrame.MONTHLY) {
                int daysInMonth = month.lengthOfMonth();
                for(int day = 1; day <= daysInMonth; day++) {
                    periodData.put(day, 0L);
                }
            } else {
                for(int m = 1; m <= 12; m++) {
                    periodData.put(m, 0L);
                }
            }

            // Fill with actual data
            results.forEach(row -> {
                Integer period = (Integer) row[0];
                Long revenue = ((Number) row[1]).longValue();
                periodData.put(period, revenue);
            });

            // Build report lines
            periodData.forEach((period, revenue) -> {
                reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Day " : "Month ")
                        .append(period)
                        .append(": $")
                        .append(revenue)
                        .append("\n");
            });

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
            session = getSessionFactory().openSession();
            session.beginTransaction();

            // CriteriaBuilder and Root setup
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
            Root<Order> root = query.from(Order.class);

            // 1. Period selection
            Expression<Integer> periodExpression = timeFrame == TimeFrame.YEARLY
                    ? builder.function("MONTH", Integer.class, root.get("date"))
                    : builder.function("DAY", Integer.class, root.get("date"));

            // 2. Base predicates
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
                finalPredicate = builder.and(finalPredicate, monthPredicate);
            }

            // 3. Restaurant filter
            if (!"ALL".equalsIgnoreCase(restaurantName)) {
                Predicate restaurantPredicate = builder.equal(
                        builder.lower(root.get("restaurantName")),
                        restaurantName.toLowerCase()
                );
                finalPredicate = builder.and(finalPredicate, restaurantPredicate);
            }

            // 4. Order type filter (NEW)
            if (orderTypeFilter != null && !"ALL".equalsIgnoreCase(orderTypeFilter)) {
                Predicate typePredicate = builder.equal(
                        builder.lower(root.get("orderType")),
                        orderTypeFilter.toLowerCase()
                );
                finalPredicate = builder.and(finalPredicate, typePredicate);
            }

            // 5. Build query
            query.multiselect(periodExpression, builder.count(root))
                    .where(finalPredicate)
                    .groupBy(periodExpression)
                    .orderBy(builder.asc(periodExpression));

            List<Object[]> results = session.createQuery(query).getResultList();

            // 6. Build report header
            String address= orderTypeFilter +" Count Report - ";
            reportBuilder.append(address)
                    .append(timeFrame == TimeFrame.YEARLY ? "Yearly" : "Monthly")
                    .append("\nPeriod: ")
                    .append(month.getMonth().toString().toUpperCase())
                    .append(" ")
                    .append(month.getYear())
                    .append("\nRestaurant: ")
                    .append("ALL".equalsIgnoreCase(restaurantName) ? "All Restaurants" : restaurantName)
                    .append("\nOrder Type: ")
                    .append("ALL".equalsIgnoreCase(orderTypeFilter) ? "All Types" : orderTypeFilter)
                    .append("\n\n");

            // 7. Initialize complete period data
            Map<Integer, Long> periodData = new LinkedHashMap<>();
            if(timeFrame == TimeFrame.YEARLY) {
                for(int m = 1; m <= 12; m++) periodData.put(m, 0L);
            } else {
                int daysInMonth = month.lengthOfMonth();
                for(int d = 1; d <= daysInMonth; d++) periodData.put(d, 0L);
            }

            // 8. Fill with actual data
            results.forEach(row -> periodData.put((Integer) row[0], (Long) row[1]));


            System.out.println("Results size: " + results.size());
            // 9. Format output lines
            periodData.forEach((period, count) -> {
                String periodLabel = timeFrame == TimeFrame.YEARLY ? "Month " : "Day ";
                reportBuilder.append(periodLabel)
                        .append(period)
                        .append(": $")
                        .append(count)
                        .append("\n");
            });

            session.getTransaction().commit();

        } catch (Exception e) {
            if (session != null) session.getTransaction().rollback();
            return "Error: " + e.getMessage();
        } finally {
            if (session != null) session.close();
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
            reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Monthly\n\n" : "Yearly\n\n"); // Use "Yearly" instead of "Monthly"
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

    public String generate_visitors_report(LocalDate month, String restaurantName, TimeFrame timeFrame, String note) {
        System.out.println("Generating visitors report for " + ("ONE".equals(note) ? "restaurant: " + restaurantName : "all restaurants"));
        StringBuilder reportBuilder = new StringBuilder();
        Session session = null;

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Object[]> query = builder.createQuery(Object[].class);
            Root<ReservationSave> root = query.from(ReservationSave.class);

            // Determine period grouping (month for yearly, day for monthly)
            Expression<Integer> periodExpression = timeFrame == TimeFrame.YEARLY
                    ? builder.function("MONTH", Integer.class, root.get("reservationDateTime"))
                    : builder.function("DAY", Integer.class, root.get("reservationDateTime"));

            // Date predicates based on timeframe
            Predicate yearPredicate = builder.equal(
                    builder.function("YEAR", Integer.class, root.get("reservationDateTime")),
                    month.getYear()
            );

            Predicate finalPredicate = yearPredicate;
            if (timeFrame == TimeFrame.MONTHLY) {
                Predicate monthPredicate = builder.equal(
                        builder.function("MONTH", Integer.class, root.get("reservationDateTime")),
                        month.getMonthValue()
                );
                finalPredicate = builder.and(yearPredicate, monthPredicate);
            }


            // Add restaurant filter only if note is "ONE"
            if ("ONE".equals(note)) {
                // Correct "name" to "restaurantName"
                Predicate restaurantPredicate = builder.equal(root.get("restaurantName"), restaurantName);
                finalPredicate = builder.and(finalPredicate, restaurantPredicate);
            }

            // Build query
            query.multiselect(periodExpression, builder.count(root))
                    .where(finalPredicate)
                    .groupBy(periodExpression)
                    .orderBy(builder.asc(periodExpression));

            List<Object[]> results = session.createQuery(query).getResultList();

            // Format report header to match client expectations
            reportBuilder.append("Visitors Report - ");
            reportBuilder.append(timeFrame == TimeFrame.MONTHLY ? "Monthly\n\n" : "Yearly\n\n"); // Use "Yearly" instead of "Monthly"
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