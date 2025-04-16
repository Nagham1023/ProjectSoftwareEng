package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javassist.Loader;
import org.hibernate.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.*;

public class MealsDB {

    private static Session session;
    public static List<Meal> meatlist;
    public static Set<Customization> customizationsList;

    static byte[] loadImage(String fileName) throws IOException {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("images/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }
            return inputStream.readAllBytes();
        }
    }

    public static void generateData() throws Exception {
        customizationsList = new HashSet<>();
        Transaction transaction = null;
        Session session = null;

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Expanded list of customization names
            List<String> customizationNames = Arrays.asList(
                    // Common burger components
                    "Regular Bread", "Black Bread", "Lettuce", "Tomato", "Pickles", "Onion",
                    "Beef Patty", "Chicken Patty", "Cheese", "Extra Cheese",
                    "Ketchup", "Mayonnaise", "Spicy Sauce", "Mustard",

                    // Pasta related
                    "Grated Cheese", "Extra Sauce", "Basil",

                    // Salad related
                    "Avocado", "Olive Oil", "Vinaigrette", "Red Onion",

                    // Grill components
                    "High Spicy Level", "BBQ Sauce", "Garlic Sauce",

                    // Toast
                    "White Cheese", "Toast Black Bread"
            );

            Map<String, Customization> savedCustomizations = new HashMap<>();

            for (String name : customizationNames) {
                Customization existing = (Customization) session.createQuery(
                                "FROM Customization WHERE customizationName = :name")
                        .setParameter("name", name)
                        .uniqueResult();

                if (existing == null) {
                    Customization customization = new Customization();
                    customization.setName(name);
                    session.save(customization);
                    savedCustomizations.put(name, customization);
                    System.out.println("Added new customization: " + name);
                } else {
                    savedCustomizations.put(name, existing);
                    System.out.println("Using existing customization: " + name);
                }
            }

            session.flush(); // Make sure customizations are saved before assigning

            // Meals with expanded customizations
            Meal burger = new Meal();
            burger.setName("Burger");
            burger.setDescription("Juicy beef burger with fresh lettuce");
            burger.setPrice(8.00);
            burger.setCustomizations(Set.of(
                    savedCustomizations.get("Regular Bread"),
                    savedCustomizations.get("Lettuce"),
                    savedCustomizations.get("Tomato"),
                    savedCustomizations.get("Pickles"),
                    savedCustomizations.get("Onion"),
                    savedCustomizations.get("Beef Patty"),
                    savedCustomizations.get("Cheese"),
                    savedCustomizations.get("Ketchup"),
                    savedCustomizations.get("Mayonnaise"),
                    savedCustomizations.get("Spicy Sauce")
            ));
            burger.setImage(loadImage("burger.jpg"));

            Meal spaghetti = new Meal();
            spaghetti.setName("Spaghetti");
            spaghetti.setDescription("Classic Italian spaghetti with cheese");
            spaghetti.setPrice(10.00);
            spaghetti.setCustomizations(Set.of(
                    savedCustomizations.get("Grated Cheese"),
                    savedCustomizations.get("Extra Sauce"),
                    savedCustomizations.get("Basil")
            ));
            spaghetti.setImage(loadImage("spaghetti.jpg"));

            Meal avocadoSalad = new Meal();
            avocadoSalad.setName("Avocado Salad");
            avocadoSalad.setDescription("Fresh avocado salad with onions");
            avocadoSalad.setPrice(7.00);
            avocadoSalad.setCustomizations(Set.of(
                    savedCustomizations.get("Avocado"),
                    savedCustomizations.get("Red Onion"),
                    savedCustomizations.get("Olive Oil"),
                    savedCustomizations.get("Vinaigrette")
            ));
            avocadoSalad.setImage(loadImage("avocado_salad.png"));

            Meal grills = new Meal();
            grills.setName("Grills");
            grills.setDescription("Mixed grilled meats with spices");
            grills.setPrice(12.00);
            grills.setCustomizations(Set.of(
                    savedCustomizations.get("High Spicy Level"),
                    savedCustomizations.get("BBQ Sauce"),
                    savedCustomizations.get("Garlic Sauce")
            ));
            grills.setImage(loadImage("grills.jpg"));

            Meal toastCheese = new Meal();
            toastCheese.setName("Toast Cheese");
            toastCheese.setDescription("Cheese toast with black bread");
            toastCheese.setPrice(5.00);
            toastCheese.setCustomizations(Set.of(
                    savedCustomizations.get("White Cheese"),
                    savedCustomizations.get("Toast Black Bread")
            ));
            toastCheese.setImage(loadImage("toast_cheese.jpg"));

            List<Meal> newMeals = Arrays.asList(burger, spaghetti, avocadoSalad, grills, toastCheese);

            for (Meal newMeal : newMeals) {
                Meal existing = (Meal) session.createQuery(
                                "FROM Meal WHERE mealName = :name")
                        .setParameter("name", newMeal.getName())
                        .uniqueResult();

                if (existing == null) {
                    session.save(newMeal);
                    System.out.println("Added new meal: " + newMeal.getName());
                } else {
                    System.out.println("Duplicate meal skipped: " + newMeal.getName());
                }
            }

            transaction.commit();
            System.out.println("Transaction committed successfully.");

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
                System.err.println("An error occurred, transaction rolled back: " + e.getMessage());
            }
            throw e;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
                System.out.println("Session closed.");
            }
        }
    }



    public static void getAllRestMeals() {
        Session session = null;
        Transaction transaction = null;

        try {
            System.out.println("Getting all meals from DBbbb...");
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            List<Restaurant> restaurants = session.createQuery("FROM Restaurant", Restaurant.class).list();
            List<Meal> meals = session.createQuery("FROM Meal", Meal.class).list();


            SimpleServer.allMeals = meals;
            RestMealsList = restaurants;
            for (Restaurant restaurant : restaurants) {
                System.out.println("Restaurant: " + restaurant.getRestaurantName());
                System.out.println("Meals:");

                for (Meal meal : restaurant.getMeals()) {
                    System.out.println("  - " + meal.getName() + ": " + meal.getDescription() + " ($" + meal.getPrice() + ")");
                }

                System.out.println("------------------------------------------------");


            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public static void generateCompanyMeals() throws Exception {
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Check if company meals already exist to avoid duplicates
            List<Meal> existingCompanyMeals = session.createQuery("FROM Meal WHERE isCompany = true", Meal.class).list();
            if (!existingCompanyMeals.isEmpty()) {
                System.out.println("Company meals already exist in the database.");
                return;
            }

            // Create new company meals
            Meal companyMeal1 = new Meal();
            companyMeal1.setMealName("Company Burger");
            companyMeal1.setDescription("Premium burger for corporate events");
            companyMeal1.setPrice(29);
            companyMeal1.setCompany(true);
            companyMeal1.setDelivery(true);
            companyMeal1.setCustomizations(customizationsList);
            companyMeal1.setImage(loadImage("cheeseburger.jpg"));

            Meal companyMeal2 = new Meal();
            companyMeal2.setMealName("Executive Pizza");
            companyMeal2.setDescription("Gourmet pizza for team meetings");
            companyMeal2.setPrice(39);
            companyMeal2.setCompany(true);
            companyMeal2.setDelivery(false);
            companyMeal2.setCustomizations(customizationsList);
            companyMeal2.setImage(loadImage("pizza.jpg"));

            // Save company meals to the database
            session.persist(companyMeal1);
            session.persist(companyMeal2);

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
            query.from(Restaurant.class);
            List<Restaurant> restaurants = session.createQuery(query).getResultList();

            // Add company meals to each restaurant
            for (Restaurant restaurant : restaurants) {
                List<Meal> currentMeals = restaurant.getMeals();
                currentMeals.add(companyMeal1);
                currentMeals.add(companyMeal2);
                restaurant.setMeals(currentMeals);
                session.update(restaurant); // Update the restaurant entity
            }

            session.getTransaction().commit();
            System.out.println("Successfully generated company meals and associated them with all restaurants.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to generate company meals: " + e.getMessage(), e);
        }
    }
    /*********************************new get all meal*************************************/
//    public static List<Meal> GetAllMeals(){
//        List<Meal> result = new ArrayList<>();
//        Session session = null;
//        Transaction transaction = null;
//
//        try {
//            SessionFactory sessionFactory = getSessionFactory();
//            session = sessionFactory.openSession(); // New session for every call
//            transaction = session.beginTransaction();
//
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
//            Root<Meal> root = query.from(Meal.class);
//            query.select(root);
//
//            // Execute query with cache disabled
//            result = session.createQuery(query)
//                    .setCacheable(false) // Bypass Hibernate cache
//                    .getResultList();
//
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            if (session != null && session.isOpen()) {
//                session.close(); // Ensure session is closed
//            }
//        }
//        return result;
//    }
/*************Catched DATA getALLMeal*********************************/

    public static List<Meal> getAllMeals() throws Exception {
        Session localSession = null;

        if(SimpleServer.allMeals != null) {
            return SimpleServer.allMeals;
        }
        List<Meal> meals = null;
        try {
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                localSession = sessionFactory.openSession();
            } else {
                localSession = session;
            }

            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            query.from(Meal.class);
            meals = localSession.createQuery(query).getResultList();
            meatlist = meals;
            allMeals = meals;

            for (Meal meal : meals) {
                Hibernate.initialize(meal.getCustomizations());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return new ArrayList<>();
        } finally {
            // Only close the local session if it was created locally
            if (localSession != null && localSession != session && localSession.isOpen()) {
                localSession.close();
            }
        }
        return meals;
    }
    /********************************/
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
        double newDiscount = updatePrice.getDiscount();
        if(allMeals != null) {
            for(Meal meal : allMeals) {
                if (meal.getId() == mealId) {
                    meal.setPrice(newPrice);
                    meal.setDiscount_percentage(newDiscount);
                    break;
                }
            }
        }
        if (RestMealsList != null) {
            for (Restaurant restaurant : RestMealsList) {
                if (restaurant.getMeals() != null) {
                    for (Meal meal : restaurant.getMeals()) {
                        if (meal.getId() == mealId) {
                            meal.setPrice(newPrice);
                            meal.setDiscount_percentage(newDiscount);
                            break;
                        }
                    }
                }
            }
        }

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
                meal.setDiscount_percentage(newDiscount);
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

    public static void updatePriceDeleteReq(updatePrice updatePrice) {
        //System.out.println("Changing the price in database.");
        int mealId = updatePrice.getIdMeal();
        double newPrice = updatePrice.getNewPrice();
        double newDiscount = updatePrice.getDiscount();
        if(allMeals != null) {
            for(Meal meal : allMeals) {
                if (meal.getId() == mealId) {
                    meal.setPrice(newPrice);
                    meal.setDiscount_percentage(newDiscount);
                    break;
                }
            }
        }
        if (RestMealsList != null) {
            for (Restaurant restaurant : RestMealsList) {
                if (restaurant.getMeals() != null) {
                    for (Meal meal : restaurant.getMeals()) {
                        if (meal.getId() == mealId) {
                            meal.setPrice(newPrice);
                            meal.setDiscount_percentage(newDiscount);
                            break;
                        }
                    }
                }
            }
        }

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
                meal.setDiscount_percentage(newDiscount);
                session.update(meal); // Persist the changes
                session.getTransaction().commit(); // Commit the transaction
                updateMealPriceById(mealId, newPrice);
                //System.out.println("Updated price for Meal ID " + mealId + " to " + newPrice);
            } else {
                //System.out.println("Meal with ID " + mealId + " not found in the database.");
            }
            /*delete req*/
            // Find all requests linked to this meal
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UpdatePriceRequest> query = builder.createQuery(UpdatePriceRequest.class);
            Root<UpdatePriceRequest> root = query.from(UpdatePriceRequest.class);
            query.where(builder.equal(root.get("meal"), meal));

            List<UpdatePriceRequest> requests = session.createQuery(query).getResultList();


            // Delete all found requests
            for (UpdatePriceRequest req : requests) {
                allUpdateRequests.removeIf(up -> Objects.equals(up.getMealId(), String.valueOf(mealId)));
                session.delete(req);
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
//    public static String AddNewMeal(mealEvent newMeal) {
//        List<Customization> customizations = new ArrayList<>();
//        // Extract data from the mealEvent object
//        String mealDisc = newMeal.getMealDisc();
//        byte[] mealImage = newMeal.getImage();
//        String mealName = newMeal.getMealName();
//        String mealPrice = newMeal.getPrice();
//        for(String custom: newMeal.getCustomizationList()){
//            Customization customization = new Customization();
//            customization.setName(custom);
//            customizations.add(customization);
//        }
//
//        try {
//            // Ensure the session is open
//            if (session == null || !session.isOpen()) {
//                SessionFactory sessionFactory = getSessionFactory();
//                session = sessionFactory.openSession();
//            }
//
//            if (session.getTransaction().isActive()) {
//                session.getTransaction().rollback();
//            }
//
//            session.beginTransaction();
//
//            // Check for duplicates in the database
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
//            query.from(Meal.class);
//            List<Meal> existingMeals = session.createQuery(query).getResultList();
//
//            boolean isDuplicate = existingMeals.stream()
//                    .anyMatch(existingMeal -> existingMeal.getName().equalsIgnoreCase(mealName) &&
//                            existingMeal.getDescription().equalsIgnoreCase(mealDisc));
//
//            if (isDuplicate) {
//                System.out.println("Meal already exists in the database: " + mealName);
//                return "exist";
//            } else {
//                // Create a new Meal entity and set its attributes
//                Meal newM = new Meal();
//                newM.setName(mealName);
//                newM.setDescription(mealDisc);
//                newM.setPrice(Double.parseDouble(mealPrice));
//                newM.setImage(mealImage);
//
//                // Save the meal to the database
//                session.save(newM);
//
//                // Add the meal to the local list
//                addMealToList(newM);
//
//                System.out.println("New meal added: " + mealName + " Id: " + newM.getId());
//                newMeal.setId(String.valueOf(newM.getId()));
//            }
//
//            // Commit the transaction
//            session.getTransaction().commit();
//
//        } catch (Exception e) {
//            if (session.getTransaction() != null) {
//                session.getTransaction().rollback(); // Rollback on error
//            }
//            e.printStackTrace();
//        } finally {
//            // Leave the session open for further operations
//            if (session != null && session.isOpen()) {
//                session.close(); // Close the session after operation
//            }
//        }
//        return "added";
//    }
    public static Meal AddNewMeal(mealEvent newMeal) {
        Session session = null;
        Transaction transaction = null;
        try {
            // 1. Initialize Hibernate session
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // 2. Check for duplicate meals
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> mealQuery = builder.createQuery(Meal.class);
            Root<Meal> root = mealQuery.from(Meal.class);
            mealQuery.where(
                    builder.equal(
                            builder.lower(root.get("mealName")),
                            newMeal.getMealName().toLowerCase()
                    )
            );

            List<Meal> existingMeals = session.createQuery(mealQuery).getResultList();
            if (!existingMeals.isEmpty()) {
                return existingMeals.get(0);
            }

            // 3. Create and configure the new meal
            Meal newMealEntity = new Meal();
            newMealEntity.setName(newMeal.getMealName());
            newMealEntity.setDescription(newMeal.getMealDisc());
            newMealEntity.setPrice(Double.parseDouble(newMeal.getPrice()));
            newMealEntity.setImage(newMeal.getImage());
            newMealEntity.setCompany(newMeal.isCompany());
            newMealEntity.setDelivery(true);

            // 4. Process customizations
            List<Customization> customizations = new ArrayList<>();
            for (String customizationName : newMeal.getCustomizationList()) {
                // Check if customization already exists
                CriteriaQuery<Customization> customQuery = builder.createQuery(Customization.class);
                Root<Customization> customRoot = customQuery.from(Customization.class);
                customQuery.where(
                        builder.equal(
                                builder.lower(customRoot.get("customizationName")),
                                customizationName.toLowerCase()
                        )
                );

                List<Customization> existingCustoms = session.createQuery(customQuery).getResultList();

                Customization customization;
                if (!existingCustoms.isEmpty()) {
                    customization = existingCustoms.get(0); // Use existing
                } else {
                    customization = new Customization(); // Create new
                    customization.setName(customizationName);
                    session.persist(customization); // Save new customization
                }

                // Bidirectional sync
                if (!customization.getMeals().contains(newMealEntity)) {
                    customization.getMeals().add(newMealEntity);
                }
                if (!newMealEntity.getCustomizations().contains(customization)) {
                    newMealEntity.getCustomizations().add(customization);
                }
            }
            if(allMeals == null) {
                allMeals = new ArrayList<>();
            }
            allMeals.add(newMealEntity);
            // 5. Save to database
            session.persist(newMealEntity);
            transaction.commit();

            newMeal.setId(String.valueOf(newMealEntity.getId()));
            return newMealEntity;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }
    public static List<mealEvent> getmealEvent() throws Exception {
        List<mealEvent> result = new ArrayList<>();
        for (Meal meal : meatlist) {
            mealEvent event = new mealEvent(meal.getName(),meal.getDescription(),String.valueOf(meal.getPrice()), String.valueOf(meal.getId()),meal.getImage());
            result.add(event);
        }
        return result;
    }
    public static List<mealEvent> getmealEventbranched(List<Meal> meals) throws Exception {
        List<mealEvent> result = new ArrayList<>();
        for (Meal meal : meals) {
            mealEvent event = new mealEvent(meal.getName(),meal.getDescription(),String.valueOf(meal.getPrice()), String.valueOf(meal.getId()),meal.getImage());
            result.add(event);
        }
        return result;
    }
    public static List<Meal> getmealsb(String branchName) throws Exception {


        for (Restaurant restaurant : RestMealsList) {
            if (restaurant.getRestaurantName().equalsIgnoreCase(branchName)) {
                //System.out.println("Found restaurant: " + restaurant.getRestaurantName());
                return restaurant.getMeals();
            }
        }
        //System.out.println("No restaurant found");

        List<Meal> meals = new ArrayList<>();

        // Open a session if it's not already open
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        // Create a criteria query to find the restaurant by name
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
        Root<Restaurant> root = query.from(Restaurant.class);

        query.select(root)
                .where(
                        builder.and(
                                builder.equal(root.get("restaurantName"), branchName)
                        )
                );

        // Execute the query and get the restaurant
        Restaurant Branch = session.createQuery(query).uniqueResult();

        // Initialize the meals collection before closing the session
        if (Branch != null) {
            Hibernate.initialize(Branch.getMeals());
            meals = Branch.getMeals();
            // Initialize nested collections for each Meal
            for (Meal meal : meals) {
                Hibernate.initialize(meal.getCustomizations());
                Hibernate.initialize(meal.getRestaurants());
            }
        }

        // Close the session after the operation
        if (session != null && session.isOpen()) {
            session.close();
        }


        return meals;
    }

    public static List<Meal> GetAllMeals() {
        if(allMeals != null)
            return allMeals;
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
            meatlist = result;
            allMeals = result;

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
            for (Meal meal : result) {
                Hibernate.initialize(meal.getCustomizations()); // Force initialization
            }

            if (session != null && session.isOpen()) {
                session.close();
            }
        }

        return result;
    }

    public static MealUpdateRequest AddUpdatePriceRequest(updatePrice priceRequest) {
        // Extract data from the priceRequest object
        int mealId = priceRequest.getIdMeal();
        double newPrice = priceRequest.getNewPrice();
        MealUpdateRequest result = new MealUpdateRequest();

        Session session = null;
        Transaction transaction = null;
        try {
            session = App.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // Fetch the Meal entity using its ID
            Meal meal = session.get(Meal.class, mealId);
            if (meal == null) {
                System.out.println("Meal not found with ID: " + mealId);
                return result;
            }

            // Check for duplicates: Existing requests for the same meal
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UpdatePriceRequest> query = builder.createQuery(UpdatePriceRequest.class);
            Root<UpdatePriceRequest> root = query.from(UpdatePriceRequest.class);

            // Compare the Meal object in existing requests
            Predicate mealPredicate = builder.equal(root.get("meal"), meal);
            query.where(mealPredicate);

            List<UpdatePriceRequest> existingRequests = session.createQuery(query).getResultList();

            if (!existingRequests.isEmpty()) {
                System.out.println("A price change request already exists for meal ID: " + mealId);
                UpdatePriceRequest existing = existingRequests.get(0);
                result = new MealUpdateRequest(
                        String.valueOf(meal.getId()),
                        meal.getName(),
                        meal.getDescription(),
                        meal.getImage(),
                        existing.getOldPrice(),
                        existing.getNewPrice(),
                        existing.getOldDiscount(),
                        existing.getNewDiscount()
                );
                return result;
            }

            // Create a new request and link it to the Meal
            UpdatePriceRequest updateRequest = new UpdatePriceRequest();
            updateRequest.setMeal(meal); // Link the Meal object
            updateRequest.setOldPrice(meal.getPrice());
            updateRequest.setNewPrice(newPrice);
            updateRequest.setOldDiscount(meal.getDiscount_percentage());
            updateRequest.setNewDiscount(priceRequest.getDiscount());

            meal.getPriceRequests().add(updateRequest); // Update the Meal's list

            session.save(updateRequest);
            session.flush();
            transaction.commit(); // Explicit commit

            // Return the complete DTO
            result = new MealUpdateRequest(
                    String.valueOf(meal.getId()),
                    meal.getName(),
                    meal.getDescription(),
                    meal.getImage(),
                    meal.getPrice(),  // old price
                    newPrice,
                    meal.getDiscount_percentage(),
                    priceRequest.getDiscount()
            );
            allUpdateRequests.add(result);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            result.setStatus("error");
            result.setMealId(String.valueOf(mealId));
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Close manually
            }
        }
        return result;
    }
    public static void deletePriceChangeReq(int mealId) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch the Meal entity
            Meal meal = session.get(Meal.class, mealId);
            if (meal == null) {
                System.out.println("Meal not found.");
                return;
            }

            // Find all requests linked to this meal
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<UpdatePriceRequest> query = builder.createQuery(UpdatePriceRequest.class);
            Root<UpdatePriceRequest> root = query.from(UpdatePriceRequest.class);
            query.where(builder.equal(root.get("meal"), meal));

            List<UpdatePriceRequest> requests = session.createQuery(query).getResultList();

            // Delete all found requests
            for (UpdatePriceRequest req : requests) {
                allUpdateRequests.removeIf(up -> Objects.equals(up.getMealId(), String.valueOf(mealId)));
                session.delete(req);
            }

            session.getTransaction().commit();
            System.out.println("Deleted " + requests.size() + " request(s) for meal ID: " + mealId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String deleteMeal(int mealId) {
        String mealName = null;
        Transaction tx = null;
        Session session = null;

        try {
            session = App.getSessionFactory().openSession();
            tx = session.beginTransaction();

            Meal meal = session.get(Meal.class, mealId);
            if (meal == null) {
                return "Meal not found";
            }

            mealName = meal.getName();


            session.createNativeQuery("DELETE FROM meal_customizations WHERE meal_id = :mealId")
                    .setParameter("mealId", mealId)
                    .executeUpdate();

            session.createNativeQuery("DELETE FROM restaurant_meals WHERE meal_id = :mealId")
                    .setParameter("mealId", mealId)
                    .executeUpdate();

            session.createQuery("DELETE FROM UpdatePriceRequest pr WHERE pr.meal.id = :mealId")
                    .setParameter("mealId", mealId)
                    .executeUpdate();

            session.createQuery("DELETE FROM MealInTheCart mic " +
                            "WHERE EXISTS (SELECT 1 FROM personal_Meal pm WHERE pm.id = mic.meal.id AND pm.meal.id = :mealId)")
                    .setParameter("mealId", mealId)
                    .executeUpdate();

            session.createQuery("DELETE FROM personal_Meal pm WHERE pm.meal.id = :mealId")
                    .setParameter("mealId", mealId)
                    .executeUpdate();


            meal.getRestaurants().clear();
            meal.getCustomizations().clear();
            meal.getPriceRequests().clear();
            meal.getPersonalMeals().clear();
            allUpdateRequests.removeIf(l -> Integer.parseInt(l.getMealId()) == mealId);

            session.refresh(meal);
            session.delete(meal);

            tx.commit();

            if (allMeals != null) {
                allMeals.removeIf(m -> m.getId() == mealId);
            }
            if (RestMealsList != null) {
                for (Restaurant restaurant : RestMealsList) {
                    restaurant.getMeals().removeIf(m -> m.getId() == mealId);
                }
            }

            return mealName;

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return "Sorry, couldn't successfully delete meal.";
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }


    public static Meal getMealById(String mealId) {
        System.out.println("now I am in the function getMealById");
        for(Meal meal:allMeals)
        {
            if(meal.getId() == Integer.parseInt(mealId))
            {
                return meal;
            }
        }
        return null;
    }

    public static void updateMealDetailsById(Meal mealS) {
        System.out.println("updating the meal details");
        // Check if the meatlist is initialized
        if (allMeals == null || allMeals.isEmpty()) {
            System.out.println("The meal list is empty or not initialized.");
            return;
        }


        for (int i = 0; i < allMeals.size(); i++) {
            if (allMeals.get(i).getId() == mealS.getId()) {
                allMeals.set(i, mealS); // Replace the actual object in the list
                //System.out.println("updated the meal details");
                break;
            }
        }

        for (Restaurant restaurant : RestMealsList) {
            List<Meal> meals = restaurant.getMeals();
            if (meals == null) continue;

            for (int i = 0; i < meals.size(); i++) {
                Meal meal = meals.get(i);
                if (meal.getId() == mealS.getId()) {
                    meals.set(i, mealS); // Replace old meal with the updated one
                    //System.out.println("Updated meal in restaurant: " + restaurant.getRestaurantName());
                }
            }
        }

    }

    public static String updateMeal(UpdateMealRequest msg) {
        Session session = null;
        Transaction transaction = null;
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // 1. Fetch the meal
            Meal meal = session.get(Meal.class, Integer.parseInt(msg.getMealId()));
            if (meal == null) return "not exist";

            // 2. Update basic fields
            meal.setDescription(msg.getNewDescription());

            // 3. Process Customizations --------------------------------------------
            // Clear existing customizations
            List<Customization> oldCustomizations = new ArrayList<>(meal.getCustomizations());
            for (Customization oldCustom : oldCustomizations) {
                oldCustom.getMeals().remove(meal);  // Update inverse side
                meal.getCustomizations().remove(oldCustom);
            }

            // Add new customizations
            for (String customizationName : msg.getNewCustomizations()) {
                // Case-insensitive search
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Customization> query = builder.createQuery(Customization.class);
                Root<Customization> root = query.from(Customization.class);
                query.where(builder.equal(builder.lower(root.get("customizationName")),
                        customizationName.toLowerCase()));

                Customization customization = session.createQuery(query).uniqueResult();

                if (customization == null) {
                    customization = new Customization();
                    customization.setName(customizationName);
                    session.persist(customization);  // Save new customization
                }

                // Sync both sides of the relationship
                if (!customization.getMeals().contains(meal)) {
                    customization.getMeals().add(meal);
                }
                meal.getCustomizations().add(customization);
            }

            // 4. Process Restaurants ----------------------------------------------
            if ("ALL".equals(msg.getNewBranches().get(0))) // Safe even if branchName is null)
             {
                meal.setCompany(true);
                // Clear restaurant associations for company-wide meals
                for (Restaurant restaurant : meal.getRestaurants()) {
                    restaurant.getMeals().remove(meal);  // Update owning side
                }
                for(Restaurant restaurant : RestMealsList)
                {
                    restaurant.getMeals().removeIf(tempMeal -> tempMeal.getId() == meal.getId());
                }
                meal.getRestaurants().clear();
                 CriteriaBuilder builder = session.getCriteriaBuilder();
                 CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
                 query.from(Restaurant.class);
                 List<Restaurant> allRestaurants = session.createQuery(query).getResultList();

                 for (Restaurant restaurant : RestMealsList) {
                     restaurant.getMeals().add(meal);
                 }
                 for (Restaurant restaurant : allRestaurants) {
                     if (!restaurant.getMeals().contains(meal)) {
                         restaurant.getMeals().add(meal);
                         session.merge(restaurant); // Make sure restaurant gets updated
                     }
                     meal.getRestaurants().add(restaurant); // Maintain both sides
                     //System.out.println("added rest to meal");
                 }
            } else {
                meal.setCompany(false);
                List<String> newBranches = msg.getNewBranches();

                for(Restaurant restaurant : RestMealsList)
                {
                    restaurant.getMeals().removeIf(tempMeal -> tempMeal.getId() == meal.getId());
                }
                // Clear existing restaurants
                for (Restaurant existingRestaurant : meal.getRestaurants()) {
                    existingRestaurant.getMeals().remove(meal); // Update owning side
                }
                meal.getRestaurants().clear();

                // Add new restaurants from the list (skip "ALL" if present)
                for (String branchName : newBranches) {
                    if ("ALL".equalsIgnoreCase(branchName)) continue; // Skip "ALL"

                    CriteriaBuilder builder = session.getCriteriaBuilder();
                    CriteriaQuery<Restaurant> query = builder.createQuery(Restaurant.class);
                    Root<Restaurant> root = query.from(Restaurant.class);
                    query.where(builder.equal(root.get("restaurantName"), branchName));

                    Restaurant restaurant = session.createQuery(query).uniqueResult();
                    if (restaurant == null) {
                        return "Restaurant '" + branchName + "' not found";
                    }
                    for(Restaurant rest : RestMealsList)
                    {
                        if(rest.getRestaurantName().equals(branchName))
                            rest.getMeals().add(meal);
                    }

                    // Add meal to the restaurant (owning side)
                    if (!restaurant.getMeals().contains(meal)) {
                        restaurant.getMeals().add(meal);
                        session.merge(restaurant);
                    }
                    meal.getRestaurants().add(restaurant); // Maintain both sides
                }
            }

            session.merge(meal);
            transaction.commit();
            updateMealDetailsById(meal);

            return "updated";

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return "error";
        } finally {
            if (session != null) session.close();
        }
    }

    public static Meal AddNewMealUpgraded(MealEventUpgraded newMeal) {

        Session session = null;
        Transaction transaction = null;
        try {
            // 1. Initialize Hibernate session
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // 2. Check for duplicate meals
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> mealQuery = builder.createQuery(Meal.class);
            Root<Meal> root = mealQuery.from(Meal.class);
            mealQuery.where(
                    builder.equal(
                            builder.lower(root.get("mealName")),
                            newMeal.getMealName().toLowerCase()
                    )
            );

            List<Meal> existingMeals = session.createQuery(mealQuery).getResultList();
            if (!existingMeals.isEmpty()) {
                return existingMeals.get(0);
            }

            // 3. Create and configure the new meal
            Meal newMealEntity = new Meal();
            newMealEntity.setDiscount_percentage(Double.parseDouble(newMeal.getDiscount()));
            newMealEntity.setName(newMeal.getMealName());
            newMealEntity.setDescription(newMeal.getMealDisc());
            newMealEntity.setPrice(Integer.parseInt(newMeal.getPrice()));
            newMealEntity.setImage(newMeal.getImage());
            newMealEntity.setDelivery(true);
            session.persist(newMealEntity);
            session.flush(); // Ensure ID is generated
            // Add new customizations
            for (String customizationName : newMeal.getCustomizationList()) {
                // Case-insensitive search
                CriteriaBuilder builder3 = session.getCriteriaBuilder();
                CriteriaQuery<Customization> query = builder3.createQuery(Customization.class);
                Root<Customization> root3 = query.from(Customization.class);
                query.where(builder3.equal(builder3.lower(root3.get("customizationName")),
                        customizationName.toLowerCase()));

                Customization customization = session.createQuery(query).uniqueResult();

                if (customization == null) {
                    customization = new Customization();
                    customization.setName(customizationName);
                    session.persist(customization);  // Save new customization
                }

                // Sync both sides of the relationship
                if (!customization.getMeals().contains(newMealEntity)) {
                    customization.getMeals().add(newMealEntity);
                }
                newMealEntity.getCustomizations().add(customization);
            }

            // 4. Process Restaurants
            if ("ALL".equals(newMeal.getBranch().get(0))) // Safe even if branchName is null)
            {
                newMealEntity.setCompany(true);

                // Get all restaurants from the DB
                builder = session.getCriteriaBuilder();
                CriteriaQuery<Restaurant> allRestaurantsQuery = builder.createQuery(Restaurant.class);
                allRestaurantsQuery.from(Restaurant.class);
                List<Restaurant> allRestaurants = session.createQuery(allRestaurantsQuery).getResultList();
                newMealEntity.setRestaurants(allRestaurants);

                for (Restaurant restaurant : allRestaurants) {
                    if (!restaurant.getMeals().contains(newMealEntity)) {
                        restaurant.getMeals().add(newMealEntity);
                        session.update(restaurant);
                    }
                }
            }else {
                newMealEntity.setCompany(false);
                List<String> newBranches = newMeal.getBranch();
                List<Restaurant> newRest = new ArrayList<>();
                // Add new restaurants from the list (skip "ALL" if present)
                for (String branchName : newBranches) {
                    if ("ALL".equalsIgnoreCase(branchName)) continue; // Skip "ALL"

                    CriteriaBuilder builder2 = session.getCriteriaBuilder();
                    CriteriaQuery<Restaurant> query = builder2.createQuery(Restaurant.class);
                    Root<Restaurant> root2 = query.from(Restaurant.class);
                    query.where(builder2.equal(root2.get("restaurantName"), branchName));

                    Restaurant restaurant = session.createQuery(query).uniqueResult();
                    if (restaurant == null) {
                        return null;
                    }
                    newRest.add(restaurant);
                    // Add meal to restaurant (now meal has an ID)
                    if (!restaurant.getMeals().contains(newMealEntity)) {
                        restaurant.getMeals().add(newMealEntity);
                        session.update(restaurant);
                    }
                }
                newMealEntity.setRestaurants(newRest);
            }

            // 5. Save to database
            session.persist(newMealEntity);
            session.merge(newMealEntity);
            transaction.commit();

            newMeal.setId(String.valueOf(newMealEntity.getId()));

            if (!allMeals.contains(newMealEntity)) {
                allMeals.add(newMealEntity);
            }
            for (String branchName : newMeal.getBranch()) {
                if ("ALL".equalsIgnoreCase(branchName))
                {
                    for (Restaurant restaurant : RestMealsList) {
                        List<Meal> mealsInBranch = restaurant.getMeals();
                        if (mealsInBranch == null) {
                            mealsInBranch = new ArrayList<>();
                            restaurant.setMeals(mealsInBranch);
                        }
                        if (!mealsInBranch.contains(newMealEntity)) {
                            mealsInBranch.add(newMealEntity);
                        }
                    }
                    break;
                }
                for (Restaurant restaurant : RestMealsList) {
                    if (restaurant.getRestaurantName().equalsIgnoreCase(branchName)) {
                        List<Meal> mealsInBranch = restaurant.getMeals();
                        if (mealsInBranch == null) {
                            mealsInBranch = new ArrayList<>();
                            restaurant.setMeals(mealsInBranch);
                        }
                        if (!mealsInBranch.contains(newMealEntity)) {
                            mealsInBranch.add(newMealEntity);
                        }
                    }
                }
            }
            meatlist.add(newMealEntity);
            return newMealEntity;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (session != null) session.close();
        }
    }
}
//getAllMeals()
