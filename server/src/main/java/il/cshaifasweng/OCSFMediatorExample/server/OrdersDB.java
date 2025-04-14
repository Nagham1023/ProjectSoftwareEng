package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.getAllMeals;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.allOrders;

public class OrdersDB {


    public static List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();

        // try-with-resources ensures the session is closed automatically
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> query = builder.createQuery(Order.class);
            query.from(Order.class);

            orders = session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // You could also log this if you have a logger
        }

        return orders;
    }

    public static Order getOrderById(int orderId) {
        System.out.println("now I am in the function");
        System.out.println("now I am in the function");
        for(Order order : allOrders) {
            if(order.getId() == orderId) {
                order.setOrderStatus("Cancelled");
            }
        }
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
        for(Order order : allOrders) {
            if(order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public static void saveOrder(Order order) {
        allOrders.add(order);
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(order);  // Use saveOrUpdate to handle both new and existing entities

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

            // Check if orders already exist
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> orderQuery = builder.createQuery(Order.class);
            Root<Order> orderRoot = orderQuery.from(Order.class);  // This was missing
            if (!session.createQuery(orderQuery).getResultList().isEmpty()) {
                System.out.println("Orders already exist in database");
                return;
            }

            // Get restaurants with proper CriteriaQuery
            CriteriaQuery<Restaurant> restaurantQuery = builder.createQuery(Restaurant.class);
            Root<Restaurant> restaurantRoot = restaurantQuery.from(Restaurant.class);
            List<Restaurant> restaurants = session.createQuery(restaurantQuery).getResultList();

            // Get meals with proper CriteriaQuery
            CriteriaQuery<Meal> mealQuery = builder.createQuery(Meal.class);
            Root<Meal> mealRoot = mealQuery.from(Meal.class);
            List<Meal> meals = session.createQuery(mealQuery).getResultList();

            if (restaurants.isEmpty() || meals.isEmpty()) {
                System.err.println("No restaurants or meals found in database.");
                return;
            }

            List<Order> orders = new ArrayList<>();
            int currentYear = 2025;

            // Generate orders for months
            orders.addAll(createOrdersForMonth(currentYear, Month.JANUARY, 20, restaurants, meals));
            orders.addAll(createOrdersForMonth(currentYear, Month.FEBRUARY, 22, restaurants, meals));
            orders.addAll(createOrdersForMonth(currentYear, Month.MARCH, 18, restaurants, meals));
            orders.addAll(createOrdersForMonth(currentYear, Month.APRIL, 50, restaurants, meals));

            // Persist orders
            for (Order order : orders) {
                session.persist(order);
            }

            session.flush();
            generateComplaints(session, orders);
            session.getTransaction().commit();

            System.out.println("Successfully generated " + orders.size() + " orders");
        }
    }

    private static List<Order> createOrdersForMonth(int year, Month month, int count,
                                                    List<Restaurant> restaurants,
                                                    List<Meal> meals) {
        List<Order> orders = new ArrayList<>();
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            Order order = new Order();
            Restaurant restaurant = restaurants.get(random.nextInt(restaurants.size()));

            // Set basic order info
            LocalDateTime orderTime = getRandomDateTimeInMonth(year, month);
            boolean isWithin24Hours = orderTime.isAfter(now.minusHours(24));

            order.setRestaurantId(restaurant.getId());
            order.setRestaurantName(restaurant.getRestaurantName());
            order.setDate(orderTime.toLocalDate());
            order.setOrderTime(orderTime);
            order.setOrderStatus(isWithin24Hours ? "Do" : "Done");
            order.setOrderType(random.nextBoolean() ? "Delivery" : "Self PickUp");
            order.setCustomerEmail("customer" + i + "@example.com");
            order.setCreditCard_num(String.format("4111-1111-1111-%04d", i));

            // Add meals to order
            List<MealInTheCart> cartMeals = new ArrayList<>();
            int mealCount = 1 + random.nextInt(3); // 1-3 meals per order
            double total = 0;

            for (int j = 0; j < mealCount; j++) {
                Meal meal = meals.get(random.nextInt(meals.size()));

                MealInTheCart mic = new MealInTheCart();
                mic.setQuantity(1 + random.nextInt(3)); // 1-3 quantity
                mic.setOrder(order);

                personal_Meal pm = new personal_Meal();
                pm.setMeal(meal);
                pm.setCustomizationsList(new HashSet<>());
                mic.setMeal(pm);

                total += meal.getPrice() * mic.getQuantity();
                cartMeals.add(mic);
            }

            order.setTotal_price((int) total);
            order.setMeals(cartMeals);
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