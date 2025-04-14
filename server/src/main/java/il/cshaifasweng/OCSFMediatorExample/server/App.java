package il.cshaifasweng.OCSFMediatorExample.server;


import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.addCreditCardDetails;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.getAllComplains;
import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.getAllCreditCards;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.OrdersDB.generateOrders;
import static il.cshaifasweng.OCSFMediatorExample.server.OrdersDB.getOrders;
import static il.cshaifasweng.OCSFMediatorExample.server.PersonalDetailsDB.getAllPersonalDetails;
import static il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB.getAllRestaurants;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.*;
import static il.cshaifasweng.OCSFMediatorExample.server.UsersDB.*;

public class App {

    private static Session session;
    private static String userinput = null;


    public static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        // Prompt for the password dynamically
        if (userinput == null) {
            Scanner userInput = new Scanner(System.in);
            System.out.print("Enter the database password: ");
            userinput = userInput.nextLine();
        }
        configuration.setProperty("hibernate.connection.password", userinput);

        // hala add this for the hibernate error
        // ✅ Set MySQL 8 dialect
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        // ✅ Explicitly disable auto-commit mode
        configuration.setProperty("hibernate.connection.autocommit", "false");


        configuration.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        // Add Meal and Customization entities
        configuration.addAnnotatedClass(Complain.class);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(Customization.class);
        configuration.addAnnotatedClass(Users.class);
        configuration.addAnnotatedClass(Order.class);
        configuration.addAnnotatedClass(Restaurant.class);
        configuration.addAnnotatedClass(TableNode.class);
        configuration.addAnnotatedClass(ReservationSave.class);
        configuration.addAnnotatedClass(PersonalDetails.class);
        configuration.addAnnotatedClass(CreditCard.class);
        configuration.addAnnotatedClass(UpdatePriceRequest.class);
        configuration.addAnnotatedClass(personal_Meal.class);
        configuration.addAnnotatedClass(MealInTheCart.class);
        configuration.addAnnotatedClass(CustomizationWithBoolean.class);


        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }


    static Path imagePath(String fileName) {
        return Paths.get("src/main/resources/images/" + fileName);
    }

    public static void printAllData() throws Exception {
        // List of meals with their customizations
        System.out.println("\n=== Meals List ===");
        for (Meal meal : getAllMeals()) {
            System.out.printf("Meal: %s | Price: %.2f%n", meal.getName(), meal.getPrice());
            System.out.println("Customizations:");
            for (Customization customization : meal.getCustomizations()) {
                System.out.printf("  - %s ", customization.getName());
            }
        }
    }

//    private static void generateOrders() throws Exception {
//        // Helper function to read image as byte[]
//        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
//            SessionFactory sessionFactory = getSessionFactory();
//            session = sessionFactory.openSession();
//        }
//
//        session.beginTransaction(); // Start a transaction
//        try {// hala added try
//            // Create orders
//            Order one = new Order();
//            Order two = new Order();
//            Order three = new Order();
//            Order four = new Order();
//            Order five = new Order();
//            Order six = new Order();
//            Order seven = new Order();
//
//            // Create Order details
//            one.setDate(LocalDate.now());
//            one.setTotal_price(150);
//            one.setRestaurantName("Nazareth");
//
//            // Create Order details
//            two.setDate(LocalDate.parse("2025-02-07"));
//            two.setTotal_price(150);
//            two.setRestaurantName("Nazareth");
//            three.setDate(LocalDate.parse("2025-02-10"));
//            three.setTotal_price(150);
//            three.setRestaurantName("Nazareth");
//            four.setDate(LocalDate.parse("2025-03-16"));
//            four.setTotal_price(150);
//            four.setRestaurantName("Nazareth");
//            four.setCustomerEmail("lamisawawdi2003@gmail.com");
//            // Create specific order time
//            LocalDateTime customTime = LocalDateTime.now().plusHours(4);
//            four.setOrderTime(customTime);
//
//
//            // List of Orders to add
//            List<Order> newOrders = Arrays.asList(one, two, three, four);
//
//            // Fetch existing meals from the database
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Order> query = builder.createQuery(Order.class);
//            query.from(Order.class);
//            List<Order> existingMeals = session.createQuery(query).getResultList();
//
//            // Save customizations
//            session.save(one);
//            session.save(two);
//            session.save(three);
//            session.save(four);
//            session.save(five);
//
//            session.flush();
//            session.getTransaction().commit();
//
//        } catch (Exception e) {
//            // Rollback the transaction if any error occurs
//            if (session.getTransaction() != null) {
//                session.getTransaction().rollback();
//            }
//            e.printStackTrace();
//            throw new Exception("An error occurred while generating orders.", e);
//        } finally {
//            // Ensure the session is closed after the operation to avoid memory leaks
//            if (session != null && session.isOpen()) {
//                 session.close();
//            }
//        }
//
//
//    }


    private static void generateBasicUser() throws Exception {
        // Helper function to read image as byte[]
        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        session.beginTransaction(); // Start a transaction
        try {
            // Create orders
            Users nagham = new Users();
            nagham.setRole("CompanyManager");
            nagham.setEmail("naghammnsor@gmail.com");
            nagham.setPassword("NaghamYes");
            nagham.setUsername("naghamTheManager");
            nagham.setGender("other");
            nagham.setAge(22);
            Users shada = new Users();
            shada.setRole("Dietation");
            shada.setEmail("shadamazzawi@gmail.com");
            shada.setPassword("123");
            shada.setUsername("shada");
            shada.setGender("other");
            shada.setAge(22);


            // List of Orders to add
            List<Users> newOrders = Arrays.asList(nagham, shada);

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            List<Users> existingMeals = session.createQuery(query).getResultList();

            // Save customizations
            session.save(nagham);
            session.save(shada);
            session.flush();
            session.getTransaction().commit(); // Commit the transaction

        } catch (Exception e) {
            // Rollback transaction in case of an error
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new Exception("An error occurred while generating the user.", e);
        }
        // Do not close the session here; keep it open for further operations

    }

    private static void generateRestaurants() throws Exception {
        if (getAllRestaurants() != null && !getAllRestaurants().isEmpty()) {
            System.out.println("there are restaurants in the database");
            return;
        } else
            System.out.println("no restaurants in the database");
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();


            LocalTime openingTime = LocalTime.of(0, 0); // 10:00 AM
            LocalTime closingTime = LocalTime.of(23, 59); // 10:00 PM

            Restaurant nazareth = new Restaurant();
            nazareth.setRestaurantName("Nazareth");
            nazareth.setImagePath("nazareth.jpg");
            nazareth.setPhoneNumber("123-456-7890");
            nazareth.setOpeningTime(openingTime);
            nazareth.setClosingTime(closingTime);

            Restaurant haifa = new Restaurant();
            haifa.setRestaurantName("Haifa");
            haifa.setImagePath("haifa.jpg");
            haifa.setPhoneNumber("234-567-8901");
            haifa.setOpeningTime(openingTime);
            haifa.setClosingTime(closingTime);

            Restaurant telAviv = new Restaurant();
            telAviv.setRestaurantName("Tel Aviv");
            telAviv.setImagePath("telaviv.jpg");
            telAviv.setPhoneNumber("345-678-9012");
            telAviv.setOpeningTime(openingTime);
            telAviv.setClosingTime(closingTime);

            Restaurant Sakhnin = new Restaurant();
            Sakhnin.setRestaurantName("SAKHNIN");
            Sakhnin.setImagePath("sakhnin.jpg");
            Sakhnin.setPhoneNumber("345-678-9012");
            Sakhnin.setOpeningTime(openingTime);
            Sakhnin.setClosingTime(closingTime);

            List<Meal> meals = getAllMeals();
            int i = 0;
            List<Meal> nazarethMeals = new ArrayList<>();
            List<Meal> haifaMeals = new ArrayList<>();
            List<Meal> telavivMeals = new ArrayList<>();
            List<Meal> sakhninMeals = new ArrayList<>();
            for (Meal meal : meals) {
                if (i % 2 == 0) {
                    nazarethMeals.add(meal);
                    sakhninMeals.add(meal);
                }
                if (i % 3 == 0) {
                    haifaMeals.add(meal);
                }
                telavivMeals.add(meal);
                i++;
            }
            telAviv.setMeals(telavivMeals);
            nazareth.setMeals(nazarethMeals);
            haifa.setMeals(haifaMeals);
            Sakhnin.setMeals(sakhninMeals);


            session.save(nazareth);
            session.save(haifa);
            session.save(telAviv);
            session.save(Sakhnin);

            session.flush();
            session.getTransaction().commit();
            System.out.println("Successfully generated new restaurants.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("An error occurred while generating restaurants.", e);
        }
    }

    private static void generateTheComplains() throws Exception {
        if (allComplains != null && !allComplains.isEmpty()) {
            System.out.println("There are already some complains in the database.");
            return;
        }

        System.out.println("Generating sample complains...");

        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            Random random = new Random();
            String[] types = {"Complaint", "Feedback", "Suggestion"};
            String[] statuses = {"Do", "Done"};
            String[] sampleTells = {
                    "The order was cold.",
                    "The driver was rude.",
                    "Great experience!",
                    "Food arrived late.",
                    "Loved the meal.",
                    "Too expensive.",
                    "The order was missing items.",
                    "Best restaurant ever!",
                    "Didn't like the packaging.",
                    "Please add more vegan options.",
                    "Food was soggy.",
                    "Staff was polite.",
                    "Took too long to prepare.",
                    "No napkins included.",
                    "Food quality was amazing.",
                    "Wish you delivered to my area.",
                    "Wrong item received.",
                    "Loved the app experience.",
                    "Order was canceled without reason.",
                    "Thank you for the refund.",
            };

            for (int i = 1; i <= 60; i++) {
                Complain complain = new Complain();
                String kind = types[random.nextInt(types.length)];
                String status = statuses[random.nextInt(statuses.length)];
                String name = "User" + i;
                String email = "user" + i + "@example.com";
                String tell = sampleTells[random.nextInt(sampleTells.length)];
                int year = 2024 + random.nextInt(2); // 2024 or 2025
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28); // Keep it safe for all months
                int hour = random.nextInt(24);
                int minute = random.nextInt(60);

                LocalDate date = LocalDate.of(year, month, day);
                LocalDateTime time = LocalDateTime.of(year, month, day, hour, minute);

                complain.setKind(kind);
                complain.setName(name);
                complain.setEmail(email);
                complain.setTell(tell);
                complain.setDate(date);
                complain.setTime(time);
                complain.setStatus(status);
                complain.setResponse(status.equals("Done") ? "We have taken care of it." : "");
                complain.setOrderNum(kind.equals("Feedback") || kind.equals("Suggestion") ? "" : String.valueOf(random.nextInt(200) + 1));
                complain.setRefund(kind.equals("Complaint") && status.equals("Done") ? random.nextInt(21) : 0);

                session.save(complain);
                allComplains.add(complain);
            }

            session.flush();
            session.getTransaction().commit();
            System.out.println("Successfully generated 60 sample complains.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("An error occurred while generating complains.", e);
        }
    }



    private static SimpleServer server;

    public static void main(String[] args) throws IOException {
        try {
            server = new SimpleServer(3000);
            server.listen();
            deleteAllTablesAndRelatedData();
            generateData();
            printAllData();
            allPersonalDetails = getAllPersonalDetails();
            allCreditCards = getAllCreditCards();
            allUsers = getUsers();
            generateBasicUser1();
            generateRestaurants();
            //generateCompanyMeals();
            getAllRestMeals();
            System.out.println("getting all custom");
            allcust = getAllCustomizations();

            allComplains = getAllComplains();
            generateTheComplains();
            initializeSampleTables();
            fetching_reservation();
            generateOrders();
            allOrders = getOrders();
            //generateBasicUser1();
            // Register a shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown initiated. Performing cleanup...");
                // Put your cleanup code here (close resources, save state, etc.)
                try {
                    Thread.sleep(1000); // Simulating cleanup (e.g., closing resources)
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Cleanup completed.");
            }));
        } catch (Exception exception) {
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        }
    }

    public static void deleteAllTablesAndRelatedData() {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction(); // Start the transaction

            // Step 1: Delete dependent rows in `reservation_save_tables`
            Query<?> deleteReservationSaveTablesQuery = session.createNativeQuery(
                    "DELETE FROM reservation_save_tables WHERE table_id IN (SELECT tableID FROM tables)"
            );
            int deletedReservationSaveTablesCount = deleteReservationSaveTablesQuery.executeUpdate();
            System.out.println("Deleted " + deletedReservationSaveTablesCount + " rows from reservation_save_tables.");

            // Step 2: Delete dependent rows in `tablenode_reservationendtimes`
            Query<?> deleteEndTimesQuery = session.createNativeQuery(
                    "DELETE FROM tablenode_reservationendtimes WHERE TableNode_tableID IN (SELECT tableID FROM tables)"
            );
            int deletedEndTimesCount = deleteEndTimesQuery.executeUpdate();
            System.out.println("Deleted " + deletedEndTimesCount + " rows from tablenode_reservationendtimes.");

            // Step 3: Delete dependent rows in `tablenode_reservationstarttimes`
            Query<?> deleteStartTimesQuery = session.createNativeQuery(
                    "DELETE FROM tablenode_reservationstarttimes WHERE TableNode_tableID IN (SELECT tableID FROM tables)"
            );
            int deletedStartTimesCount = deleteStartTimesQuery.executeUpdate();
            System.out.println("Deleted " + deletedStartTimesCount + " rows from tablenode_reservationstarttimes.");

            // Step 4: Delete all existing tables
            Query<?> deleteTablesQuery = session.createQuery("DELETE FROM TableNode");
            int deletedTablesCount = deleteTablesQuery.executeUpdate();
            System.out.println("Deleted " + deletedTablesCount + " existing tables.");

            session.getTransaction().commit(); // Commit the transaction
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., rollback transaction if needed)
        }
    }

    private static void initializeSampleTables() {
        List<Integer> capacities = Arrays.asList(2, 3, 4);
        Random random = new Random();

        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction(); // Start the transaction

            // Check if tables already exist in the database
            List<TableNode> tableCountQuery = session.createQuery("FROM TableNode", TableNode.class).list();
            int tableCount = tableCountQuery.size();

            // Initialize lazy-loaded fields
            for (TableNode table : tableCountQuery) {
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());
                Hibernate.initialize(table.getRestaurant()); // if needed
            }

            SimpleServer.allTables = tableCountQuery;

            if (tableCount > 0) {
                System.out.println("Tables already exist in the database. Skipping sample table generation.");
                session.getTransaction().commit();
                return; // Exit the method if tables exist
            }

            // Step 1: Fetch all restaurants
            Query<Restaurant> query = session.createQuery("FROM Restaurant", Restaurant.class);
            List<Restaurant> restaurants = query.getResultList();
            System.out.println("The restaurants that will have tables are: " + restaurants);

            // Step 2: Create sample tables for each restaurant
            for (Restaurant restaurant : restaurants) {
                List<TableNode> tables = new ArrayList<>();
                for (int i = 0; i < 15; i++) { // Add 15 tables for each restaurant
                    int capacity = capacities.get(random.nextInt(capacities.size()));
                    TableNode table = new TableNode(restaurant, random.nextBoolean(), capacity, "available");

                    // Save table to the session
                    session.save(table);
                    allTables.add(table);
                    tables.add(table);
                }

                // Save the tables in the restaurant
                restaurant.setTables(tables);
            }

            session.getTransaction().commit(); // Commit the transaction after saving tables
            System.out.println("Sample tables generated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<Restaurant> Get_Restaurant(String queryString, String restaurantName) {
        List<Restaurant> result = new ArrayList<>();

        try (Session session = getSessionFactory().openSession()) { // Auto-closing session
            session.beginTransaction();

            org.hibernate.query.Query<Restaurant> query = session.createQuery(queryString, Restaurant.class);
            query.setParameter("restaurantName", restaurantName); // Bind the named parameter

            result = query.getResultList();

            // Initialize the tables collection for each restaurant
            for (Restaurant restaurant : result) {
                Hibernate.initialize(restaurant.getTables()); // Force initialization of the tables collection
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public static void fetching_reservation() {
        List<Restaurant> result = new ArrayList<>();

        try (Session session = getSessionFactory().openSession()) { // Auto-closing session
            session.beginTransaction();

            // Fetch all ReservationSave objects from the database
            allSavedReservation = session.createQuery("FROM ReservationSave", ReservationSave.class).list();

            // Initialize the tables collection for each ReservationSave object
            for (ReservationSave reservation : allSavedReservation) {
                Hibernate.initialize(reservation.getTables()); // Initialize the lazy-loaded tables collection
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    public static void relateMealsWithRestaurants() throws Exception {
//        // Example data generation
//        List<Meal> meals = getAllMeals();  // Assuming this method generates the list of meals
//        List<Restaurant> restaurants = getAllRestaurants();  // Assuming this method generates the list of restaurants
//
//        Random random = new Random();
//
//        // Relate each restaurant to a random subset of meals
//        for (Restaurant restaurant : restaurants) {
//            // Select a random number of meals for this restaurant (let's say between 1 and all meals)
//            int numMealsForRestaurant = random.nextInt(meals.size()) + 1;  // Random number between 1 and meals.size()
//
//            // Randomly select meals for this restaurant
//            for (int i = 0; i < numMealsForRestaurant; i++) {
//                // Randomly pick a meal from the list of meals
//                Meal randomMeal = meals.get(random.nextInt(meals.size()));  // Get a random meal from the list
//
//                // Initialize the restaurants collection if it's lazily loaded
//                Hibernate.initialize(randomMeal.getRestaurants());
//
//                // Check if this meal is already related to any restaurant
//                if (randomMeal.getRestaurants() != null && !randomMeal.getRestaurants().isEmpty()) {
//                    // Skip meal if it is already related to a restaurant
//                    continue;
//                }
//
//                // Add the meal to the restaurant's meals list, if not already added
//                if (!restaurant.getMeals().contains(randomMeal)) {
//                    restaurant.getMeals().add(randomMeal);
//                }
//
//                // Add the restaurant to the meal's restaurants list, if not already added
//                if (!randomMeal.getRestaurants().contains(restaurant)) {
//                    randomMeal.getRestaurants().add(restaurant);
//                }
//            }
//        }
//    }
}