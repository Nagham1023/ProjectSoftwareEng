package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
public class ComplainDB {
    private static Session session;
    public static List<Complain> complainslist;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

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
        complainslist.add(newComp);
    }

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
                /*updateCompResponseById(Idcomp, newRes);
                sendResToEmail(emailComp, newRes);
                giveBackTheRefund(orderNum,refund);*/
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

    public static String addComplainIntoDatabase(complainEvent newComplain, ConnectionToClient client) {
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
            complaintAutoResponse(newComp,client);
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
    public static void complaintAutoResponse(Complain complaint,ConnectionToClient client) {
        // Calculate 24 hours in milliseconds
        long dayToFinish = TimeUnit.HOURS.toMillis(24);
        // Schedule the autoRespondToComplaint method to run after the delay
        scheduler.schedule(() -> autoRespondToComplaint(complaint, client), dayToFinish, TimeUnit.MILLISECONDS);
    }

    private static void autoRespondToComplaint(Complain complaint, ConnectionToClient client) {
        System.out.println("SchedulerService DEBUG: Auto-responding to complaint: " + complaint.getId());
        autoRespond(complaint, client);
    }

    private static void autoRespond(Complain complaint, ConnectionToClient client) {
        Session session = null;
        Transaction transaction = null;
        int complaintId = complaint.getId();
        String response = "We regret that we couldn't resolve your complaint within 24 hours. \" +\n" +
                "                \"A full refund of 75 NIS has been issued. We apologize for the inconvenience.";
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            String hql = "FROM Complain c WHERE c.id = :id " +
                    "AND c.kind_complain = 'Complaint' " +
                    "AND c.status_complaint = 'Do'";
            Query<Complain> query = session.createQuery(hql, Complain.class);
            query.setParameter("id", complaintId);
            Complain complain = query.uniqueResult();

            session.getTransaction().commit();

            if (complain != null && complain.getStatus().equals("Do")) {
                updateResponse ur=new updateResponse(response, complaintId,complaint.getEmail(),complaint.getOrderNum(),75);
                updateComplainResponseInDatabase(ur);
                client.sendToClient(ur);

            } else if(complaint==null) {
                System.out.println("AutoRespond: Complaint not found for ID: " + complaintId);
            } else {
                System.out.println("complaintAutoRespond: Complaint for ID: " + complaintId+ "is Done");
            }
            transaction.commit();
        } catch (Exception e) {
            System.err.println("complaintAutoRespond: Error responding to complaint: " + e.getMessage());
            e.printStackTrace();
        }
    }
}