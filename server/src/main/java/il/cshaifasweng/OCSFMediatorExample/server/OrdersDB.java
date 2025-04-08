package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.getAllMeals;

public class OrdersDB {

    public static Order getOrderById(int orderId) {
        System.out.println("now I am in the function");
        Order order = null;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            order = session.get(Order.class, orderId);
            if (order != null) {
                order.setOrderStatus("Cancelled");
                session.update(order);
                session.getTransaction().commit();
            } else {
                session.getTransaction().rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }




    public static Order OrderById(int orderId) {
        Order order = null;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            order = session.get(Order.class, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public static void saveOrder(Order order) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(order);  // Use saveOrUpdate to handle both new and existing entities

            session.flush();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCustomizationsbool(Set<CustomizationWithBoolean> customs) {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            for (CustomizationWithBoolean custom : customs) {
                session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
            }

            session.flush();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /// ///////**************************generate Data********************************************///////////
    public static void generateOrders() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            if (!session.createQuery("FROM Order", Order.class).list().isEmpty()) {
                System.out.println("Orders already exist in database");
                return;
            }

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
            query.from(Restaurant.class);
            List<Restaurant> restaurants = session.createQuery(query).getResultList();

            //I imported static function here
            List<Meal> meals = getAllMeals();

            List<Order> orders = new ArrayList<>();

            // Generate orders for each month
            int currentYear = 2025; // Use target report year
            orders.addAll(createOrdersForMonth(currentYear, Month.JANUARY, 10, restaurants, meals));
            orders.addAll(createOrdersForMonth(currentYear, Month.FEBRUARY, 5, restaurants, meals));
            orders.addAll(createOrdersForMonth(currentYear, Month.MARCH, 8, restaurants, meals));

            // Save orders (cascades will handle nested entities)
            for (Order order : orders) {
                session.persist(order); // Cascades to MealInTheCart -> personal_Meal -> CustomizationWithBoolean
            }

            session.flush(); // Ensure orders get IDs
            System.out.println("Generated " + orders.size() + " orders with customizations");



            // Generate complaints for random orders
            generateComplaints(session, orders);

            session.getTransaction().commit();
            System.out.println("Generated " + orders.size() + " orders and complaints");
        }
    }

    private static List<Order> createOrdersForMonth(int year, Month month, int count,
                                                    List<Restaurant> restaurants,
                                                    List<Meal> meals) {
        List<Order> orders = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now(); // Get current time
        int total = 0;

        for (int i = 0; i < count; i++) {
            Order order = new Order();
            Restaurant restaurant = restaurants.get(random.nextInt(restaurants.size()));

            total= 50;

            // Generate order time
            LocalDateTime orderTime = getRandomDateTimeInMonth(2025, month);

            // Determine status based on time difference
            boolean isWithin24Hours = orderTime.isAfter(now.minusHours(24));
            String status = isWithin24Hours ? "Do" : "Done";

            // Set order properties
            order.setRestaurantId(restaurant.getId());
            order.setRestaurantName(restaurant.getRestaurantName());
            order.setDate(orderTime.toLocalDate());
            order.setOrderTime(orderTime);
            order.setTotal_price(total);
            order.setOrderType(random.nextBoolean() ? "Delivery" : "Pickup");
            order.setOrderStatus(status); // Set determined status
            order.setCustomerEmail("naghammnsor@gmail.com");
            order.setCreditCard_num(String.format("4111-1111-1111-%04d", i));

            // Rest of meal creation code remains the same...
            // [Keep existing meal and customization logic here]

            orders.add(order);
        }
        return orders;
    }

    private static Order createDynamicOrder(List<Restaurant> restaurants, List<Meal> meals) {
        Order order = new Order();
        Random random = new Random();
        Restaurant restaurant = restaurants.get(random.nextInt(restaurants.size()));

        // Set time to 23h55m ago
        LocalDateTime dynamicTime = LocalDateTime.now()
                .minusHours(23)
                .minusMinutes(55);

        // Set basic order info
        order.setRestaurantId(restaurant.getId());
        order.setRestaurantName(restaurant.getRestaurantName());
        order.setDate(dynamicTime.toLocalDate());
        order.setOrderTime(dynamicTime);
        order.setOrderType("Delivery");
        order.setOrderStatus("Do"); // Special status for dynamic order
        order.setCustomerEmail("naghammnsor@gmail.com");
        order.setCreditCard_num("5555-5555-5555-5555");

        // Rest of meal creation code remains the same...
        // [Keep existing meal and customization logic here]

        return order;
    }

    private static void generateComplaints(Session session, List<Order> orders) {
        Random random = new Random();
        String[] firstNames = {"John", "Alice", "Bob", "Emma", "David"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Wilson", "Taylor"};

        // Get all restaurants for lookup
        List<Restaurant> restaurants = session.createQuery("FROM Restaurant", Restaurant.class).list();
        Map<String, Restaurant> restaurantMap = new HashMap<>();
        for (Restaurant r : restaurants) {
            restaurantMap.put(r.getRestaurantName(), r);
        }

        for (Order order : orders) {
            // 30% chance to generate complaint for each order
            if (random.nextDouble() < 0.3) {
                Complain complaint = new Complain();

                // Set fixed values
                complaint.setEmail("naghammnsor@gmail.com");
                complaint.setKind("Complaint");

                // Generate random name
                String randomName = firstNames[random.nextInt(firstNames.length)] + " " +
                        lastNames[random.nextInt(lastNames.length)];
                complaint.setName(randomName);

                // Set time and date (1 day after order)
                complaint.setDate(order.getDate().plusDays(1));
                complaint.setTime(order.getOrderTime().plusHours(24));

                // Random refund between 0 and total price
                complaint.setRefund(0.5 * order.getTotal_price());

                // Find associated restaurant
                Restaurant restaurant = restaurantMap.get(order.getRestaurantName());
                if (restaurant != null) {
                    complaint.setRestaurant(restaurant);
                }

                // Set order number
                complaint.setOrderNum(String.valueOf(order.getId()));

                // Set default values
                complaint.setTell(""); // Empty as requested
                complaint.setResponse(""); // Empty as requested
                complaint.setStatus("Done"); // Default status

                session.persist(complaint);
            }
        }
    }

    // Helper methods
    private static LocalDate getRandomDateInMonth(int year, Month month) {
        LocalDate start = LocalDate.of(year, month, 1);
        int days = start.lengthOfMonth();
        return start.plusDays(new Random().nextInt(days));
    }

    private static LocalDateTime getRandomDateTimeInMonth(int year, Month month) {
        LocalDate date = getRandomDateInMonth(year, month);
        return date.atTime(
                new Random().nextInt(24),
                new Random().nextInt(60)
        );
    }
}
    /*public static void saveOrder2(Order order) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Persist CustomizationWithBoolean if necessary, using saveOrUpdate
            for (MealInTheCart mealInTheCart : order.getMeals()) {
                for (CustomizationWithBoolean customization : mealInTheCart.getMeal().getCustomizationsList()) {
                    // Save or update CustomizationWithBoolean
                    session.saveOrUpdate(customization);
                }
            }
            // Now save or update the order (this will cascade to meals and customizations)
            session.saveOrUpdate(order);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/