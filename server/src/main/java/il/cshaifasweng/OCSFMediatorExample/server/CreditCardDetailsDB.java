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

    public static void addCreditCardDetails(CreditCard newCardDetails, PersonalDetails personalDetails) {
        try {
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            // Start the transaction
            session.beginTransaction();

            // If the personalDetails object doesn't already exist, save it
            if (personalDetails != null) {

                personalDetails.addCreditCard(newCardDetails);
                session.save(personalDetails);

                newCardDetails.setPersonalDetails(personalDetails);
                session.save(newCardDetails);


                session.getTransaction().commit();
            } else {
                session.getTransaction().rollback(); // Explicit rollback if personalDetails is null
                throw new RuntimeException("PersonalDetails cannot be null");
            }
        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback on error
            }
            throw new RuntimeException("Failed to add credit card details", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }



    public static void addCreditCardToExistingPersonalDetails(CreditCard newCreditCard,PersonalDetails newPersonalDetails) {
        Transaction transaction = null;
        try {
            // Ensure session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
                //System.out.println("Session opened");
            }

            // Start transaction
            transaction = session.beginTransaction();
            //System.out.println("Transaction started");

            // Validate input
            if (newPersonalDetails == null || newCreditCard == null) {
                throw new IllegalArgumentException("PersonalDetails or CreditCard cannot be null");
            }

            // Ensure relationship is established both ways
            newCreditCard.getPersonalDetails().add(newPersonalDetails);
            newPersonalDetails.getCreditCardDetails().add(newCreditCard);

            // Save entities (Cascade handles related entity persistence)
            //System.out.println("Saving CreditCard...");
            session.save(newCreditCard);
            //System.out.println("CreditCard saved");

            //System.out.println("Saving PersonalDetails...");
            session.save(newPersonalDetails);
            //System.out.println("PersonalDetails saved");

            // Commit transaction
            transaction.commit();
            //System.out.println("Transaction committed");

        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                //System.out.println("Transaction rolled back");
            }
            e.printStackTrace(); // <-- Print the full stack trace
            throw new RuntimeException("Failed to save CreditCard and PersonalDetails", e);
        }
             finally {
            if (session != null && session.isOpen()) {
                session.close();
                //System.out.println("Session closed");
            }
        }
    }







    public static boolean isCreditCardNew(CreditCard credit) throws HibernateException {
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
                                    builder.equal(root.get("cardNumber"), credit.getCardNumber())
                            )
                    );

            Long count = session.createQuery(query).getSingleResult();
            isNew = (count == 0);
            //System.out.println("there is a new credit card details: " + isNew );
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


    public static void addPersonalDetailsAndAssociateWithCreditCard(PersonalDetails newPersonalDetails, CreditCard existingCreditCard) {
        try {
            // Open session if not already open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            // Begin transaction
            session.beginTransaction();

            // Check if newPersonalDetails is null
            if (newPersonalDetails == null) {
                //System.out.println("newPersonalDetails is null");
                return; // Exit early if the PersonalDetails is null
            }


            // Also, add the CreditCard to the PersonalDetails' list of associated CreditCards
            newPersonalDetails.addCreditCard(existingCreditCard);

            // Save the new PersonalDetails to the database
            session.save(newPersonalDetails);



            // Add the new PersonalDetails to the CreditCard's list of associated PersonalDetails
            existingCreditCard.getPersonalDetails().add(newPersonalDetails);

            // Save the updated CreditCard object (this will persist the association)
            session.saveOrUpdate(existingCreditCard);

            // Commit the transaction
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback on error
            }
            System.out.println("Error occurred: " + e.getMessage());
            throw new RuntimeException("Failed to add personal details and associate with credit card", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public static void addCreditCardToPersonalDetailsIfBothExists(PersonalDetails existingPersonalDetails, CreditCard existingCreditCard) {
        try {
            // Open session if not already open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            //System.out.println("The credit card and the personal details are both exist and now associating with the credit card.");

            // Begin transaction
            session.beginTransaction();

            // Retrieve the entities from the session to avoid duplicate entity issue
            CreditCard dbCreditCard = session.get(CreditCard.class, existingCreditCard.getId());
            PersonalDetails dbPersonalDetails = session.get(PersonalDetails.class, existingPersonalDetails.getId());

            boolean isAssociated = false;
            boolean isReverseAssociated = false;

            // Check associations
            for (PersonalDetails pd : dbCreditCard.getPersonalDetails()) {
                if (pd.getEmail().equals(dbPersonalDetails.getEmail())) {
                    isAssociated = true;
                    break;
                }
            }

            for (CreditCard cc : dbPersonalDetails.getCreditCardDetails()) {
                if (cc.getCardNumber().equals(dbCreditCard.getCardNumber())) {
                    isReverseAssociated = true;
                    break;
                }
            }

            if (isAssociated && isReverseAssociated) {
                //System.out.println("The card and personal details are already associated. No need to do anything.");
                return;
            }

            // Associate if not already associated
            if (!dbPersonalDetails.getCreditCardDetails().contains(dbCreditCard)) {
                dbPersonalDetails.addCreditCard(dbCreditCard);
            }

            if (!dbCreditCard.getPersonalDetails().contains(dbPersonalDetails)) {
                dbCreditCard.getPersonalDetails().add(dbPersonalDetails);
            }

            // Save or update the CreditCard and PersonalDetails objects
            session.saveOrUpdate(dbPersonalDetails);
            session.saveOrUpdate(dbCreditCard);

            // Commit the transaction
            session.getTransaction().commit();
            //System.out.println("Transaction committed.");
        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback on error
            }
            throw new RuntimeException("Failed to add credit card to personal details", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
              //  System.out.println("Session closed.");
            }
        }
    }






    public static CreditCard getCreditCardDetailsByCardNumber(String cardNumber) {
        try (SessionFactory sessionFactory = getSessionFactory();
             Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<CreditCard> query = builder.createQuery(CreditCard.class);
            Root<CreditCard> root = query.from(CreditCard.class);

            query.select(root).where(builder.equal(builder.lower(root.get("cardNumber")), cardNumber.toLowerCase()));

            CreditCard cc = session.createQuery(query).uniqueResult();
            session.getTransaction().commit();

            return cc;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving CreditCard by card number", e);
        }
    }




}
