package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.CreditCard;
import il.cshaifasweng.OCSFMediatorExample.entities.CreditCardCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.PersonalDetails;
import il.cshaifasweng.OCSFMediatorExample.entities.Users;
import org.hibernate.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class CreditCardDetailsDB {
    private static Session session;

    public static void addCreditCardDetails(CreditCard newCardDetails, String personalEmail) {

        try {
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            session.beginTransaction();

            PersonalDetails personalDetails = (PersonalDetails) session.createQuery("FROM PersonalDetails WHERE email = :email")
                    .setParameter("email", personalEmail)
                    .uniqueResult();

            if (personalDetails != null) {
                newCardDetails.setPersonalDetails(personalDetails);
                session.save(newCardDetails);
                System.out.println("Credit card details added for: " + personalEmail);
                session.getTransaction().commit();
            } else {
                System.out.println("No personal details found with email: " + personalEmail);
                session.getTransaction().rollback();  // Explicit rollback if no personal details are found
            }
        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to add credit card details", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static boolean isCreditCardNew(CreditCardCheck credit) throws HibernateException {
        boolean isNew = false;
        try {
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<CreditCard> root = query.from(CreditCard.class);
            query.select(builder.count(root))
                    .where(
                            builder.and(
                                    builder.equal(root.get("cvv"), credit.getCvv())
                                    //builder.equal(root.get("cardNumber"), credit.getCardNumber()),
                                    //builder.equal(root.get("cardholderName"), credit.getCardholderName()),
                                    //builder.equal(root.get("cardholdersID"), credit.getCardholdersID()),
                                    //builder.equal(root.get("expiryDate"), credit.getExpiryDate()),
                                    //builder.equal(root.get("personalEmail"), credit.getPersonalEmail())
                            )
                    );

            Long count = session.createQuery(query).getSingleResult();
            isNew = (count == 0);
            System.out.println("there is a new credit card details: " + isNew );
        } catch (Exception e) {
            // You might want to log the exception here or handle specific exceptions
            throw new HibernateException("Failed to check if credit card is new", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Ensure session is closed in the finally block
            }
        }
        return isNew;
    }



    public static List<CreditCard> getCreditCardDetailsByPersonalEmail(String personalEmail) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            PersonalDetails personalDetails = session.bySimpleNaturalId(PersonalDetails.class).load(personalEmail);
            if (personalDetails != null && personalDetails.getCreditCardDetails() != null) {
                // Initialize the proxy to fetch the credit cards if lazy loading
                Hibernate.initialize(personalDetails.getCreditCardDetails());
                return personalDetails.getCreditCardDetails();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return Collections.emptyList(); // Return an empty list if there are no details or in case of exceptions
    }


    public static List<CreditCard> getAllCreditCardDetails() {
        try {
            session = getSessionFactory().openSession();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CreditCard> query = builder.createQuery(CreditCard.class);
            query.from(CreditCard.class);
            return session.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) session.close();
        }
        return null;
    }
}
