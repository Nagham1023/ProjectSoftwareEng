package il.cshaifasweng.OCSFMediatorExample.server;



import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.UsersDB.printAllUsers;

public class App 
{

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

    //for my developing
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
        // Helper function to read image as byte[]
        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        session.beginTransaction();
        try {
            // Create restaurant instances
            Restaurant nazareth = new Restaurant();
            nazareth.setRestaurantName("Nazareth");
            nazareth.setImagePath("nazareth.jpg");
            nazareth.setPhoneNumber("123-456-7890");

            Restaurant haifa = new Restaurant();
            haifa.setRestaurantName("Haifa");
            haifa.setImagePath("haifa.jpg");
            haifa.setPhoneNumber("234-567-8901");

            Restaurant telAviv = new Restaurant();
            telAviv.setRestaurantName("Tel Aviv");
            telAviv.setImagePath("telaviv.jpg");
            telAviv.setPhoneNumber("345-678-9012");

//            Restaurant jerusalem = new Restaurant();
//            jerusalem.setRestaurantName("Jerusalem");
//            jerusalem.setImagePath("jerusalem.jpg");
//            jerusalem.setPhoneNumber("456-789-0123");

            // Save restaurants to the database
            session.save(nazareth);
            session.save(haifa);
            session.save(telAviv);
//            session.save(jerusalem);

            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            // Rollback transaction in case of an error
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new Exception("An error occurred while generating the user.", e);
        }
    }


    private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        try {
            server = new SimpleServer(3000);
            server.listen();
            generateData();
            printAllData();
            printAllUsers();
            generateOrders();
            //generateRestaurants();
            generateBasicUser();
        } catch (Exception exception) {
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        }
    }
    public static List<Restaurant> Get_Restaurant(String queryString) {
        // Ensure the session is open
        if (session == null || !session.isOpen()) {
            try {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            } catch (HibernateException e) {
                System.err.println("Error initializing Hibernate session: " + e.getMessage());
                return null;
            }
        }

        List<Restaurant> result = new ArrayList<>();
        try {
            // Begin the transaction for querying
            session.beginTransaction();

            // Create the query
            org.hibernate.query.Query<Restaurant> query = session.createQuery(queryString, Restaurant.class);

            // Execute the query and get the result list
            result = query.getResultList();

            // Commit the transaction
            session.getTransaction().commit();
        } catch (Exception e) {
            // Rollback the transaction if something went wrong
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }finally {
            // Leave the session open for further operations
            if (session != null && session.isOpen()) {
                session.close(); // Close the session after operation
            }
        }

        return result;
    }
}
