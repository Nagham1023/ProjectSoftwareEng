package il.cshaifasweng.OCSFMediatorExample.server;



import java.io.IOException;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import java.util.ArrayList;
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

        configuration.getProperties().forEach((key, value) -> System.out.println(key + ": " + value));
        // Add Meal and Customization entities
        configuration.addAnnotatedClass(Complain.class);
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(Customization.class);
        configuration.addAnnotatedClass(Users.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }




    static Path imagePath(String fileName) {
        return Paths.get("src/main/resources/images/" + fileName);
    }


    public static void printAllData() throws Exception {
        System.out.println("\n=== Meals List ===");
        for (Meal meal : getAllMeals()) {
            System.out.printf("Meal: %s | Price: %.2f%n", meal.getName(), meal.getPrice());
            System.out.println("Customizations:");
            for (Customization customization : meal.getCustomizations()) {
                System.out.printf("  - %s ", customization.getName());
            }
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
