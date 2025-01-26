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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Hello world!
 *
 */
public class App 
{

    private static Session session;
    public static List<Meal> meatlist;
    public static List<Users> users;
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
        configuration.addAnnotatedClass(Users.class);
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
        spaghetti.setImage(loadImage("spaghetti.jpg")); // Updated call

        Meal avocadoSalad = new Meal();
        avocadoSalad.setName("Avocado Salad");
        avocadoSalad.setDescription("Fresh avocado salad with onions");
        avocadoSalad.setPrice(7.00);
        avocadoSalad.setCustomizations(Arrays.asList(moreOnion));
        //avocadoSalad.setImage(Files.readAllBytes(imagePath("avocado_salad.png"))); // Add image
        avocadoSalad.setImage(loadImage("avocado_salad.png")); // Updated call

        Meal grills = new Meal();
        grills.setName("Grills");
        grills.setDescription("Mixed grilled meats with spices");
        grills.setPrice(12.00);
        grills.setCustomizations(Arrays.asList(highSpicyLevel));
        //grills.setImage(Files.readAllBytes(imagePath("grills.jpg"))); // Add image
        grills.setImage(loadImage("grills.jpg")); // Updated call

        Meal toastCheese = new Meal();
        toastCheese.setName("Toast Cheese");
        toastCheese.setDescription("Cheese toast with black bread");
        toastCheese.setPrice(5.00);
        toastCheese.setCustomizations(Arrays.asList(blackBread));
        //toastCheese.setImage(Files.readAllBytes(imagePath("toast_cheese.jpg"))); // Add image
        toastCheese.setImage(loadImage("toast_cheese.jpg")); // Updated call

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
    public static List<Users> getUsers() {
        Session localSession = null; // Local session for this method

        try {
            // Ensure the session is open or create a new session locally
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                localSession = sessionFactory.openSession(); // Use local session
            } else {
                localSession = session; // Use the shared session if available
            }

            // Perform the query
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            users = localSession.createQuery(query).getResultList();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        } finally {
            // Only close the local session if it was created locally
            if (localSession != null && localSession != session && localSession.isOpen()) {
                localSession.close();
            }
        }

        return users;
    }
    public static boolean checkUser(String username, String password) throws Exception {
    if (session == null || !session.isOpen()) {
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
    }
    CriteriaBuilder builder = session.getCriteriaBuilder();
    CriteriaQuery<Long> query = builder.createQuery(Long.class);
    Root<Users> root = query.from(Users.class);

    query.select(builder.count(root))
         .where(
             builder.and(
                 builder.equal(root.get("username"), username),
                 builder.equal(root.get("password"), password) // Hash password if applicable
             )
         );

    Long count = session.createQuery(query).getSingleResult();
    if (session != null && session.isOpen()) {
        session.close(); // Close the session after operation
    }

    return count > 0;
}
    public static boolean checkEmail(UserCheck us) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        // Use CriteriaBuilder to fetch the user
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Users> query = builder.createQuery(Users.class); // Change the return type to Users
        Root<Users> root = query.from(Users.class);

        query.select(root)
                .where(
                        builder.and(
                                builder.equal(root.get("username"), us.getUsername()),
                                builder.equal(root.get("email"), us.getEmail())
                        )
                );
        Users user = session.createQuery(query).uniqueResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }

        if (user != null) {
            us.setPassword(user.getPassword());
            return true;
        } else {
            return false;
        }
    }
    public static boolean checkUserName(String username) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Users> root = query.from(Users.class);

        query.select(builder.count(root))
                .where(
                        builder.and(
                                builder.equal(root.get("username"), username)// Hash password if applicable
                        )
                );

        Long count = session.createQuery(query).getSingleResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }

        return count > 0;
    }
    public static void getUserInfo(UserCheck us) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Users> query = builder.createQuery(Users.class);
        Root<Users> root = query.from(Users.class);

        query.select(root)
                .where(
                        builder.and(
                                builder.equal(root.get("username"), us.getUsername())
                        )
                );
        Users user = session.createQuery(query).uniqueResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }
        if(user != null) {
            us.setEmail(user.getEmail());
            us.setUsername(user.getUsername());
            us.setPassword(user.getPassword());
            us.setAge(user.getAge());
            us.setGender(user.getGender());
            us.setId(user.getId());
            us.setRole(user.getRole());
        }
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
    public static void addMealToList(Meal newMeal) {
        // Ensure meatlist is initialized
        if (meatlist == null) {
            meatlist = new ArrayList<>();
        }

        // Check if the meal already exists in the list (optional, if you want to avoid duplicates)
        boolean isDuplicate = meatlist.stream()
                .anyMatch(existingMeal -> existingMeal.getName().equalsIgnoreCase(newMeal.getName()) &&
                        existingMeal.getDescription().equalsIgnoreCase(newMeal.getDescription()));

        if (isDuplicate) {
            System.out.println("Meal already exists in the list: " + newMeal.getName());
        } else {
            // Add the new meal to the list
            meatlist.add(newMeal);
            //System.out.println("Added new meal to the list: " + newMeal.getName());
        }
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
    public static String AddNewMeal(mealEvent newMeal) {
        // Extract data from the mealEvent object
        String mealDisc = newMeal.getMealDisc();
        byte[] mealImage = newMeal.getImage();
        String mealName = newMeal.getMealName();
        String mealPrice = newMeal.getPrice();

        try {
            // Ensure the session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }

            session.beginTransaction();

            // Check for duplicates in the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            query.from(Meal.class);
            List<Meal> existingMeals = session.createQuery(query).getResultList();

            boolean isDuplicate = existingMeals.stream()
                    .anyMatch(existingMeal -> existingMeal.getName().equalsIgnoreCase(mealName) &&
                            existingMeal.getDescription().equalsIgnoreCase(mealDisc));

            if (isDuplicate) {
                System.out.println("Meal already exists in the database: " + mealName);
                return "exist";
            } else {
                // Create a new Meal entity and set its attributes
                Meal newM = new Meal();
                newM.setName(mealName);
                newM.setDescription(mealDisc);
                newM.setPrice(Double.parseDouble(mealPrice));
                newM.setImage(mealImage);

                // Save the meal to the database
                session.save(newM);

                // Add the meal to the local list
                addMealToList(newM);

                System.out.println("New meal added: " + mealName + " Id: " + newM.getId());
                newMeal.setId(String.valueOf(newM.getId()));
            }

            // Commit the transaction
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback on error
            }
            e.printStackTrace();
        } finally {
            // Leave the session open for further operations
            if (session != null && session.isOpen()) {
                session.close(); // Close the session after operation
            }
        }
        return "added";
    }

    public static String AddNewUser(UserCheck newUser) {
        // Extract data from the mealEvent object
        String UserName = newUser.getUsername();
        String UserPass = newUser.getPassword();
        String UserEmail = newUser.getEmail();
        String UserGender = newUser.getGender();
        int UserAge = newUser.getAge();


        try {
            // Ensure the session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }

            session.beginTransaction();

            // Check for duplicates in the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            List<Users> existingUsers = session.createQuery(query).getResultList();

            boolean isDuplicate = existingUsers.stream()
                    .anyMatch(existingMeal -> existingMeal.getUsername().equalsIgnoreCase(UserName));

            if (isDuplicate) {
                System.out.println("User already exist: " + UserName);
                return "User already exist";
            } else {
                Users newU = new Users();
                newU.setUsername(UserName);
                newU.setPassword(UserPass);
                newU.setEmail(UserEmail);
                newU.setGender(UserGender);
                newU.setAge(UserAge);
                newU.setRole("Customer");


                session.save(newU);
                users.add(newU);

                System.out.println("New User added: " + UserName);
            }

            // Commit the transaction
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback on error
            }
            e.printStackTrace();
        } finally {
            // Leave the session open for further operations
            if (session != null && session.isOpen()) {
                session.close(); // Close the session after operation
            }
        }
        return "Registration completed successfully";
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
    public static void printAllUsers() {
        try {
            System.out.println("\n=== Users List ===");
            for (Users user : getUsers()) {
                System.out.println(user.toString());
            }
        } catch (Exception e) {
            System.err.println("An error occurred while fetching users: " + e.getMessage());
            e.printStackTrace();
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
            printAllUsers();
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
    public static List<Resturant> Get_Resturant(String queryString) {
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

        List<Resturant> result = new ArrayList<>();
        try {
            // Begin the transaction for querying
            session.beginTransaction();

            // Create the query
            org.hibernate.query.Query<Resturant> query = session.createQuery(queryString, Resturant.class);

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

    public static List<Meal> GetAllMeals() {
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

        List<Meal> result = new ArrayList<>();
        try {
            // Begin the transaction for querying
            session.beginTransaction();

            // Create a query to find all meals without any constraint
            String queryString = "FROM Meal";  // No WHERE clause, fetch all meals
            org.hibernate.query.Query<Meal> query = session.createQuery(queryString, Meal.class);

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
        } finally {
            // Ensure session is closed after use
            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return result;
    }
}
