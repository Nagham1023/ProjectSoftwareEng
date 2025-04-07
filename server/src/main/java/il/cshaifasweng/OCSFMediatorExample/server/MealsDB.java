package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class MealsDB {

    private static Session session;
    public static List<Meal> meatlist;
    public static List<Customization> customizationsList;

    static byte[] loadImage(String fileName) throws IOException {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream("images/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }
            return inputStream.readAllBytes();
        }
    }

    public static void generateData() throws Exception {
        customizationsList= new ArrayList<>();
        Transaction transaction = null;
        try {
            if (session == null || !session.isOpen()) {
                try {
                    SessionFactory sessionFactory = getSessionFactory();
                    session = sessionFactory.openSession();
                } catch (HibernateException e) {
                    System.err.println("Error initializing Hibernate session: " + e.getMessage());
                    return;
                }
            }

            transaction = session.beginTransaction();

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

            // List of customizations to check and add if not existing
            List<Customization> customizations = Arrays.asList(moreLettuce, extraCheese, moreOnion, highSpicyLevel, blackBread);

            // Check if customizations already exist in the database and save only new ones
            for (Customization customization : customizations) {
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Customization> query = builder.createQuery(Customization.class);
                query.from(Customization.class);

                List<Customization> existingCustomizations = session.createQuery(query).getResultList();
                boolean isDuplicate = existingCustomizations.stream()
                        .anyMatch(existingCustomization -> existingCustomization.getName().equalsIgnoreCase(customization.getName()));

                if (!isDuplicate) {
                    session.save(customization);
                    customizationsList.add(customization);
                    System.out.println("Added new customization: " + customization.getName());
                } else {
                    System.out.println("Duplicate customization skipped: " + customization.getName());
                }
            }

            // Create Meals
            Meal burger = new Meal();
            burger.setName("Burger");
            burger.setDescription("Juicy beef burger with fresh lettuce");
            burger.setPrice(8.00);
            burger.setCustomizations(Arrays.asList(moreLettuce));
            burger.setImage(loadImage("burger.jpg"));

            Meal spaghetti = new Meal();
            spaghetti.setName("Spaghetti");
            spaghetti.setDescription("Classic Italian spaghetti with cheese");
            spaghetti.setPrice(10.00);
            spaghetti.setCustomizations(Arrays.asList(extraCheese));
            spaghetti.setImage(loadImage("spaghetti.jpg"));

            Meal avocadoSalad = new Meal();
            avocadoSalad.setName("Avocado Salad");
            avocadoSalad.setDescription("Fresh avocado salad with onions");
            avocadoSalad.setPrice(7.00);
            avocadoSalad.setCustomizations(Arrays.asList(moreOnion));
            avocadoSalad.setImage(loadImage("avocado_salad.png"));

            Meal grills = new Meal();
            grills.setName("Grills");
            grills.setDescription("Mixed grilled meats with spices");
            grills.setPrice(12.00);
            grills.setCustomizations(Arrays.asList(highSpicyLevel));
            grills.setImage(loadImage("grills.jpg"));

            Meal toastCheese = new Meal();
            toastCheese.setName("Toast Cheese");
            toastCheese.setDescription("Cheese toast with black bread");
            toastCheese.setPrice(5.00);
            toastCheese.setCustomizations(Arrays.asList(blackBread));
            toastCheese.setImage(loadImage("toast_cheese.jpg"));

            // List of meals to add
            List<Meal> newMeals = Arrays.asList(burger, spaghetti, avocadoSalad, grills, toastCheese);

            // Fetch existing meals from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
            query.from(Meal.class);
            List<Meal> existingMeals = session.createQuery(query).getResultList();


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
            transaction.commit();
            System.out.println("Transaction committed successfully.");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
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
                        existing.getNewPrice()
                );
                return result;

            }

            // Create a new request and link it to the Meal
            UpdatePriceRequest updateRequest = new UpdatePriceRequest();
            updateRequest.setMeal(meal); // Link the Meal object
            updateRequest.setOldPrice(meal.getPrice());
            updateRequest.setNewPrice(newPrice);

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
                    newPrice
            );
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
                session.delete(req);
            }

            session.getTransaction().commit();
            System.out.println("Deleted " + requests.size() + " request(s) for meal ID: " + mealId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String deleteMeal(int mealId) {
        System.out.println("now I am in the function");
        String mealName = null;
        Transaction tx = null;

        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            Meal meal = session.get(Meal.class, mealId);
            if (meal != null) {
                // First, remove associations from restaurants
                for (Restaurant restaurant : meal.getRestaurants()) {
                    restaurant.getMeals().remove(meal);
                    session.update(restaurant); // Save the updated restaurant
                }
            }
            if (meal != null) {
                session.delete(meal); // Will cascade to delete requests
            }

            session.getTransaction().commit();
            System.out.println("Meal and associated requests deleted");
            mealName = meal.getName();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
                return "sorry didn't successfully delete meal";
            }
            e.printStackTrace();
        }
        return mealName;
    }
    public static Meal getMealById(String mealId) {
        System.out.println("now I am in the function getMealById");
        Meal meal = null;
        int id=Integer.parseInt(mealId);
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            meal = session.get(Meal.class,id);
            if (meal != null) {
                session.update(meal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meal;
    }

    public static void updateMealDetailsById(Meal mealS) {
        // Check if the meatlist is initialized
        if (meatlist == null || meatlist.isEmpty()) {
            System.out.println("The meal list is empty or not initialized.");
            return;
        }

        // Search for the meal with the given ID
        for (Meal meal : meatlist) {
            if (meal.getId() == mealS.getId()) {
                meal=mealS;
                return; // Exit the loop after updating
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
                meal.getRestaurants().clear();
            } else {
                meal.setCompany(false);
                List<String> newBranches = msg.getNewBranches();

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

                    // Add meal to the restaurant (owning side)
                    if (!restaurant.getMeals().contains(meal)) {
                        restaurant.getMeals().add(meal);
                        session.merge(restaurant);
                    }
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
            } else {
                newMealEntity.setCompany(false);
                List<String> newBranches = newMeal.getBranch();

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
                    // Add meal to restaurant (now meal has an ID)
                    if (!restaurant.getMeals().contains(newMealEntity)) {
                        restaurant.getMeals().add(newMealEntity);
                        session.update(restaurant);
                    }
                }
            }

            // 5. Save to database
            session.persist(newMealEntity);
            session.merge(newMealEntity);
            transaction.commit();

            newMeal.setId(String.valueOf(newMealEntity.getId()));
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
