package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class ComplainDB {

    private static Session session;
    public static List<Complain> complainslist;

    public static List<Complain> getAllComplains() {
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

        List<Complain> result = new ArrayList<>();
        try {
            // Begin the transaction for querying
            session.beginTransaction();

            // Create a query to find all meals without any constraint
            String queryString = "FROM Complain";  // No WHERE clause, fetch all meals
            org.hibernate.query.Query<Complain> query = session.createQuery(queryString, Complain.class);

            // Execute the query and get the result list
            result = query.getResultList();
            complainslist = result;

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
    public static void addCompToList(Complain newComp) {
        // Ensure complainslist is initialized
        if (complainslist == null) {
            complainslist = new ArrayList<>();
        }
        complainslist.add(newComp);}

    public static void updateComplainResponseInDatabase(updateResponse updateResponse) {
        //System.out.println("Changing the price in database.");
        int Idcomp = updateResponse.getIdComplain();
        String newRes = updateResponse.getnewResponse();

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
            complainEvent complain = session.get(complainEvent.class, Idcomp);
            if (complain != null) {
                //System.out.println("Found Meal: " + meal.getName() + " with current price: " + meal.getPrice());
                complain.setResponse(newRes); // Update the price
                session.update(complain); // Persist the changes
                session.getTransaction().commit(); // Commit the transaction
                updateCompResponseById(Idcomp, newRes);
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
    }

    public static void updateCompResponseById(int Idcomp, String newRes) {
        // Check if the meatlist is initialized
        if (complainslist == null || complainslist.isEmpty()) {
            System.out.println("The meal list is empty or not initialized.");
            return;
        }
        // Search for the comp with the given ID
        for (Complain complain : complainslist) {
            if (complain.getId() == Idcomp) {
                complain.setResponse(newRes);
                System.out.println("Complain ID " + Idcomp + " the response: " + complain.getResponse());
                return; // Exit the loop after updating
            }
        }

    }

    public static String addComplainIntoDatabase(complainEvent newComplain) {
        // Extract data from the mealEvent object
        String kind_complain = newComplain.getKind();
        String name_complain = newComplain.getName();
        String email_complain = newComplain.getEmail();
        String tell_complain = newComplain.getTell();
        Date date_complain = newComplain.getDate();
        Time time_complain = newComplain.getTime();
        String status_complain = newComplain.getStatus();
        String response_complaint = newComplain.getResponse();
        Restaurant restaurant_complain = newComplain.getRestaurant();

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

            // Create a new complain entity and set its attributes
            Complain newComp = new Complain();
            newComp.setKind(kind_complain);
            newComp.setName(name_complain);
            newComp.setEmail(email_complain);
            newComp.setTell(tell_complain);
            newComp.setDate(date_complain);
            newComp.setTime(time_complain);
            newComp.setRestaurant(restaurant_complain);
            newComp.setStatus(status_complain);
            newComp.setResponse(response_complaint);


            // Save the complain to the database
            session.save(newComp);
            // Add the complain to the local list
            addCompToList(newComp);

            System.out.println("New Comp added");
            newComp.setId(newComplain.getId());

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



}