package il.cshaifasweng.OCSFMediatorExample.server;



import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import static il.cshaifasweng.OCSFMediatorExample.entities.DatabaseSessionManager.*;

/**
 * Hello world!
 *
 */
public class App 
{

    private static Session session;
    public static List<Meal> meatlist;
    private static String userinput = null;

    private static SessionFactory getSessionFactory() throws HibernateException {
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
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(Customization.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }



    static byte[] loadImage(String fileName) throws IOException {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("images/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }
            return inputStream.readAllBytes();
        }
    }
    static Path imagePath(String fileName) {
        return Paths.get("src/main/resources/images/" + fileName);
    }

    private static void generateData() throws Exception {
        // Helper function to read image as byte[]

        // Create Customizations
        Customization moreLettuce = new Customization();
        moreLettuce.setName("More Lettuce");

        Customization extraCheese = new Customization();
        extraCheese.setName("Extra Cheese");

        Customization moreOnion = new Customization();
        moreOnion.setName("More Onion");

        Customization highSpicyLevel = new Customization();
        highSpicyLevel.setName("High Spicy Level");

        Customization blackBread = new Customization();
        blackBread.setName("Black Bread");

        // Create Meals
        Meal burger = new Meal();
        burger.setName("Burger");
        burger.setDescription("Juicy beef burger with fresh lettuce");
        burger.setPrice(8.00);
        burger.setCustomizations(Arrays.asList(moreLettuce));
        //burger.setImage(Files.readAllBytes(imagePath("burger.jpg"))); // Add image
        burger.setImage(loadImage("burger.jpg")); // Updated call
        //System.out.println("Image size for " + burger.getName() + ": " + Files.readAllBytes(imagePath("burger.jpg")).length + " bytes");

        Meal spaghetti = new Meal();
        spaghetti.setName("Spaghetti");
        spaghetti.setDescription("Classic Italian spaghetti with cheese");
        spaghetti.setPrice(10.00);
        spaghetti.setCustomizations(Arrays.asList(extraCheese));
        //spaghetti.setImage(Files.readAllBytes(imagePath("spaghetti.jpg"))); // Add image
        burger.setImage(loadImage("spaghetti.jpg")); // Updated call

        Meal avocadoSalad = new Meal();
        avocadoSalad.setName("Avocado Salad");
        avocadoSalad.setDescription("Fresh avocado salad with onions");
        avocadoSalad.setPrice(7.00);
        avocadoSalad.setCustomizations(Arrays.asList(moreOnion));
        //avocadoSalad.setImage(Files.readAllBytes(imagePath("avocado_salad.png"))); // Add image
        burger.setImage(loadImage("avocado_salad.png")); // Updated call

        Meal grills = new Meal();
        grills.setName("Grills");
        grills.setDescription("Mixed grilled meats with spices");
        grills.setPrice(12.00);
        grills.setCustomizations(Arrays.asList(highSpicyLevel));
        //grills.setImage(Files.readAllBytes(imagePath("grills.jpg"))); // Add image
        burger.setImage(loadImage("grills.jpg")); // Updated call

        Meal toastCheese = new Meal();
        toastCheese.setName("Toast Cheese");
        toastCheese.setDescription("Cheese toast with black bread");
        toastCheese.setPrice(5.00);
        toastCheese.setCustomizations(Arrays.asList(blackBread));
        //toastCheese.setImage(Files.readAllBytes(imagePath("toast_cheese.jpg"))); // Add image
        burger.setImage(loadImage("toast_cheese.jpg")); // Updated call

        // List of meals to add
        List<Meal> newMeals = Arrays.asList(burger, spaghetti, avocadoSalad, grills, toastCheese);

        // Fetch existing meals from the database
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);
        List<Meal> existingMeals = session.createQuery(query).getResultList();

        // Save customizations
        session.save(moreLettuce);
        session.save(extraCheese);
        session.save(moreOnion);
        session.save(highSpicyLevel);
        session.save(blackBread);

        // Add only unique meals
        for (Meal newMeal : newMeals) {
            boolean isDuplicate = existingMeals.stream()
                    .anyMatch(existingMeal -> existingMeal.getName().equalsIgnoreCase(newMeal.getName()) &&
                            existingMeal.getDescription().equalsIgnoreCase(newMeal.getDescription()));

            if (!isDuplicate) {
                session.save(newMeal);
                System.out.println("Added new meal: " + newMeal.getName());
            } else {
                System.out.println("Duplicate meal skipped: " + newMeal.getName());
            }
        }

        session.flush();
    }


    public static List<Meal> getAllMeals() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);
        List<Meal> meals = session.createQuery(query).getResultList();
        meatlist = meals;
        // Force loading of customizations
        for (Meal meal : meals) {
            Hibernate.initialize(meal.getCustomizations());
        }
        return meals;
    }
    public static void updateMealPriceById(int mealId, double newPrice) {
        // Check if the meatlist is initialized
        if (meatlist == null || meatlist.isEmpty()) {
            System.out.println("The meal list is empty or not initialized.");
            return;
        }

        // Search for the meal with the given ID
        for (Meal meal : meatlist) {
            if (meal.getId() == mealId) {
                //meal.setPrice(newPrice); // Update the price
                meal.setPrice(newPrice);
                System.out.println("Meal ID " + mealId + " price: " + meal.getPrice());
                return; // Exit the loop after updating
            }
        }

        // If meal with the given ID is not found
        //System.out.println("Meal with ID " + mealId + " not found.");
    }

    public static void updateMealPriceInDatabase(updatePrice updatePrice) {
        //System.out.println("Changing the price in database.");
        int mealId = updatePrice.getIdMeal();
        double newPrice = updatePrice.getNewPrice();

        try {
            if (session == null || !session.isOpen()) {
                //System.out.println("Session is not initialized. Creating a new session.");
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            if (session.getTransaction().isActive()) {
                //System.out.println("Transaction already active. Rolling it back.");
                session.getTransaction().rollback();
            }

            session.beginTransaction();
            //System.out.println("Started transaction to update meal price.");

            // Fetch the meal by ID from the current session
            Meal meal = session.get(Meal.class, mealId);
            if (meal != null) {
                //System.out.println("Found Meal: " + meal.getName() + " with current price: " + meal.getPrice());
                meal.setPrice(newPrice); // Update the price
                session.update(meal); // Persist the changes
                session.getTransaction().commit(); // Commit the transaction
                updateMealPriceById(mealId, newPrice);
                //System.out.println("Updated price for Meal ID " + mealId + " to " + newPrice);
            } else {
                //System.out.println("Meal with ID " + mealId + " not found in the database.");
            }
        } catch (Exception e) {
            //System.out.println("An error occurred during the update operation.");
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback on error
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Close the session after operation
                //System.out.println("Session closed.");
            }
        }
        //System.out.println("Finished updating the price in the database.");
    }






    public static List<mealEvent> getmealEvent() throws Exception {
        List<mealEvent> result = new ArrayList<>(); // Initialize the result list
        // Force loading of customizations
        for (Meal meal : meatlist) {
            mealEvent event = new mealEvent(meal.getName(),meal.getDescription(),String.valueOf(meal.getPrice()), String.valueOf(meal.getId()),meal.getImage());
            result.add(event);
            /*here i  want to save the every meal as mealEvent then push it to the List*/
        }
        //System.out.println(result.get(0).toString());
        return result;
    }


    private static void printAllData() throws Exception {
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
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        try {
            server = new SimpleServer(3000);
            server.listen();
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            generateData();
            printAllData();
            session.getTransaction().commit();
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
        //server = new SimpleServer(3000);
        //server.listen();
}
