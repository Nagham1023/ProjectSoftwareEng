package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            String queryString = "FROM Complain ORDER BY time_complain DESC";
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
        System.out.println("updating Complain Response In Database");
        int Idcomp = updateResponse.getIdComplain();
        String newRes = updateResponse.getnewResponse();
        String emailComp = updateResponse.getEmailComplain();
        String orderNum = updateResponse.getOrderNumber();
        double refund = updateResponse.getRefundAmount();

        try {
            if (session == null || !session.isOpen()) {
                System.out.println("Session is not initialized. Creating a new session.");
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
            Complain complain = session.get(Complain.class, Idcomp);
            if (complain != null) {
                //System.out.println("Found Meal: " + meal.getName() + " with current price: " + meal.getPrice());
                complain.setResponse(newRes); // Update the response
                complain.setStatus("Done");
                complain.setRefund(refund);
                session.update(complain); // Persist the changes
                System.out.println("updated Complain Response In Database after setting response");
                session.getTransaction().commit(); // Commit the transaction
                updateCompResponseById(Idcomp, newRes);
                sendResToEmail(emailComp, newRes);
                giveBackTheRefund(orderNum,refund);
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

    public static void  giveBackTheRefund(String orderNum,double refund) {
        // Check if the meatlist is initialized
        if (orderNum == null || refund == 0) {
            System.out.println("no need to give back the refund");
            return;
        }
        else {

            // neeeeeeeeeeeeed tooooooooooooooo dooooooooooooooooo
            // send back the refund to the card that paied on the order with this order num

        }


    }

    public static void sendResToEmail(String emailComp, String newRes) {
        // Check if the meatlist is initialized
        if (emailComp == null || newRes == null) {
            System.out.println("Something empty or not initialized.");
            return;
        }

        // neeeeeeeeeeeeed tooooooooooooooo dooooooooooooooooo
        // send new res to the email emailComp

    }

    public static void updateCompResponseById(int Idcomp, String newRes) {
        // Check if the meatlist is initialized
        if (complainslist == null || complainslist.isEmpty()) {
            System.out.println("Something is empty or not initialized.");
            return;
        }
        // Search for the comp with the given ID
        for (Complain complain : complainslist) {
            if (complain.getId() == Idcomp) {
                complain.setResponse(newRes);
                complain.setStatus("Done");
                System.out.println("Complain ID " + Idcomp + " the response: " + complain.getResponse());
                return; // Exit the loop after updating
            }
        }
    }

    public static String addComplainIntoDatabase(complainEvent newComplain) {
        Session session = null;
        Transaction transaction = null;

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Complain newComp = new Complain();
            // Set all fields EXCEPT ID
            newComp.setKind(newComplain.getKind());
            newComp.setName(newComplain.getName());
            newComp.setEmail(newComplain.getEmail());
            newComp.setTell(newComplain.getTell());
            newComp.setDate(newComplain.getDate());
            newComp.setTime(newComplain.getTime());
            newComp.setStatus(newComplain.getStatus());
            newComp.setResponse(newComplain.getResponse());
            newComp.setOrderNum(newComplain.getOrderNum());
            newComp.setRefund(newComplain.getRefund());
            newComp.setRestaurant(newComplain.getRestaurant());

            session.save(newComp);
            transaction.commit();

            System.out.println("New Comp added successfully with ID: " + newComp.getId());
            return "added";

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return "error";
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }



}