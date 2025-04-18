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
import java.time.Month;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.addCreditCardDetails;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.getAllComplains;
import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.getAllCreditCards;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.OrdersDB.*;
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
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            if (getAllRestaurants(session) != null && !getAllRestaurants(session).isEmpty()) {
                Restaurant nazareth = getAllRestaurants(session).get(0);
                Restaurant haifa = getAllRestaurants(session).get(1);
                Restaurant telAviv = getAllRestaurants(session).get(2);
                Restaurant Sakhnin = getAllRestaurants(session).get(3);
                List<Meal> meals = getAllMeals();
                int i = 0;
                for (Meal meal : meals) {
                    if (meal.getRestaurants() == null || meal.getRestaurants().isEmpty()) {
                        if (i % 2 == 0) {
                            meal.getRestaurants().add(nazareth);
                            meal.getRestaurants().add(Sakhnin);
                            nazareth.getMeals().add(meal);
                            Sakhnin.getMeals().add(meal);
                        }
                        if (i % 3 == 0) {
                            meal.getRestaurants().add(haifa);
                            haifa.getMeals().add(meal);
                        }
                        meal.getRestaurants().add(telAviv);
                        telAviv.getMeals().add(meal);
                        i++;
                    }
                }
                session.update(nazareth);
                session.update(haifa);
                session.update(telAviv);
                session.update(Sakhnin);

                session.flush();
                session.getTransaction().commit();
                System.out.println("there are restaurants in the database");
                return;
            } else
                System.out.println("no restaurants in the database");

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
                    meal.getRestaurants().add(nazareth);
                    meal.getRestaurants().add(Sakhnin);
                    nazarethMeals.add(meal);
                    sakhninMeals.add(meal);
                }
                if (i % 3 == 0) {
                    meal.getRestaurants().add(haifa);
                    haifaMeals.add(meal);
                }
                meal.getRestaurants().add(telAviv);
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
                complain.setResponse(status.equals("Done") ? "We have taken care of it." : "takeeen care");
                complain.setOrderNum(kind.equals("Feedback") || kind.equals("Suggestion") ? "" : String.valueOf(random.nextInt(100) + 1));
                complain.setRefund(kind.equals("Complaint") && status.equals("Done") ? random.nextInt(21) : 0);
                complain.setRestaurant(RestMealsList.get(random.nextInt(RestMealsList.size())));

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


    private static void generatingAllData(){


        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction(); // Start the transaction


            /*deleteAllTablesAndRelatedData()*/
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


            System.out.println("deleteAllTablesAndRelatedData");

            /*generateData() from MealsDB*/
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

            System.out.println("generateData() finished");

            /*getAllPersonalDetails()*/
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<PersonalDetails> query = builder.createQuery(PersonalDetails.class);
            query.from(PersonalDetails.class);
            allPersonalDetails = session.createQuery(query).getResultList();

            System.out.println("finished getAllPersonalDetails()");

            /*getAllCreditCards()*/
            CriteriaBuilder builder2 = session.getCriteriaBuilder();
            CriteriaQuery<CreditCard> query2 = builder2.createQuery(CreditCard.class);
            query2.from(CreditCard.class);
            allCreditCards = session.createQuery(query2).getResultList();

            System.out.println("finished getAllCreditCards()");

            /*getUsers()*/

            CriteriaBuilder builder3 = session.getCriteriaBuilder();
            CriteriaQuery<Users> query3 = builder3.createQuery(Users.class);
            query3.from(Users.class);
            allUsers = session.createQuery(query3).getResultList();

            System.out.println("finished getAllUsers()");

            /*generateBasicUser1()*/

            Users nagham = new Users();
            nagham.setRole("CompanyManager");
            nagham.setEmail("naghammnsor@gmail.com");
            nagham.setPassword("NaghamYes");
            nagham.setUsername("naghamTheManager");
            nagham.setGender("other");
            nagham.setAge(22);
            Users salha = new Users();
            salha.setRole("CustomerService");
            salha.setEmail("salhasalha121314@gmail.com");
            salha.setPassword("salha121314");
            salha.setUsername("salhaTheCustomerService");
            salha.setGender("female");
            salha.setAge(55);
            Users knani = new Users();
            knani.setRole("CustomerService");
            knani.setEmail("yousefknani9@gmail.com");
            knani.setPassword("123");
            knani.setUsername("yousef");
            knani.setGender("male");
            knani.setAge(32);
            Users yousef = new Users();
            yousef.setRole("CompanyManager");
            yousef.setEmail("yousefknani9@gmail.com");
            yousef.setPassword("212");
            yousef.setUsername("ceo");
            yousef.setGender("male");
            yousef.setAge(24);
            Users shada = new Users();
            shada.setRole("Dietation");
            shada.setEmail("shadamazzawi@gmail.com");
            shada.setPassword("123");
            shada.setUsername("shada");
            shada.setGender("other");
            shada.setAge(22);

            Users Lamis = new Users();
            Lamis.setRole("Host Nazareth");
            Lamis.setEmail("shadamazzawi@gmail.com");
            Lamis.setPassword("123");
            Lamis.setUsername("lamis");
            Lamis.setGender("other");
            Lamis.setAge(22);

            Users adan = new Users();
            adan.setRole("ChainManager Nazareth");
            adan.setEmail("shadamazzawi@gmail.com");
            adan.setPassword("123");
            adan.setUsername("adan");
            adan.setGender("other");
            adan.setAge(22);
            Users yos = new Users();
            yos.setRole("Kitchen Nazareth");
            yos.setEmail("yousefknani9@gmail.com");
            yos.setPassword("123");
            yos.setUsername("kitchen");
            yos.setGender("male");
            yos.setAge(22);

            if (!isUserExists("naghamTheManager") ) {
                allUsers.add(nagham);
                session.save(nagham);
                System.out.println("User has been created: " + nagham.getUsername());
            }

            // Ensure you are checking for existing user
            if (!isUserExists("salhaTheCustomerService")) {
                allUsers.add(salha);
                // Save the new user to the database
                session.save(salha);
                System.out.println("User has been created: " + salha.getUsername());
            }

            // Create orders
            if (!isUserExists("ceo") ) {
                allUsers.add(yousef);
                session.save(yousef);
                System.out.println("User has been created: " + yousef.getUsername());
            }
            if (!isUserExists("shada") ) {
                allUsers.add(shada);
                session.save(shada);
                System.out.println("User has been created: " + shada.getUsername());
            }
            if (!isUserExists("yousef") ) {
                allUsers.add(knani);
                session.save(knani);
                System.out.println("User has been created: " + knani.getUsername());
            }
            if (!isUserExists("adan") ) {
                allUsers.add(adan);
                session.save(adan);
                System.out.println("User has been created: " + adan.getUsername());
            }
            if (!isUserExists("lamis") ) {
                allUsers.add(Lamis);
                session.save(Lamis);
                System.out.println("User has been created: " + Lamis.getUsername());
            }
            if (!isUserExists("kitchen") ) {
                allUsers.add(yos);
                session.save(yos);
                System.out.println("User has been created: " + yos.getUsername());
            }

            session.flush();
            session.clear();

            System.out.println("finished allUsers()");

            /*generateRestaurants()*/
//            List<Restaurant> tempRest = getAllRestaurants(session);
            List<Restaurant> tempRest;
            /*tempRest = getAllRetaurants()*/
            List<Restaurant> result = new ArrayList<>();


            String queryString = "FROM Restaurant";
            org.hibernate.query.Query<Restaurant> query12 = session.createQuery(queryString, Restaurant.class);
            result = query12.getResultList();
            tempRest = result;

            if (tempRest != null && !tempRest.isEmpty()) {
                Restaurant nazareth = tempRest.get(0);
                Restaurant haifa = tempRest.get(1);
                Restaurant telAviv = tempRest.get(2);
                Restaurant Sakhnin = tempRest.get(3);


                /*getAllMeals()*/

                //List<Meal> tempMeals = getAllMeals();
                List<Meal> tempMeals;
                if(SimpleServer.allMeals != null) {
                    tempMeals = SimpleServer.allMeals;
                }
                else {
                    CriteriaBuilder builder11 = session.getCriteriaBuilder();
                    CriteriaQuery<Meal> query11 = builder11.createQuery(Meal.class);
                    query11.from(Meal.class);
                    List<Meal> meals = session.createQuery(query11).getResultList();
                    meatlist = meals;
                    allMeals = meals;

                    for (Meal meal : meals) {
                        Hibernate.initialize(meal.getCustomizations());
                    }
                    tempMeals = meals;
                }

                int i = 0;
                for (Meal meal : tempMeals) {
                    if (meal.getRestaurants() == null || meal.getRestaurants().isEmpty()) {
                        if (i % 2 == 0) {
                            meal.getRestaurants().add(nazareth);
                            meal.getRestaurants().add(Sakhnin);
                            nazareth.getMeals().add(meal);
                            Sakhnin.getMeals().add(meal);
                        }
                        if (i % 3 == 0) {
                            meal.getRestaurants().add(haifa);
                            haifa.getMeals().add(meal);
                        }
                        meal.getRestaurants().add(telAviv);
                        telAviv.getMeals().add(meal);
                        i++;
                    }
                }
                session.update(nazareth);
                session.update(haifa);
                session.update(telAviv);
                session.update(Sakhnin);

                session.flush();
            } else {
                System.out.println("no restaurants in the database");

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

                Restaurant Sakhnin = new Restaurant();
                Sakhnin.setRestaurantName("SAKHNIN");
                Sakhnin.setImagePath("sakhnin.jpg");
                Sakhnin.setPhoneNumber("345-678-9012");
                Sakhnin.setOpeningTime(openingTime);
                Sakhnin.setClosingTime(closingTime);

                //List<Meal> meals = getAllMeals();
                List<Meal> meals ;
                /*getAllMeals()*/
                if(SimpleServer.allMeals != null) {
                    meals = SimpleServer.allMeals;
                }
                else {
                    List<Meal> tempMeals;
                    CriteriaBuilder builder14 = session.getCriteriaBuilder();
                    CriteriaQuery<Meal> query14 = builder14.createQuery(Meal.class);
                    query14.from(Meal.class);
                    tempMeals = session.createQuery(query14).getResultList();
                    meatlist = tempMeals;
                    allMeals = tempMeals;

                    for (Meal meal : tempMeals) {
                        Hibernate.initialize(meal.getCustomizations());
                    }
                    meals = tempMeals;
                }


                int i = 0;
                List<Meal> nazarethMeals = new ArrayList<>();
                List<Meal> haifaMeals = new ArrayList<>();
                List<Meal> telavivMeals = new ArrayList<>();
                List<Meal> sakhninMeals = new ArrayList<>();
                for (Meal meal : meals) {
                    if (i % 2 == 0) {
                        meal.getRestaurants().add(nazareth);
                        meal.getRestaurants().add(Sakhnin);
                        nazarethMeals.add(meal);
                        sakhninMeals.add(meal);
                    }
                    if (i % 3 == 0) {
                        meal.getRestaurants().add(haifa);
                        haifaMeals.add(meal);
                    }
                    meal.getRestaurants().add(telAviv);
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
            }
            System.out.println("finished generateRestaurants()");


            /*getAllRestMeals*/

            List<Restaurant> restaurants = session.createQuery("FROM Restaurant", Restaurant.class).list();
            List<Meal> newwwMeals = session.createQuery("FROM Meal", Meal.class).list();


            SimpleServer.allMeals = newwwMeals;
            RestMealsList = restaurants;
            for (Restaurant restaurant : restaurants) {
                System.out.println("Restaurant: " + restaurant.getRestaurantName());
                System.out.println("Meals:");

                for (Meal meal : restaurant.getMeals()) {
                    System.out.println("  - " + meal.getName() + ": " + meal.getDescription() + " ($" + meal.getPrice() + ")");
                }

                System.out.println("------------------------------------------------");


            }
            System.out.println("finished getAllRestMeals()");


            /*getAllCustomizations()*/
            if(allcust == null)
            {
                Query<Customization> query4 = session.createQuery("FROM Customization", Customization.class);
                List<Customization> customizations = query4.getResultList();

                allcust = new ArrayList<>(customizations);
                System.out.println("printing all customizations");
                System.out.println(allcust);

            }
            System.out.println("finished getallCustomizations()");

            /*getAllComplains()*/

            List<Complain> result2;
            String queryString2 = "FROM Complain ORDER BY time_complain DESC";
            org.hibernate.query.Query<Complain> query5 = session.createQuery(queryString2, Complain.class);
            // Execute the query and get the result list
            result2 = query5.getResultList();
            complainslist = result2;
            allComplains = result2;

            System.out.println("finished getallComplains()");

            /*generateTheComplains()*/

            if (allComplains == null || allComplains.isEmpty()) {
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

                for (int j = 1; j <= 60; j++) {
                    Complain complain = new Complain();
                    String kind = types[random.nextInt(types.length)];
                    String status = statuses[random.nextInt(statuses.length)];
                    String name = "User" + j;
                    String email = "user" + j + "@example.com";
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
                    complain.setResponse(status.equals("Done") ? "We have taken care of it." : "takeeen care");
                    complain.setOrderNum(kind.equals("Feedback") || kind.equals("Suggestion") ? "" : String.valueOf(random.nextInt(100) + 1));
                    complain.setRefund(kind.equals("Complaint") && status.equals("Done") ? random.nextInt(21) : 0);
                    complain.setRestaurant(RestMealsList.get(random.nextInt(RestMealsList.size())));

                    session.save(complain);
                    allComplains.add(complain);
                }

                session.flush();
            }

            System.out.println("finished generateTheComplains()");

            /*initializeSampleTables()*/
            List<Integer> capacities = Arrays.asList(2, 3, 4);
            Random random = new Random();

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
            }
            else {
                // Step 1: Fetch all restaurants
                Query<Restaurant> query6 = session.createQuery("FROM Restaurant", Restaurant.class);
                List<Restaurant> restaurantss = query6.getResultList();
                System.out.println("The restaurants that will have tables are: " + restaurants);

                // Step 2: Create sample tables for each restaurant
                for (Restaurant restaurant : restaurantss) {
                    List<TableNode> tables = new ArrayList<>();
                    for (int k = 0; k < 15; k++) { // Add 15 tables for each restaurant
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
            }


            System.out.println("finished initializeSampleTables()");
            /*fetching_reservation()*/
            allSavedReservation = session.createQuery("FROM ReservationSave", ReservationSave.class).list();

            // Initialize the tables collection for each ReservationSave object
            for (ReservationSave reservation : allSavedReservation) {
                Hibernate.initialize(reservation.getTables()); // Initialize the lazy-loaded tables collection
            }

            System.out.println("finished fetching_reservations()");


            /*generateOrders()*/
            CriteriaBuilder builder7 = session.getCriteriaBuilder();
            CriteriaQuery<Order> orderQuery = builder7.createQuery(Order.class);
            Root<Order> orderRoot = orderQuery.from(Order.class);  // This was missing
            if (session.createQuery(orderQuery).getResultList().isEmpty()) {


                // Get restaurants with proper CriteriaQuery
                CriteriaQuery<Restaurant> restaurantQuery = builder.createQuery(Restaurant.class);
                Root<Restaurant> restaurantRoot = restaurantQuery.from(Restaurant.class);
                List<Restaurant> restaurants3 = session.createQuery(restaurantQuery).getResultList();

                // Get meals with proper CriteriaQuery
                CriteriaQuery<Meal> mealQuery = builder.createQuery(Meal.class);
                Root<Meal> mealRoot = mealQuery.from(Meal.class);
                List<Meal> meals3 = session.createQuery(mealQuery).getResultList();

                if (!restaurants3.isEmpty() && !meals3.isEmpty()) {
                    List<Order> orders = new ArrayList<>();
                    int currentYear = 2025;

                    // Generate orders for months
                    orders.addAll(createOrdersForMonth(currentYear, Month.JANUARY, 20, restaurants, meals3));
                    orders.addAll(createOrdersForMonth(currentYear, Month.FEBRUARY, 22, restaurants, meals3));
                    orders.addAll(createOrdersForMonth(currentYear, Month.MARCH, 18, restaurants, meals3));
                    orders.addAll(createOrdersForMonth(currentYear, Month.APRIL, 50, restaurants, meals3));

                    // Persist orders
                    for (Order order : orders) {
                        session.persist(order);
                    }

                    session.flush();
                    generateComplaints(session, orders);
                }
            }
            System.out.println("finished generateOrders()");

            /*getOrders()*/
            List<Order> orders;
            CriteriaBuilder builder8 = session.getCriteriaBuilder();
            CriteriaQuery<Order> query8 = builder8.createQuery(Order.class);
            query8.from(Order.class);
            orders = session.createQuery(query8).getResultList();
            allOrders= orders;

            System.out.println("finished getOrders()");
            /*getAllRequestsWithMealDetails()*/

            Query<UpdatePriceRequest> query9 = session.createQuery(
                    "SELECT r FROM UpdatePriceRequest r JOIN FETCH r.meal",
                    UpdatePriceRequest.class
            );

            List<UpdatePriceRequest> requests = query9.getResultList();

            // Convert to MealUpdateRequest DTOs
            List<MealUpdateRequest> dtos = new ArrayList<>();
            for (UpdatePriceRequest request : requests) {
                Meal meal = request.getMeal();
                dtos.add(new MealUpdateRequest(
                        String.valueOf(meal.getId()) ,
                        meal.getName(),
                        meal.getDescription(),
                        meal.getImage(),
                        request.getOldPrice(),
                        request.getNewPrice(),
                        request.getOldDiscount(),
                        request.getNewDiscount()
                ));
            }

            allUpdateRequests = dtos;

            System.out.println("finished getAllRequests()");










            session.getTransaction().commit(); // Commit the transaction

            //rollback..
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., rollback transaction if needed)
        }
    }

    private static SimpleServer server;

    public static void main(String[] args) throws IOException {
        try {
            server = new SimpleServer(3000);
            server.listen();
            generatingAllData();


            //deleteAllTablesAndRelatedData();
            //generateData();
            //printAllData();
            //allPersonalDetails = getAllPersonalDetails();
            //allCreditCards = getAllCreditCards();
            //allUsers = getUsers();
            //generateBasicUser1();
            //generateRestaurants();
            //generateCompanyMeals();
            //getAllRestMeals();
            //System.out.println("getting all custom");
            //allcust = getAllCustomizations();

            //allComplains = getAllComplains();
            //generateTheComplains();
            //initializeSampleTables();
            //fetching_reservation();
            //generateOrders();
            //allOrders = getOrders();
            //allUpdateRequests = getAllRequestsWithMealDetails();


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
    private static boolean isUserExists(String username) {

        for (Users user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
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