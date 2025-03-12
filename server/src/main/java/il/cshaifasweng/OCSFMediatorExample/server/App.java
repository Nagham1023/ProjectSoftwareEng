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
import java.time.LocalTime;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB.getAllRestaurants;
import static il.cshaifasweng.OCSFMediatorExample.server.UsersDB.printAllUsers;

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

    private static void generateOrders() throws Exception {
        // Helper function to read image as byte[]
        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        session.beginTransaction(); // Start a transaction
        try {// hala added try
            // Create orders
            Order one = new Order();
            Order two = new Order();
            Order three = new Order();
            Order four = new Order();
            Order five = new Order();
            Order six = new Order();
            Order seven = new Order();

            // Create Order details
            one.setDate(LocalDate.now());
            one.setTotal_price(150);
            one.setRestaurantName("Nazareth");

            // Create Order details
            two.setDate(LocalDate.parse("2025-02-07"));
            two.setTotal_price(150);
            two.setRestaurantName("Nazareth");
            three.setDate(LocalDate.parse("2025-02-10"));
            three.setTotal_price(150);
            three.setRestaurantName("Nazareth");
            four.setDate(LocalDate.parse("2025-01-17"));
            four.setTotal_price(150);
            four.setRestaurantName("Nazareth");

            // List of Orders to add
            List<Order> newOrders = Arrays.asList(one, two, three, four);

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> query = builder.createQuery(Order.class);
            query.from(Order.class);
            List<Order> existingMeals = session.createQuery(query).getResultList();

            // Save customizations
            session.save(one);
            session.save(two);
            session.save(three);
            session.save(four);
            session.save(five);

            session.flush();
            // Commit the transaction after all operations are done
            session.getTransaction().commit();

        } catch (Exception e) {
            // Rollback the transaction if any error occurs
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new Exception("An error occurred while generating orders.", e);
        } finally {
            // Ensure the session is closed after the operation to avoid memory leaks
            if (session != null && session.isOpen()) {
                // session.close();
            }
        }


    }


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


            // List of Orders to add
            List<Users> newOrders = Arrays.asList(nagham);

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            List<Users> existingMeals = session.createQuery(query).getResultList();

            // Save customizations
            session.save(nagham);
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
        if(getAllRestaurants() != null && !getAllRestaurants().isEmpty()) {
            System.out.println("there are restaurants in the database");
            return;
        }
        else
            System.out.println("no restaurants in the database");
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();


            LocalTime openingTime = LocalTime.of(10, 0); // 10:00 AM
            LocalTime closingTime = LocalTime.of(22, 0); // 10:00 PM

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

            List<Meal> meals = getAllMeals();
            int i = 0;
            List<Meal> nazarethMeals = new ArrayList<>();
            List<Meal> haifaMeals = new ArrayList<>();
            List<Meal> telavivMeals = new ArrayList<>();
            for (Meal meal : meals) {
                if (i % 2 == 0)
                    nazarethMeals.add(meal);
                if(i% 3 ==0)
                    haifaMeals.add(meal);
                telavivMeals.add(meal);
                i++;
            }
            telAviv.setMeals(telavivMeals);
            nazareth.setMeals(nazarethMeals);
            haifa.setMeals(haifaMeals);


            session.save(nazareth);
            session.save(haifa);
            session.save(telAviv);

            session.flush();
            session.getTransaction().commit();
            System.out.println("Successfully generated new restaurants.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("An error occurred while generating restaurants.", e);
        }
    }


    private static SimpleServer server;

    public static void main(String[] args) throws IOException {
        try {
            server = new SimpleServer(3000);
            server.listen();
            generateData();
            printAllData();
            printAllUsers();
            //generateOrders();
            generateRestaurants();
            initializeSampleTables();
            //generateBasicUser();
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
            Query<Long> tableCountQuery = session.createQuery("SELECT COUNT(*) FROM TableNode", Long.class);
            long tableCount = tableCountQuery.uniqueResult();

            if (tableCount > 0) {
                System.out.println("Tables already exist in the database. Skipping sample table generation.");
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
                    // Randomly choose capacity (2, 3, or 4)
                    int capacity = capacities.get(random.nextInt(capacities.size()));

                    TableNode table = new TableNode(restaurant, random.nextBoolean(), capacity, "available");
                    tables.add(table);
                    // Save table to the session
                    session.save(table);
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