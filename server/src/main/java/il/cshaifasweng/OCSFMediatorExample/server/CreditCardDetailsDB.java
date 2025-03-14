package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.CreditCard;
import il.cshaifasweng.OCSFMediatorExample.entities.CreditCardCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.PersonalDetails;
import il.cshaifasweng.OCSFMediatorExample.entities.Users;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class CreditCardDetailsDB {
    private static Session session;

    public static void addCreditCardDetails(CreditCard newCardDetails, String personalEmail) {
        Transaction transaction = null;
        Session session = null;
        try {
            System.out.println("1");
            session = getSessionFactory().openSession();
            System.out.println("2");
            transaction = session.beginTransaction();

            PersonalDetails personalDetails = (PersonalDetails) session.createQuery("FROM PersonalDetails WHERE email = :email")
                    .setParameter("email", personalEmail)
                    .uniqueResult();
            System.out.println("3");

            if (personalDetails != null) {
                newCardDetails.setPersonalDetails(personalDetails);
                session.save(newCardDetails);
                System.out.println("Credit card details added for: " + personalEmail);
                transaction.commit();
            } else {
                System.out.println("No personal details found with email: " + personalEmail);
                transaction.rollback();  // Explicit rollback if no personal details are found
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Failed to add credit card details", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    public static boolean isCreditCardNew(CreditCardCheck credit) throws Exception {
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
                                builder.equal(root.get("cvv"), credit.getCvv()),
                                builder.equal(root.get("cardNumber"), credit.getCardNumber()),
                                builder.equal(root.get("cardholderName"), credit.getCardholderName()),
                                builder.equal(root.get("cardholdersID"), credit.getCardholdersID()),
                                builder.equal(root.get("expiryDate"), credit.getExpiryDate()),
                                builder.equal(root.get("personalEmail"), credit.getPersonalEmail())
                                //builder.equal(root.get("password"), password) // Uncomment and modify if password needs to be checked
                        )
                );


        Long count = session.createQuery(query).getSingleResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }
        return count == 0;
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

    private static SessionFactory getSessionFactory() {
        // This method should return your Hibernate SessionFactory instance
        return App.getSessionFactory();
    }
}
