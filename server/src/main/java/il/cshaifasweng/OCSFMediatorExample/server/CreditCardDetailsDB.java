package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.hibernate.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.OrdersDB.saveOrder;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.*;

public class CreditCardDetailsDB {
    private static Session session;

    public static void addCreditCardDetails(CreditCard newCardDetails, PersonalDetails personalDetails, Order newOrder) {
        try {
            System.out.println("Adding personal and credit card details");
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            // Start the transaction
            session.beginTransaction();


            for(MealInTheCart meal : newOrder.getMeals()) {
                for (CustomizationWithBoolean custom : meal.getMeal().getCustomizationsList()) {
                    session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
                }
            }
            // If the personalDetails object doesn't already exist, save it
            if (personalDetails != null) {




                personalDetails.addCreditCard(newCardDetails);
                session.save(personalDetails);

                newCardDetails.setPersonalDetails(personalDetails);
                session.save(newCardDetails);


                session.saveOrUpdate(newOrder);


                session.getTransaction().commit();
                allCreditCards.add(newCardDetails);
                allPersonalDetails.add(personalDetails);
                allOrders.add(newOrder);
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



    public static void addCreditCardToExistingPersonalDetails(CreditCard newCreditCard,PersonalDetails PersonalDetails,Order newOrder) {
        Transaction transaction = null;
        try {
            System.out.println("Adding personal and credit card details");
            // Ensure session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            transaction = session.beginTransaction();

            // Validate input
            if (PersonalDetails == null || newCreditCard == null) {
                throw new IllegalArgumentException("PersonalDetails or CreditCard cannot be null");
            }
            for(MealInTheCart meal : newOrder.getMeals()) {
                for (CustomizationWithBoolean custom : meal.getMeal().getCustomizationsList()) {
                    session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
                }
            }




            // Load existing entities from the session to avoid duplicates
            PersonalDetails managedPersonalDetails = session.get(PersonalDetails.class, PersonalDetails.getId());

            // Associate new credit card
            newCreditCard.setPersonalDetails(managedPersonalDetails);
            managedPersonalDetails.getCreditCardDetails().add(newCreditCard);
            allCreditCards.add(newCreditCard);
            for(PersonalDetails details : allPersonalDetails) {
                if(details.getEmail().equals(PersonalDetails.getEmail())) {
                    details.getCreditCardDetails().add(newCreditCard);
                }
            }

            // Save the credit card (no duplicate error)
            session.save(newCreditCard);

            // Update other entities
            session.saveOrUpdate(managedPersonalDetails);
            session.saveOrUpdate(newOrder);
            allOrders.add(newOrder);


            transaction.commit();

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


    public static void addPersonalDetailsAndAssociateWithCreditCard(PersonalDetails newPersonalDetails, CreditCard existingCreditCard,Order newOrder) {
        System.out.println("Adding personal and credit card details");
        try {
            // Open session if not already open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            // Begin transaction
            session.beginTransaction();
            for(MealInTheCart meal : newOrder.getMeals()) {
                for (CustomizationWithBoolean custom : meal.getMeal().getCustomizationsList()) {
                    session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
                }
            }
            // Check if newPersonalDetails is null
            if (newPersonalDetails == null) {
                return; // Exit early if the PersonalDetails is null
            }




            newPersonalDetails.addCreditCard(existingCreditCard);


            session.save(newPersonalDetails);



            // Add the new PersonalDetails to the CreditCard's list of associated PersonalDetails
            existingCreditCard.getPersonalDetails().add(newPersonalDetails);

            // Save the updated CreditCard object (this will persist the association)
            session.saveOrUpdate(existingCreditCard);

            session.saveOrUpdate(newOrder);
            allOrders.add(newOrder);

            // Commit the transaction
            session.getTransaction().commit();
            allPersonalDetails.add(newPersonalDetails);

            for(CreditCard cc :allCreditCards)
            {
                if(cc.getCardNumber().equals(existingCreditCard.getCardNumber())) {
                    cc.getPersonalDetails().add(newPersonalDetails);
                }
            }

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

    public static void addCreditCardToPersonalDetailsIfBothExists(PersonalDetails existingPersonalDetails, CreditCard existingCreditCard,Order newOrder) {
        System.out.println("Adding personal and credit card details");
        try {
            // Open session if not already open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }


            //System.out.println("The credit card and the personal details are both exist and now associating with the credit card.");

            // Begin transaction
            session.beginTransaction();

            for(MealInTheCart meal : newOrder.getMeals()) {
                for (CustomizationWithBoolean custom : meal.getMeal().getCustomizationsList()) {
                    session.clear();
                    session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
                }
            }

            // Retrieve the entities from the session to avoid duplicate entity issue
            CreditCard dbCreditCard = session.get(CreditCard.class, existingCreditCard.getId());
            PersonalDetails dbPersonalDetails = session.get(PersonalDetails.class, existingPersonalDetails.getId());

            // Associate if not already associated
            if (!dbPersonalDetails.getCreditCardDetails().contains(dbCreditCard)) {
                dbPersonalDetails.addCreditCard(dbCreditCard);
                for(PersonalDetails details : allPersonalDetails) {
                    if(details.getEmail().equals(existingPersonalDetails.getEmail())) {
                        details.addCreditCard(dbCreditCard);
                    }
                }
            }

            if (!dbCreditCard.getPersonalDetails().contains(dbPersonalDetails)) {
                for(CreditCard cc : allCreditCards) {
                    if(cc.getCardNumber().equals(existingCreditCard.getCardNumber())) {
                        cc.getPersonalDetails().add(dbPersonalDetails);
                    }
                }
                dbCreditCard.getPersonalDetails().add(dbPersonalDetails);
            }

            // Save or update the CreditCard and PersonalDetails objects
            session.saveOrUpdate(dbPersonalDetails);
            session.saveOrUpdate(dbCreditCard);
            session.saveOrUpdate(newOrder);
            allOrders.add(newOrder);

            // Commit the transaction
            session.getTransaction().commit();
            //System.out.println("Transaction committed.");
        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback on error
            }
            System.out.println("Error: " + e.getMessage()); // Print the exception message
            throw new RuntimeException("Failed to add credit card to personal details", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
              //  System.out.println("Session closed.");
            }
        }
    }






    public static CreditCard getCreditCardDetailsByCardNumber(String cardNumber) {
        for(CreditCard cc : allCreditCards) {
            if(cc.getCardNumber().equals(cardNumber)) {
                return cc;
            }
        }
        return null;
    }
    public static List<CreditCard> getAllCreditCards() {
        try (Session localSession = getSessionFactory().openSession()) {
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<CreditCard> query = builder.createQuery(CreditCard.class);
            query.from(CreditCard.class);
            return localSession.createQuery(query).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //**************************** for reservation purposes***************************************************************//
    public static void addCreditCardDetailsForRes(CreditCard newCardDetails, PersonalDetails personalDetails, ReservationSave finalReservationEvent) {
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

                allPersonalDetails.add(personalDetails);
                allCreditCards.add(newCardDetails);

                session.saveOrUpdate(finalReservationEvent);
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
    public static void addCreditCardToExistingPersonalDetailsForRes(CreditCard newCreditCard,PersonalDetails PersonalDetails ,ReservationSave finalReservationEvent) {
        Transaction transaction = null;
        try {
            // Ensure session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
                //System.out.println("Session opened");
            }

            transaction = session.beginTransaction();

            // Validate input
            if (PersonalDetails == null || newCreditCard == null) {
                throw new IllegalArgumentException("PersonalDetails or CreditCard cannot be null");
            }



            // Load existing entities from the session to avoid duplicates
            PersonalDetails managedPersonalDetails = session.get(PersonalDetails.class, PersonalDetails.getId());

            // Associate new credit card
            newCreditCard.setPersonalDetails(managedPersonalDetails);
            managedPersonalDetails.getCreditCardDetails().add(newCreditCard);

            // Save the credit card (no duplicate error)
            session.save(newCreditCard);

            // Update other entities
            session.saveOrUpdate(managedPersonalDetails);
            session.saveOrUpdate(finalReservationEvent);

            allCreditCards.add(newCreditCard);
            for(PersonalDetails details : allPersonalDetails) {
                if(details.getEmail().equals(PersonalDetails.getEmail())) {
                    details.addCreditCard(newCreditCard);
                }
            }


            transaction.commit();

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
    public static void addPersonalDetailsAndAssociateWithCreditCardForRes(PersonalDetails newPersonalDetails, CreditCard existingCreditCard,ReservationSave finalReservationEvent) {
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

            session.saveOrUpdate(finalReservationEvent);
            // Commit the transaction
            session.getTransaction().commit();
            allPersonalDetails.add(newPersonalDetails);
            for(CreditCard cc : allCreditCards) {
                if(cc.getCardNumber().equals(existingCreditCard.getCardNumber())) {
                    cc.getPersonalDetails().add(newPersonalDetails);
                }
            }

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
    public static void addCreditCardToPersonalDetailsIfBothExistsForRes(PersonalDetails existingPersonalDetails, CreditCard existingCreditCard,ReservationSave finalReservationEvent) {
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

            // Associate if not already associated
            if (!dbPersonalDetails.getCreditCardDetails().contains(dbCreditCard)) {
                dbPersonalDetails.addCreditCard(dbCreditCard);
                for(CreditCard cc : allCreditCards) {
                    if(cc.getCardNumber().equals(existingCreditCard.getCardNumber())) {
                        cc.getPersonalDetails().add(dbPersonalDetails);
                    }
                }
            }

            if (!dbCreditCard.getPersonalDetails().contains(dbPersonalDetails)) {
                dbCreditCard.getPersonalDetails().add(dbPersonalDetails);
                for(CreditCard cc : allCreditCards) {
                    if(cc.getCardNumber().equals(existingCreditCard.getCardNumber())) {
                        cc.getPersonalDetails().add(dbPersonalDetails);
                    }
                }
            }

            // Save or update the CreditCard and PersonalDetails objects
            session.saveOrUpdate(dbPersonalDetails);
            session.saveOrUpdate(dbCreditCard);
            session.saveOrUpdate(finalReservationEvent);

            // Commit the transaction
            session.getTransaction().commit();
            //System.out.println("Transaction committed.");
        } catch (Exception e) {
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback on error
            }
            System.out.println("Error: " + e.getMessage()); // Print the exception message
            throw new RuntimeException("Failed to add credit card to personal details", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
                //  System.out.println("Session closed.");
            }
        }
    }






}
