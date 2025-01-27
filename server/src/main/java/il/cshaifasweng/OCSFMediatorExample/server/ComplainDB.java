package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
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
            newComplain.setTime(time_complain);
            newComplain.setRestaurant(restaurant_complain);

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