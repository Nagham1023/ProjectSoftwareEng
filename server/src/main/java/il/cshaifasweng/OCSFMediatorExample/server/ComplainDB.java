package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class ComplainDB {

    private static Session session;
    public static List<Complain> complainslist;

    public static void generateData() throws Exception {
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

            // Create Complains
            Complain complain1 = new Complain();
            complain1.setKind("feed");
            complain1.setName("Hala Saloty");
            complain1.setEmail("7ala.saloty@gmail.com");
            complain1.setDate(Date.valueOf("23.12.2002"));
            complain1.setTime(new Time(System.currentTimeMillis()));
            complain1.setStatus_complaint("needReplay");
            //complain1.setRestaurant(restaurants.);

            Complain complain2 = new Complain();
            complain2.setKind("comp");
            complain2.setName("Hala");
            complain2.setEmail("7ala.saloty@gmail.com");
            complain2.setDate(Date.valueOf("30.12.2002"));
            complain2.setTime(new Time(System.currentTimeMillis()));
            complain2.setStatus_complaint("done");
            //complain2.setRestaurant(restaurants.);

            Complain complain3 = new Complain();
            complain1.setKind("sugg");
            complain1.setName("Saloty");
            complain1.setEmail("7ala.saloty@gmail.com");
            complain1.setDate(Date.valueOf("31.12.2002"));
            complain1.setTime(new Time(System.currentTimeMillis()));
            complain1.setStatus_complaint("done");
            //complain1.setRestaurant(restaurants.);


            // List of meals to add
            List<Complain> newComplains = Arrays.asList(complain1, complain2, complain3);

            // Fetch existing complains from the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complain> query = builder.createQuery(Complain.class);
            query.from(Complain.class);
            List<Complain> existingMeals = session.createQuery(query).getResultList();

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

    public static List<Complain> getAllComplains() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Complain> query = builder.createQuery(Complain.class);
        query.from(Meal.class);
        List<Complain> complains = session.createQuery(query).getResultList();
        complainslist = complains;
        return complainslist;
    }
    public static void addCompToList(Complain newComp) {
        // Ensure meatlist is initialized
        if (complainslist == null) {
            complainslist = new ArrayList<>();
        }
        complainslist.add(newComp);}

    public static String addComplainIntoDatabase(complainEvent newComplain) {
        // Extract data from the mealEvent object
        String kind_complain = newComplain.getKind();
        String name_complain = newComplain.getName();
        String email_complain = newComplain.getEmail();
        String tell_complain = newComplain.getTell();
        Date date_complain = newComplain.getDate();
        Time time_complain = newComplain.getTime();
        Restaurant restaurant_complain = newComplain.getRestaurant();
        String status_complain = newComplain.getStatus_complain();

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
            newComp.setStatus_complaint(status_complain);


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