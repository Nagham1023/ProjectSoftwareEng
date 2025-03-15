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

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

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

    public static PersonalDetails getPersonalDetailsByEmail(String email) {
        try (Session localSession = getSessionFactory().openSession()) {
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<PersonalDetails> query = builder.createQuery(PersonalDetails.class);
            Root<PersonalDetails> root = query.from(PersonalDetails.class);
            query.select(root).where(builder.equal(root.get("email"), email));
            return localSession.createQuery(query).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
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

    // Add personal details to the database
    public static void addPersonalDetails(PersonalDetails personalDetails) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        session.beginTransaction();
        try {
            session.save(personalDetails);
            session.flush();
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new Exception("An error occurred while generating orders.", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

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
