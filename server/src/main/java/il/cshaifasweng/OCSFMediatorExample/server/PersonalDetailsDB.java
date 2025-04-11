package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.List;
import org.hibernate.query.Query;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.allPersonalDetails;

public class PersonalDetailsDB {
    private static Session session;

    public static List<PersonalDetails> getAllPersonalDetails() {
        try (Session localSession = getSessionFactory().openSession()) {
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<PersonalDetails> query = builder.createQuery(PersonalDetails.class);
            query.from(PersonalDetails.class);
            return localSession.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean checkPersonalDetailsByEmail(String email) {
        try (Session localSession = getSessionFactory().openSession()) {
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<PersonalDetails> root = query.from(PersonalDetails.class);
            query.select(builder.count(root)).where(builder.equal(root.get("email"), email));
            return localSession.createQuery(query).getSingleResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isPersonalDetailsNew(PersonalDetails pd) throws HibernateException {
        boolean isNew = false;
        try {
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
                System.out.println("Session opened: " + session.isOpen());
            }

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<PersonalDetails> root = query.from(PersonalDetails.class);
            query.select(builder.count(root))
                    .where(
                            builder.equal(root.get("email"), pd.getEmail())
                    );

            System.out.println("Running query...");

            Long count = session.createQuery(query).getSingleResult();
            isNew = (count == 0);
            System.out.println("there is a new Personal details: " + isNew);

        } catch (Exception e) {
            // Log the exception to see if there's an issue
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();  // Print stack trace to help debug the issue
            throw new HibernateException("Failed to check if PersonalDetails is new", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Ensure session is closed in the finally block
                System.out.println("Session closed.");
            }
        }
        return isNew;
    }



    public static PersonalDetails getPersonalDetailsByEmail(String email) throws Exception {

        for(PersonalDetails pd : allPersonalDetails) {
            if(pd.getEmail().equals(email)) {
                return pd;
            }
        }
        return null;
    }



//    public static String addNewPersonalDetails(PersonalDetails newPersonalDetails) {
//        Transaction transaction = null;
//        try (Session localSession = getSessionFactory().openSession()) {
//            transaction = localSession.beginTransaction();
//            localSession.save(newPersonalDetails);
//            transaction.commit();
//            return "New personal details added successfully.";
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            e.printStackTrace();
//            return "Failed to add personal details.";
//        }
//    }


    /*public static boolean hasCreditCardDetails(String email) {
        Transaction transaction = null;
        try (Session localSession = getSessionFactory().openSession()) {
            transaction = localSession.beginTransaction();

            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<PersonalDetails> query = builder.createQuery(PersonalDetails.class);
            Root<PersonalDetails> root = query.from(PersonalDetails.class);
            query.select(root).where(builder.equal(root.get("email"), email));
            PersonalDetails details = localSession.createQuery(query).uniqueResult();

            transaction.commit();

            if (details != null && details.getCreditCardDetails() != null) {
                return true;  // Credit card details exist
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        return false;  // No credit card details found or an error occurred
    }*/
}
