package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.ReportRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.entities.CreditCardCheck;

import java.io.IOException;

import java.time.LocalDateTime;

import java.time.LocalDate;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalTime;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ReportDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.TableDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.PersonalDetailsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.UsersDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ReportDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.*;
import il.cshaifasweng.OCSFMediatorExample.server.RevenueReport;
import il.cshaifasweng.OCSFMediatorExample.server.OrderTypeReport;

public class SimpleServer extends AbstractServer {

    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private static ArrayList<ConnectionToClient> Subscribers = new ArrayList<>();

    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Received message from client ");
        String msgString = msg.toString();


        if (msg instanceof ReservationEvent) {
            ReservationEvent reservation = (ReservationEvent) msg;
            printAllTablesInRestaurant(reservation.getRestaurantName());
            List<ReservationEvent> availableReservation = check_Available_Reservation(reservation);
            System.out.println("the available times are :" + availableReservation);

            if (!availableReservation.isEmpty()) {
                try {
                    client.sendToClient(availableReservation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("*********************************************");
                System.out.println("No available tables for the selected time and seats.");
                System.out.println("Checking other time slots...");
                System.out.println("*********************************************");

                // Define restaurant opening and closing times
                LocalTime closingTime = getRestaurantByName(reservation.getRestaurantName()).getClosingTime();// 10:00 PM

                LocalTime start = getRestaurantByName(reservation.getRestaurantName()).getOpeningTime(); // e.g., 10:00 AM
                LocalDate currentDate = reservation.getReservationDateTime().toLocalDate(); // Get the current date
                LocalDateTime startTime = LocalDateTime.of(currentDate, start); // Combine date and time

                // Iterate through all time slots from the current time to the closing time
                List<LocalDateTime> availableTimeSlots = new ArrayList<>();
                System.out.println("*********************************************");
                System.out.println("the opening hour is: " + start);
                System.out.println("the closing hour is: " + closingTime);
                System.out.println("the start time is: " + startTime);
                System.out.println("*********************************************");


                while (startTime.toLocalTime().isBefore(closingTime.minusMinutes(30))) {

                    // Check if tables are available for this time slot
                    System.out.println("*********************************************");
                    System.out.println("checking this tims slot: " + startTime);
                    System.out.println("*********************************************");
                    List<TableNode> tablesForSlot = getAvailableTables(reservation.getRestaurantName(), startTime, reservation.getSeats(), reservation.isInside());

                    if (!tablesForSlot.isEmpty() && startTime.isAfter(LocalDateTime.now())) {
                        // If tables are available, add the time slot to the list
                        availableTimeSlots.add(startTime);
                    } else {
                        System.out.println("there is no availale tables for this slot -" + startTime);
                    }

                    // Move to the next time slot (e.g., increment by 1 hour)
                    startTime = startTime.plusMinutes(30);
                }

                if (!availableTimeSlots.isEmpty()) {
                    // Notify the client of available time slots
                    System.out.println("*********************************************");
                    System.out.println("Available time slots for the restaurant:");
                    for (LocalDateTime slot : availableTimeSlots) {
                        System.out.println("  - " + slot.toLocalTime());
                    }
                    System.out.println("*********************************************");

                    try {
                        // Create a new ReservationEvent with all available time slots
                        DifferentResrvation available_Reservation_for_client = new DifferentResrvation(reservation.getRestaurantName(), // Use the restaurant name from the event
                                reservation.getSeats(),         // Use the number of seats from the event
                                reservation.isInside(),          // Use the inside/outside preference from the event
                                availableTimeSlots         // Pass all available time slots
                        );
                        // Send the available reservation to the client
                        client.sendToClient(available_Reservation_for_client);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to send available time slots to the client.", e);
                    }
                } else {
                    // Notify the client that no tables are available at any time
                    System.out.println("*********************************************");
                    System.out.println("No available tables at any time for the selected seats.");
                    System.out.println("*********************************************");
                    List<ReservationEvent> reservationList = new ArrayList<>();
                    // Create a new ReservationEvent with all available time slots
                    ReservationEvent available_Reservation_for_client = new ReservationEvent(reservation.getRestaurantName(), reservation.getSeats(), reservation.isInside());
                    reservationList.add(available_Reservation_for_client);
                    try {
                        client.sendToClient(reservationList);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
        if (msg instanceof FinalReservationEvent) {
            try {
                FinalReservationEvent event = (FinalReservationEvent) msg;

                // Print all the tables in the specified restaurant
                printAllTablesInRestaurant(event.getRestaurantName());

                // Fetch available tables for the restaurant and time
                List<TableNode> availableTables = getAvailableTables(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside());

                if (availableTables.isEmpty()) {
                    System.out.println("*********************************************");
                    System.out.println("No available tables for the selected time and seats.");
                    System.out.println("Checking other time slots...");
                    System.out.println("*********************************************");

                    // Define restaurant opening and closing times
                    LocalTime closingTime = getRestaurantByName(event.getRestaurantName()).getClosingTime(); // 10:00 PM
                    LocalTime start = getRestaurantByName(event.getRestaurantName()).getOpeningTime(); // e.g., 10:00 AM
                    LocalDate currentDate = event.getReservationDateTime().toLocalDate(); // Get the current date
                    LocalDateTime startTime = LocalDateTime.of(currentDate, start); // Combine date and time

                    // Iterate through all time slots from the current time to the closing time
                    List<LocalDateTime> availableTimeSlots = new ArrayList<>();

                    while (startTime.toLocalTime().isBefore(closingTime)) {
                        // Check if tables are available for this time slot
                        List<TableNode> tablesForSlot = getAvailableTables(event.getRestaurantName(), startTime, event.getSeats(), event.isInside());

                        if (!tablesForSlot.isEmpty()) {
                            // If tables are available, add the time slot to the list
                            availableTimeSlots.add(startTime);
                        }

                        // Move to the next time slot (e.g., increment by 1 hour)
                        startTime = startTime.plusMinutes(15);
                    }

                    if (!availableTimeSlots.isEmpty()) {
                        // Notify the client of available time slots
                        System.out.println("*********************************************");
                        System.out.println("Available time slots for the restaurant:");
                        for (LocalDateTime slot : availableTimeSlots) {
                            System.out.println("  - " + slot.toLocalTime());
                        }
                        System.out.println("*********************************************");

                        try {
                            // Create a new ReservationEvent with all available time slots
                            ReservationEvent available_Reservation_for_client = new ReservationEvent(event.getRestaurantName(), // Use the restaurant name from the event
                                    event.getSeats(),         // Use the number of seats from the event
                                    event.isInside(),          // Use the inside/outside preference from the event
                                    availableTimeSlots         // Pass all available time slots
                            );
                            List<ReservationEvent> reservationList = new ArrayList<>();
                            reservationList.add(available_Reservation_for_client);
                            // Send the available reservation to the client
                            client.sendToClient(reservationList);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to send available time slots to the client.", e);
                        }
                    } else {
                        // Notify the client that no tables are available at any time
                        System.out.println("*********************************************");
                        System.out.println("No available tables at any time for the selected seats.");
                        System.out.println("*********************************************");
                        List<ReservationEvent> reservationList = new ArrayList<>();
                        // Create a new ReservationEvent with all available time slots
                        ReservationEvent available_Reservation_for_client = new ReservationEvent(event.getRestaurantName(), event.getSeats(), event.isInside());
                        reservationList.add(available_Reservation_for_client);
                        try {
                            client.sendToClient(reservationList);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    System.out.println("*********************************************");
                    System.out.println("The available tables are: " + availableTables);
                    System.out.println("*********************************************");

                    // Assign tables to the reservation
                    assignTablesToReservation(availableTables, event.getReservationDateTime(), event.isInside());
                    printAllTablesInRestaurant(event.getRestaurantName());

                    // Create a new ReservationSave entity to save the reservation and tables
                    ReservationSave reservationSave = new ReservationSave(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside(), event.getFullName(), event.getPhoneNumber(), event.getEmail(), availableTables);

                    // Save the reservation to the database
                    saveReservationToDatabase(reservationSave);

                    printAllReservationSaves();
                    // Notify the client that the reservation was successful
                    System.out.println("Reservation confirmed successfully.");
                    client.sendToClient("Reservation confirmed successfully.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Notify the client of the error
                System.out.println("Failed to save reservation.");
            }
        }
        if (msg instanceof String && msg.equals("getAllRestaurants")) {
            try {
                RestaurantList restaurantList = new RestaurantList();
                restaurantList.setRestaurantList(getAllRestaurants()); // Set list to send
                System.out.println(restaurantList.getRestaurantList());
                client.sendToClient(restaurantList); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (msg instanceof String && msg.equals("getAllComplaints6")) {
            try {
                List<Complain> complainList = getAllComplainss();
                for (Complain Complain : complainList) {
                    Complain.toString();
                }

                // for complain and do
                List<Complain> List1 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Complaint") && complain.getStatus().equals("Do"))
                        List1.add(complain);
                    System.out.println("fill list 1");
                }

                // for complain amd done
                List<Complain> List2 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Complaint") && complain.getStatus().equals("Done"))
                        List2.add(complain);
                    System.out.println("fill list 2");
                }
                // for Feedback and do
                List<Complain> List3 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Feedback") && complain.getStatus().equals("Do"))
                        List3.add(complain);
                    System.out.println("fill list 3");
                }

                // for Feedback amd done
                List<Complain> List4 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Feedback") && complain.getStatus().equals("Done"))
                        List4.add(complain);
                    System.out.println("fill list 4");
                }
                // for Suggestion and do
                List<Complain> List5 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Suggestion") && complain.getStatus().equals("Do"))
                        List5.add(complain);
                    System.out.println("fill list 5");
                }

                // for Suggestion amd done
                List<Complain> List6 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Suggestion") && complain.getStatus().equals("Done"))
                        List6.add(complain);
                    System.out.println("fill list 6");
                }
                ListComplainList result = new ListComplainList(List1, List2, List3, List4, List5, List6);
                client.sendToClient(result); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (msg instanceof String && msg.equals("getAllComplaints")) {
            try {
                ComplainList complist = new ComplainList();
                complist.setComplainList(getAllComplains()); // Set list to send
                System.out.println(complist.getComplainList());
                client.sendToClient(complist); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (msg instanceof specificComplains) {
            try {
                specificComplains specificComplains =(specificComplains)msg;
                String kind = specificComplains.getSpecificKind();
                String status = specificComplains.getSpecificStatus();
                ComplainList SpecificList = new ComplainList();
                ComplainList Allcomplist = new ComplainList();
                Allcomplist.setComplainList(getAllComplains()); // Set list to send

                for (Complain complain : Allcomplist.getComplainList()) {
                    if (kind.equals(complain.getKind()) && status.equals(complain.getStatus())) {
                        SpecificList.getComplainList().add(complain);
                    }
                }
                client.sendToClient(SpecificList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        if (msg instanceof updateResponse) {
            updateResponse response = (updateResponse) msg;
            System.out.println("Received updateResponse from client: " + response.getnewResponse());
            // שליחת התגובה לכל הלקוחות (אם זה נדרש)
            sendToAllClients(msg);
            System.out.println("Sent updateResponse to all clients");

            String subject = "Thanks For Contacting MAMA's Kitchen";
            String email = response.getEmailComplain();
            String body = response.getnewResponse();
            EmailSender emailSender = new EmailSender();
            emailSender.sendEmail(subject, body, email);
            System.out.println("Email sent to: " + email);

            try {
                updateComplainResponseInDatabase(response);
                System.out.println("Updating Complain Response In Database");

                client.sendToClient(msg);
            } catch (IOException e) {
                throw new RuntimeException("Failed to send response to client", e);
            }
        }




        if (msg instanceof mealEvent) {
            //here we're adding new meal !!
            //System.out.println("Received adding new mealEvent ");
            Meal addResult = AddNewMeal((mealEvent) msg);//if "added" then successed if "exist" then failed bcs there is a meal like that
            System.out.println("Added new mealEvent to the database");
            ((mealEvent) msg).setMeal(addResult);
            sendToAll(msg);


        }
        if (msg instanceof UpdateMealRequest) {
            //here we're adding new meal !!
            //System.out.println("Received adding new UpdateMealRequest ");
            String addResult = updateMeal((UpdateMealRequest) msg);//if "added" then successed if "not exist" then failed bcs there is no meal like that
            System.out.println("Added new UpdateMealRequest to the database");
            sendToAll(msg);
            if (Objects.equals(addResult, "added")) {
                sendToAll(msg);
            }

        }
        if (msg instanceof String && msgString.equals("toMenuPage")) {
            try {
                client.sendToClient(getmealEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        if (msg instanceof String && msgString.startsWith("menu")) {
            String branch = msgString.substring(4);
            System.out.println("getting a menu to " + branch);
            List<Meal> ml=null;
            try {
                if(branch.equals("ALL")) {
                    ml = MealsDB.GetAllMeals();
                }
                else{
                ml = getmealsb(branch);}
                MealsList sending = new MealsList(ml);
                client.sendToClient(sending);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        if (msg instanceof UserCheck) {
            System.out.println(((UserCheck) msg).getUsername());
            if (((UserCheck) msg).isState() == 1)//if login
            {
                try {

                    if (checkUser(((UserCheck) msg).getUsername(), ((UserCheck) msg).getPassword())) {
                        if (!checkAndUpdateUserSignInStatus(((UserCheck) msg).getUsername())) {
                            getUserInfo((UserCheck) msg); //to update it's info so we can save them.
                            //send to singin to the user
                            ((UserCheck) msg).setRespond("Valid");
                        } else ((UserCheck) msg).setRespond("Already Signed in");
                        client.sendToClient(msg);
                    } else {
                        ((UserCheck) msg).setRespond("Username or password incorrect");
                        client.sendToClient(msg);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (((UserCheck) msg).isState() == 0) //if register
            {
                String response = AddNewUser((UserCheck) (msg));
                ((UserCheck) msg).setRespond(response);
                try {
                    client.sendToClient(msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (((UserCheck) msg).isState() == 2) //if forgetpass
            {
                try {
                    if (checkEmail(((UserCheck) msg))) {
                        ((UserCheck) msg).setRespond("Valid");
                        client.sendToClient(msg);
                    } else {
                        ((UserCheck) msg).setRespond("notValid");
                        client.sendToClient(msg);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (((UserCheck) msg).isState() == 3) {//if just a name check
                try {
                    if (checkUserName(((UserCheck) msg).getUsername())) {
                        ((UserCheck) msg).setRespond("notValid");
                        client.sendToClient(msg);
                    } else {
                        ((UserCheck) msg).setRespond("Valid");
                        client.sendToClient(msg);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (((UserCheck) msg).isState() == 4) {
                try {
                    //System.out.println("Logging out from the database");
                    SignOut((UserCheck) msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }


        }
        /*************************adan*****************************/
        if (msg instanceof PaymentCheck) {
            PaymentCheck paymentCheck = (PaymentCheck) msg;
            //System.out.println("Processing request for card number: " + paymentCheck.getCreditCard().getCardNumber());

            try {
                //System.out.println(" is new card " + isCreditCardNew(paymentCheck.getCreditCard()));
                //System.out.println("person");
                PersonalDetails personalDetailsDB = getPersonalDetailsByEmail(paymentCheck.getPersonalDetails().getEmail());
                //System.out.println("personal "+personalDetailsDB);
                CreditCard cc = getCreditCardDetailsByCardNumber(paymentCheck.getCreditCard().getCardNumber());
                //System.out.println("the cc number "+paymentCheck.getCreditCard().getCardNumber());
                //System.out.println("personal details " + cc);

                if (cc == null) {
                    //System.out.println("new credit card");

                    if (personalDetailsDB == null) {
                        //System.out.println("new personal details");
                        paymentCheck.setResponse("Added the personal details and the Credit Card to the database");
                        addCreditCardDetails(paymentCheck.getCreditCard(), paymentCheck.getPersonalDetails());
                    } else {
                        //  System.out.println("not new personal details");
                        paymentCheck.setResponse("Added the Credit Card to the database.");
                        addCreditCardToExistingPersonalDetails(paymentCheck.getCreditCard(), personalDetailsDB);
                    }
                    client.sendToClient(paymentCheck);
                } else {
                    //System.out.println("not new credit card");
                    if(personalDetailsDB == null) {
                        //System.out.println("new personal details");
                        paymentCheck.setResponse("Added the personal details to the database");
                        addPersonalDetailsAndAssociateWithCreditCard(paymentCheck.getPersonalDetails(), cc);
                    }

                    else {
                        //System.out.println("not new personal details");
                        paymentCheck.setResponse("Updated the personal details to the database.");
                        addCreditCardToPersonalDetailsIfBothExists(personalDetailsDB, cc);
                        //System.out.println("Associated.");
                    }
                    client.sendToClient(paymentCheck);
                }
            } catch (Exception e) {
                paymentCheck.setResponse("Error processing request: " + e.getMessage());
                throw new RuntimeException("Failed to process credit card request", e);
            }
        }


        /*if (msg instanceof CreditCardCheck) {
            CreditCardCheck creditCardCheck = (CreditCardCheck) msg;
            System.out.println("Processing request for card number: " + creditCardCheck.getCardNumber());

            try {
                System.out.println(" is new card " + isCreditCardNew(creditCardCheck));
                if (isCreditCardNew(creditCardCheck)) {
                    PersonalDetails personalDetails = PersonalDetailsDB.getPersonalDetailsByEmail(creditCardCheck.getPersonalEmail());
                    if (personalDetails != null) {
                        System.out.println("personal details found");
                        CreditCard newCardDetails = new CreditCard();
                        newCardDetails.setCardNumber(creditCardCheck.getCardNumber());
                        newCardDetails.setCardholderName(creditCardCheck.getCardholderName());
                        newCardDetails.setCvv(creditCardCheck.getCvv());
                        newCardDetails.setExpiryDate(creditCardCheck.getExpiryDate()); // Ensure date format is correctly parsed
                        newCardDetails.setCardholdersID(creditCardCheck.getCardholdersID());

                        newCardDetails.setPersonalDetails(personalDetails);
                        CreditCardDetailsDB.addCreditCardDetails(newCardDetails, personalDetails.getEmail()); // Assuming email is used for lookup in the add method
                        creditCardCheck.setValid(true);
                        creditCardCheck.setRespond("Credit card details added successfully.");
                    } else {

                        creditCardCheck.setValid(false);
                        creditCardCheck.setRespond("Personal details not found.");
                    }
                    client.sendToClient(creditCardCheck);
                } else {
                    // Existing card validation
                    List<CreditCard> creditCards = CreditCardDetailsDB.getCreditCardDetailsByPersonalEmail(creditCardCheck.getPersonalEmail());
                    boolean isValid = creditCards.stream().anyMatch(card ->
                            card.getCardNumber().equals(creditCardCheck.getCardNumber()) &&
                                    card.getCvv().equals(creditCardCheck.getCvv()) &&
                                    card.getCardholderName().equals(creditCardCheck.getCardholderName()) &&
                                    card.getExpiryDate().equals(creditCardCheck.getExpiryDate())
                    );

                    creditCardCheck.setValid(isValid);
                    creditCardCheck.setRespond(isValid ? "Credit Card validation successful" : "Credit Card validation failed");

                    // Send the validation result back to the client
                    client.sendToClient(creditCardCheck);
                }
            } catch (Exception e) {
                // Handle other exceptions that might be thrown during processing
                creditCardCheck.setValid(false);
                creditCardCheck.setRespond("Error processing request: " + e.getMessage());
                throw new RuntimeException("Failed to process credit card request", e);
            }
        }

        if (msg instanceof PersonalDetailsCheck) {
            PersonalDetailsCheck detailsCheck = (PersonalDetailsCheck) msg;

            try {
                PersonalDetails existingDetails = PersonalDetailsDB.getPersonalDetailsByEmail(detailsCheck.getEmail());

                if (existingDetails != null) {
                    // Validate existing personal details
                    if (existingDetails.getName().equals(detailsCheck.getName()) && existingDetails.getPhoneNumber().equals(detailsCheck.getPhoneNumber())) {
                        // Existing details match the provided details
                        detailsCheck.setDetailsComplete(true);
                        detailsCheck.setEmailVerified(true);
                        detailsCheck.setRespond("Existing details validated successfully.");
                    } else {
                        // Conflict in details
                        detailsCheck.setDetailsComplete(false);
                        detailsCheck.setEmailVerified(false);
                        detailsCheck.setRespond("Error: Mismatch with existing details for this email.");
                    }
                } else {
                    // No existing details, add new details
                    PersonalDetails newPersonalDetails = new PersonalDetails();
                    newPersonalDetails.setEmail(detailsCheck.getEmail());
                    newPersonalDetails.setName(detailsCheck.getName());
                    newPersonalDetails.setPhoneNumber(detailsCheck.getPhoneNumber());

                    // Assuming you have a method to save new details
                    PersonalDetailsDB.addPersonalDetails(newPersonalDetails);
                    detailsCheck.setDetailsComplete(true);
                    detailsCheck.setEmailVerified(true);
                    detailsCheck.setRespond("New personal details added successfully.");
                }

                // Send the response back to the client
                client.sendToClient(detailsCheck);
            } catch (Exception e) {
                throw new RuntimeException(e);
                // Handle exceptions and send error message back to client
//                detailsCheck.setDetailsComplete(false);
//                detailsCheck.setRespond("Server error: " + e.getMessage());
//                client.sendToClient(detailsCheck);
            }
        }*/

/***************************adan********************************/
        if (msg instanceof CancelOrderEvent) {
            CancelOrderEvent cancelEvent = (CancelOrderEvent) msg;
            String orderNumber = cancelEvent.getOrderNumber();
            Order order = OrdersDB.getOrderById(orderNumber);

            try {
                CancelOrderEvent event = new CancelOrderEvent(order, orderNumber);
                if (order == null) {
                    event.setStatus("Order not found");
                } else {
                    event.setStatus("Order found");
                }
                client.sendToClient(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (msg instanceof UpdateMealEvent) {
            UpdateMealEvent UpdateMealEvent = (UpdateMealEvent) msg;
            String mealId = UpdateMealEvent.getMealId();
            Meal meal = MealsDB.getMealById(mealId);

            try {
                UpdateMealEvent event = new UpdateMealEvent(meal, mealId);
                if (meal == null) {
                    event.setStatus("meal not found");
                } else {
                    event.setStatus("meal found");
                }
                client.sendToClient(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
                System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (msg instanceof ReportRequest) {

            ReportRequest reportRequest = (ReportRequest) msg;

            // Get the report type (revenue report in this case)
            String reportType = reportRequest.getReportType();
            String response = "";

            // Use a switch to handle different report types (we only have revenue report for now)
            switch (reportType) {
                case "revenueReport":
                    // Generate the revenue report (assuming 'month', 'restaurantName', and 'timeFrame' are part of the request)
                    RevenueReport revenueReport = new RevenueReport();
                    response = revenueReport.generate(reportRequest.getDate(), reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "revenue");
                    break;
                case "deliveryReport":
                    OrderTypeReport deliveryReport = new OrderTypeReport();
                    response = deliveryReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "DELIVERY");
                    break;
                case "pickupReport":
                    OrderTypeReport pickupReport = new OrderTypeReport();
                    response = pickupReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "PICKUP");
                    break;
                case "allOrdersReport":
                    OrderTypeReport allOrdersReport = new OrderTypeReport();
                    response = allOrdersReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "ALL");
                    break;

                // You can add more cases here for other report types in the future

                default:
                    response = "Invalid report type: " + reportType;
            }

            // Send the response message to the client (it can be a string with the report content)
            String message = "ReportResponse\n" + response;
            try {
                client.sendToClient(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (msg instanceof updatePrice) {
            System.out.println("Received update price message from client ");
            //sendToAllClients(msg);
            try {
                if(((updatePrice) msg).getPurpose().equals("changing")){
                    updatePrice req=(updatePrice)msg;
                    updateMealPriceInDatabase(req);
                    //String mealId = String.valueOf(req.getIdMeal());
                    deletePriceChangeReq(req.getIdMeal());
                    client.sendToClient(msg);
                    sendToAll(msg);
                } else if (((updatePrice) msg).getPurpose().equals("denying")) {
                    deletePriceChangeReq( ((updatePrice) msg).getIdMeal());
                    client.sendToClient(msg);
                    sendToAll(msg);

                } else{
                    MealUpdateRequest req= new MealUpdateRequest();
                    req=AddUpdatePriceRequest((updatePrice) msg);
                    client.sendToClient(req);
                    sendToAll(req);
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        else if (msg.toString().startsWith("add client")) {
            System.out.println("Adding client");
            Subscribers.add(client);
            SubscribedClient connection = new SubscribedClient(client);
            SubscribersList.add(connection);
            /*try {
				client.sendToClient(getmealEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }*/

            try {
                client.sendToClient("client added successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (msg.toString().startsWith("remove client")) {
            System.out.println("Deleting client");
            if (!Subscribers.isEmpty()) {
                Iterator<ConnectionToClient> iterator = Subscribers.iterator();
                while (iterator.hasNext()) {
                    ConnectionToClient subscribedClient = iterator.next();
                    if (subscribedClient.equals(client)) {
                        iterator.remove(); // Safe removal during iteration
                        break;
                    }
                }
            }
            if (!SubscribersList.isEmpty()) {
                for (SubscribedClient subscribedClient : SubscribersList) {
                    if (subscribedClient.getClient().equals(client)) {
                        SubscribersList.remove(subscribedClient);
                        break;
                    }
                }
            }
        }
        else if (msg instanceof SearchOptions) {
            try {
                System.out.println("*************************************");
                // Extract categories from the message
                SearchOptions options = (SearchOptions) msg;
                var categories = options.getRestaurantNames();

                System.out.println("Received SearchOptions message");
                System.out.println("Branch Name: " + options.getBranchName());
                System.out.println("Restaurant Names: " + options.getRestaurantNames());
                System.out.println("Customization Names: " + options.getCustomizationNames());

                // Retrieve current meals based on branch name
                List<Meal> currentMeals = getRestaurantByName(options.getBranchName()).getMeals();
                if(options.getBranchName().equals("all")){
                    currentMeals=getAllMeals();
                }
                System.out.println("Current meals for branch " + options.getBranchName() + ": " + currentMeals.size());

                // Handle reset option: Send all meals if no filters are applied
                if (options.getCustomizationNames().isEmpty() && options.getRestaurantNames().isEmpty()) {
                    System.out.println("No filters applied. Sending all meals.");
                    try {
                        client.sendToClient(currentMeals);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // List to store all meals after filtering
                List<Meal> allFilteredMeals = new ArrayList<>();

                // Search by restaurant
                for (String category : categories) {
                    System.out.println("Searching meals by restaurant: " + category);
                    List<Meal> meals = getMealsByRestaurant(category);
                    System.out.println("Meals found for restaurant " + category + ": " + meals.size());
                    allFilteredMeals.addAll(meals);  // Add these meals to the list
                }

                categories = options.getCustomizationNames();

                // Search by ingredients
                for (String category : categories) {
                    try {
                        System.out.println("Searching meals by ingredient: " + category);
                        List<Meal> meals = getMealsByIngredient(category);
                        System.out.println("Meals found with ingredient " + category + ": " + meals.size());
                        allFilteredMeals.addAll(meals);  // Add these meals to the list
                    } catch (Exception e) {
                        System.out.println("Error retrieving meals by ingredient: " + category);
                        e.printStackTrace();
                    }
                }

                // Combine the current meals with the filtered meals, but only keep those present in both lists
                List<Meal> combinedMeals = new ArrayList<>();

                System.out.println("Total meals in currentMeals: " + currentMeals.size());
                System.out.println("Total meals in allFilteredMeals: " + allFilteredMeals.size());
                // Use an iterator to safely remove elements while iterating
                Iterator<Meal> iterator = allFilteredMeals.iterator();
                while (iterator.hasNext()) {
                    Meal meal = iterator.next();
                    boolean flag = false;
                    for(Meal ml : currentMeals){
                        if(ml.getName().equals(meal.getName())){
                            flag = true;
                        }
                    }
                    if (!flag) {
                        iterator.remove(); // Safely remove the meal from allFilteredMeals
                    }
                }

                // Now, allFilteredMeals contains only meals that are also in currentMeals
                combinedMeals = allFilteredMeals;
                // Remove duplicates (based on name)
                // Step 2: Remove duplicate meals (based on name) from combinedMeals
                Set<String> uniqueMealNames = new HashSet<>();
                for (int i = 0; i < combinedMeals.size(); i++) {
                    Meal meal = combinedMeals.get(i);
                    if (!uniqueMealNames.add(meal.getName())) {
                        combinedMeals.remove(i); // Remove duplicate meal
                        i--; // Adjust the index after removal
                    }
                }
                System.out.println("Total meals in intersection (combinedMeals): " + combinedMeals.size());
                MealsList totalMeals = new MealsList(combinedMeals);
                // Send the combined meals list in a MealsList object to the client
                try {
                    System.out.println("Sending " + totalMeals.getMeals().size() + " meals to client.");
                    client.sendToClient(totalMeals);  // Send the combined meals list in one message
                } catch (Exception e) {
                    System.out.println("Error sending meals to client.");
                    e.printStackTrace();
                }
                System.out.println("*************************************");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg.toString().equals("Fetching SearchBy Options")) {
            try {
                // Fetch all restaurants
                List<Restaurant> restaurants = getAllRestaurants();
                List<String> restaurantNames = new ArrayList<>();
                for (Restaurant restaurant : restaurants) {
                    restaurantNames.add(restaurant.getRestaurantName());
                }

                // Fetch all customizations
                List<Customization> customizations = getAllCustomizations();
                List<String> customizationNames = new ArrayList<>();
                for (Customization customization : customizations) {
                    customizationNames.add(customization.getName());
                }

                // Remove duplicates (if necessary)
                restaurantNames = new ArrayList<>(new HashSet<>(restaurantNames));
                customizationNames = new ArrayList<>(new HashSet<>(customizationNames));

                // Create SearchOptions object with both restaurant and customization lists
                SearchOptions response = new SearchOptions(restaurantNames, customizationNames);

                // Send response back to client
                client.sendToClient(response);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg.toString().startsWith("DeleteMeal")){
            System.out.println("Deleting MEAL");
            String mealId = msgString.substring(6);
            int mealIdn = Integer.parseInt(msgString.replaceAll("\\D+", ""));
            System.out.println(mealIdn);
            String S=deleteMeal(mealIdn);
            System.out.println("ftt am7a dab3at esa");
            // Build structured message
            String response = "delete"+" "+mealId+" "+S;

            try {
                System.out.println("Sending " + response);
                client.sendToClient(response); // Send to requesting client
                sendToAll(response); // Broadcast to all clients
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (msg instanceof complainEvent) {
            //here we're adding new complain !!
            System.out.println("Received adding new complainEvent ");
            try {
                addComplainIntoDatabase((complainEvent) msg);
                client.sendToClient(msg);
                sendToAll(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (msg instanceof String && msgString.equals("show change price requests")){
            List<MealUpdateRequest> requests=null;
            try {
                requests = getAllRequestsWithMealDetails();
                PCRequestsList sending = new PCRequestsList(requests);
                client.sendToClient(sending);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Restaurant getRestaurantByName(String restaurantName) {
        Restaurant restaurant = null;

        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Create a query to fetch the restaurant by name
            Query<Restaurant> query = session.createQuery("FROM Restaurant WHERE restaurantName = :restaurantName", Restaurant.class);
            query.setParameter("restaurantName", restaurantName);

            // Execute the query and get the result
            restaurant = query.uniqueResult(); // Use uniqueResult() since restaurant names should be unique

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions (e.g., log the error or throw a custom exception)
        }

        return restaurant; // Returns null if no restaurant is found
    }

    private List<TableNode> getAvailableTables(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside) {
        List<TableNode> availableTables = new ArrayList<>();

        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch tables for the restaurant that match the inside/outside preference
            Query<TableNode> query = session.createQuery("SELECT t FROM TableNode t WHERE t.restaurant.restaurantName = :restaurantName AND t.isInside = :isInside", TableNode.class);
            query.setParameter("restaurantName", restaurantName);
            query.setParameter("isInside", isInside);
            List<TableNode> tables = query.getResultList();

            // Filter tables based on availability
            List<TableNode> availableForReservation = new ArrayList<>();
            for (TableNode table : tables) {
                // Initialize the collections within the session
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());

                if (isTableAvailable(table, reservationDateTime)) {
                    availableForReservation.add(table);
                }
            }

            // Sort tables by capacity in descending order
            availableForReservation.sort((t1, t2) -> Integer.compare(t2.getCapacity(), t1.getCapacity()));

            // Find the minimal number of tables to accommodate the seats
            availableTables = findMinimalTableCombination(availableForReservation, seats, reservationDateTime);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return availableTables;
    }

    private List<TableNode> findMinimalTableCombination(List<TableNode> tables, int seats, LocalDateTime reservationDateTime) {
        // Sort tables in descending order by capacity
        tables.sort((a, b) -> Integer.compare(b.getCapacity(), a.getCapacity()));

        List<TableNode> selectedTables = new ArrayList<>();
        int totalSeats = 0;

        // Iterate through the sorted tables and pick the fewest number to reach the required seats
        for (TableNode table : tables) {
            if (isTableAvailable(table, reservationDateTime)) { // Only consider available tables
                selectedTables.add(table);
                totalSeats += table.getCapacity();

                // Stop once we have enough seats
                if (totalSeats >= seats) {
                    break;
                }
            }
        }

        // If we couldn't fulfill the reservation, return an empty list
        if (totalSeats < seats) {
            return new ArrayList<>();
        }

        // Call the debug print function
        debugPrintTableSelection(selectedTables, seats, reservationDateTime);

        return selectedTables;
    }


    private void debugPrintTableSelection(List<TableNode> selectedTables, int requiredSeats, LocalDateTime reservationDateTime) {
        System.out.println("********************************************************");
        System.out.println("DEBUGGING TABLE SELECTION");
        System.out.println("Reservation Time: " + reservationDateTime);
        System.out.println("Required Seats: " + requiredSeats);

        // Check if selection was successful
        if (selectedTables.isEmpty()) {
            System.out.println("No valid table combination found!");
            System.out.println("********************************************************");
            return;
        }

        // Print the selected tables
        int totalSeatsAllocated = 0;
        System.out.println("\nSelected Tables:");
        for (TableNode table : selectedTables) {
            System.out.println("Table ID: " + table.getTableID() + ", Capacity: " + table.getCapacity() + ", Status: " + table.getStatus() + ", Available: " + isTableAvailable(table, reservationDateTime));
            totalSeatsAllocated += table.getCapacity();
        }

        // Print additional debugging details
        System.out.println("\nTotal Seats Allocated: " + totalSeatsAllocated);
        int wastedSeats = totalSeatsAllocated - requiredSeats;
        System.out.println("Wasted Seats: " + wastedSeats);

        System.out.println("********************************************************\n");
    }


    private boolean isTableAvailable(TableNode table, LocalDateTime reservationDateTime) {
        LocalDateTime endTime = reservationDateTime.plusHours(1); // 1.5 hours
        endTime = endTime.plusMinutes(30);
        for (LocalDateTime startTime : table.getReservationStartTimes()) {
            LocalDateTime existingEndTime = table.getReservationEndTimes().get(table.getReservationStartTimes().indexOf(startTime));
            // Check for overlap
            if (!reservationDateTime.isAfter(existingEndTime) && !endTime.isBefore(startTime)) {
                return false; // Table is not available
            }
        }

        return true; // Table is available
    }

    private void assignTablesToReservation(List<TableNode> tables, LocalDateTime reservationDateTime, boolean isInside) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            LocalDateTime endTime = reservationDateTime.plusHours(1); // Assume reservation lasts 1.5 hours
            endTime = endTime.plusMinutes(30);
            for (TableNode table : tables) {
                // Ensure the collections are initialized
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());

                // Update table status and reservation times
                table.setStatus("reserved");


                table.getReservationStartTimes().add(reservationDateTime);
                table.getReservationEndTimes().add(endTime);

                // Save the updated table
                session.update(table);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("i am in assigdTablesToReservation ");

    }

    private List<ReservationEvent> check_Available_Reservation(ReservationEvent reservation) {
        List<ReservationEvent> availableReservations = new ArrayList<>();

        try {
            String restaurantName = reservation.getRestaurantName();
            LocalDateTime requestedTime = reservation.getReservationDateTime();
            int requestedSeats = reservation.getSeats();
            boolean isInside = reservation.isInside();

            // Fetch restaurant opening and closing hours

            LocalTime openingTime = getRestaurantByName(restaurantName).getOpeningTime();
            LocalTime closingTime = getRestaurantByName(restaurantName).getClosingTime().minusHours(1); // One hour before closing

            // Validate that the requested reservation time is within business hours
            LocalTime requestedLocalTime = requestedTime.toLocalTime();
            if (requestedLocalTime.isBefore(openingTime) || requestedLocalTime.isAfter(closingTime)) {
                System.out.println("Requested time is outside of business hours!");
                return availableReservations; // Return empty list
            }

            // Fetch tables for the restaurant
            System.out.println("Fetching tables for restaurant: " + restaurantName);
            List<TableNode> tables = getTablesByRestaurant(restaurantName);
            System.out.println("Fetched tables: " + tables);

            // Define the time range (from requested time to one hour after)
            LocalTime startTime = requestedLocalTime;
            LocalTime endTime = startTime.plusHours(1);

            // Ensure end time does not exceed the last valid reservation slot
            if (endTime.isAfter(closingTime)) {
                endTime = closingTime;
            }
            System.out.println("Valid reservation time range: " + startTime + " to " + endTime);

            // Iterate over 15-minute time slots within the valid range
            for (LocalTime currentTimeSlot = startTime; currentTimeSlot.isBefore(endTime) || currentTimeSlot.equals(endTime); currentTimeSlot = currentTimeSlot.plusMinutes(15)) {
                if (currentTimeSlot.isBefore(openingTime) || currentTimeSlot.isAfter(closingTime)) {
                    System.out.println("Requested time is outside of business hours!");
                    continue;
                }
                LocalTime nextTimeSlot = currentTimeSlot.plusMinutes(15);
                boolean isAvailable = false;
                int totalAvailableSeats = 0;

                System.out.println("Checking time slot: " + currentTimeSlot + " to " + nextTimeSlot);

                for (TableNode table : tables) {
                    System.out.println("Checking table: " + table);

                    if (table.isInside() != isInside) continue; // Skip tables that don't match the preference

                    List<LocalDateTime> startTimes = table.getReservationStartTimes();
                    List<LocalDateTime> endTimes = table.getReservationEndTimes();
                    if (startTimes == null) startTimes = new ArrayList<>();
                    if (endTimes == null) endTimes = new ArrayList<>();

                    boolean slotOccupied = false;

                    for (int i = 0; i < startTimes.size(); i++) {
                        try {
                            LocalTime start = (startTimes.get(i) != null) ? startTimes.get(i).toLocalTime() : null;
                            LocalTime end = (endTimes.get(i) != null) ? endTimes.get(i).toLocalTime() : null;

                            if (start == null || end == null) continue;

                            if (!(nextTimeSlot.isBefore(start) || currentTimeSlot.isAfter(end))) {
                                slotOccupied = true;
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println("Error while checking reservation times: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    if (!slotOccupied) {
                        totalAvailableSeats += table.getCapacity();
                    }

                    if (totalAvailableSeats >= requestedSeats) {
                        isAvailable = true;
                        break;
                    }
                }

                if (isAvailable) {
                    LocalDateTime availableDateTime = LocalDateTime.of(requestedTime.toLocalDate(), currentTimeSlot);
                    ReservationEvent availableReservation = new ReservationEvent(restaurantName, availableDateTime, requestedSeats, isInside);
                    availableReservations.add(availableReservation);
                    System.out.println("Available reservation found: " + availableReservation);
                }
            }

            System.out.println("Available reservations: " + availableReservations);
        } catch (Exception e) {
            System.out.println("Exception occurred during the reservation availability check: " + e.getMessage());
            e.printStackTrace();
        }

        return availableReservations;
    }

//    //method to check if the reservation date is available
//    private boolean check_Available_Reservation(ReservationEvent reservation) {
//        String restaurantName = reservation.getRestaurantName();
//        LocalDateTime requestedTime = reservation.getReservationDateTime();
//
//        // Fetch tables for the given restaurant
//        List<TableNode> tables = getTablesByRestaurant(restaurantName);
//
//        for (TableNode table : tables) {
//            List<LocalDateTime> startTimes = table.getReservationStartTimes();
//            List<LocalDateTime> endTimes = table.getReservationEndTimes();
//
//            for (int i = 0; i < startTimes.size(); i++) {
//                LocalDateTime start = startTimes.get(i);
//                LocalDateTime end = endTimes.get(i);
//
//                // Check if the requested time overlaps with an existing reservation
//                if (!requestedTime.isBefore(start) && requestedTime.isBefore(end)) {
//                    return false; // The time slot is occupied
//                }
//            }
//        }
//
//        return true; // Reservation is available
//    }


    private List<TableNode> getTablesByRestaurant(String restaurantName) {
        List<Restaurant> result = new ArrayList<>();

        try (Session session = App.getSessionFactory().openSession()) { // Auto-closing session
            session.beginTransaction();

            // Correct the query by structuring the JOIN FETCH properly
            String queryString = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.tables WHERE r.restaurantName = :restaurantName";
            Query<Restaurant> query = session.createQuery(queryString, Restaurant.class);

            // Bind the named parameter
            query.setParameter("restaurantName", restaurantName);

            // Execute the query and retrieve the result
            result = query.getResultList();

            List<TableNode> tables = result.get(0).getTables();
            for (TableNode table : tables) {
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }

        if (result.isEmpty()) {
            return new ArrayList<>(); // Return an empty list if no restaurant is found
        }

        // Return the tables of the first matching restaurant
        return result.get(0).getTables();
    }


    // Method to get meals by restaurant
    private List<Meal> getMealsByRestaurant(String restaurantName) {
        // Fetch the restaurant with meals (avoid fetching tables in the same query)
        String query = "SELECT r FROM Restaurant r " +
                "LEFT JOIN FETCH r.meals " + // Fetch meals
                "WHERE r.restaurantName = :restaurantName";

        List<Restaurant> restaurants = App.Get_Restaurant(query, restaurantName);

        if (restaurants.isEmpty()) {
            System.out.println("No restaurant found with name: " + restaurantName);
            return new ArrayList<>(); // Return an empty list if no restaurant is found
        }

        // Get the restaurant
        Restaurant restaurant = restaurants.get(0);

        // Initialize the tables collection lazily (if needed)
        if (restaurant.getTables() != null) {
            Hibernate.initialize(restaurant.getTables()); // Force initialization of the tables collection
        }

        // Return the meals
        return restaurant.getMeals();
    }

    // Method to get meals by ingredient
    private List<Meal> getMealsByIngredient(String ingredient) throws Exception {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Get all meals (detached)
            var meals = MealsDB.GetAllMeals();

            // Create a list to store meals that contain the ingredient in the description
            List<Meal> mealsWithIngredient = new ArrayList<>();

            // Iterate over all meals and reattach them to the session
            for (Meal meal : meals) {
                session.update(meal); // Reattach the meal to the session
                Hibernate.initialize(meal.getCustomizations()); // Initialize the collection
                List<Customization> customizations = meal.getCustomizations();
                for (Customization customization : customizations) {
                    if (customization.getName().equals(ingredient)) {
                        mealsWithIngredient.add(meal);
                    }
                }
            }

            session.getTransaction().commit();
            return mealsWithIngredient;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void sendToAllClients(Object message) {
        try {
            for (SubscribedClient subscribedClient : SubscribersList) {
                subscribedClient.getClient().sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendToAll(Object message) {
        try {
            for (ConnectionToClient Subscriber : Subscribers) {
                Subscriber.sendToClient(message);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void printAllTablesInRestaurant(String restaurantName) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all tables for the specified restaurant
            Query<TableNode> query = session.createQuery("SELECT t FROM TableNode t WHERE t.restaurant.restaurantName = :restaurantName", TableNode.class);
            query.setParameter("restaurantName", restaurantName);
            List<TableNode> tables = query.getResultList();

            if (tables.isEmpty()) {
                System.out.println("No tables found for restaurant: " + restaurantName);
                return;
            }

            // Print attributes of each table
            for (TableNode table : tables) {
                // Initialize the lazy-loaded collections
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());

                System.out.println("Table ID: " + table.getTableID());
                System.out.println("Restaurant: " + table.getRestaurant().getRestaurantName());
                System.out.println("Is Inside: " + table.isInside());
                System.out.println("Capacity: " + table.getCapacity());
                System.out.println("Status: " + table.getStatus());

                // Print reservation start times
                System.out.println("Reservation Start Times:");
                for (LocalDateTime startTime : table.getReservationStartTimes()) {
                    System.out.println("  - " + startTime);
                }

                // Print reservation end times
                System.out.println("Reservation End Times:");
                for (LocalDateTime endTime : table.getReservationEndTimes()) {
                    System.out.println("  - " + endTime);
                }

                System.out.println("----------------------------------------");
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch tables for restaurant: " + restaurantName);
        }
    }

    public List<Restaurant> getAllRestaurants() {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all restaurants from the database
            Query<Restaurant> query = session.createQuery("FROM Restaurant", Restaurant.class);
            List<Restaurant> restaurants = query.getResultList();

            session.getTransaction().commit();

            // Return a copy of the list to avoid external modifications
            return new ArrayList<>(restaurants);
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return new ArrayList<>();
        }
    }

    public List<Complain> getAllComplainss() {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all the complains from the database
            Query<Complain> query = session.createQuery("FROM Complain ", Complain.class);
            List<Complain> COMPS = query.getResultList();

            session.getTransaction().commit();

            // Return a copy of the list to avoid external modifications
            return new ArrayList<>(COMPS);
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return new ArrayList<>();
        }
    }

    public List<Customization> getAllCustomizations() {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all customizations from the database
            Query<Customization> query = session.createQuery("FROM Customization", Customization.class);
            List<Customization> customizations = query.getResultList();

            session.getTransaction().commit();

            // Return a copy of the list to avoid external modifications
            return new ArrayList<>(customizations);
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return new ArrayList<>();
        }
    }

    private void saveReservationToDatabase(ReservationSave reservationSave) {
        try (Session session = App.getSessionFactory().openSession()) {
            // Begin a transaction
            session.beginTransaction();

            // Save the reservation
            session.save(reservationSave);

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Reservation saved successfully with ID: " + reservationSave.getReservationSaveID());
        } catch (Exception e) {

            System.err.println("Failed to save reservation to the database:");
            e.printStackTrace();
            throw new RuntimeException("Failed to save reservation to the database.", e);
        }
    }

    public void printAllReservationSaves() {
        Session session = App.getSessionFactory().openSession();

        try {
            session.beginTransaction();

            // Fetch all ReservationSave entities from the database
            List<ReservationSave> reservationSaves = session.createQuery("FROM ReservationSave", ReservationSave.class).list();

            // Print each ReservationSave entity
            System.out.println("*********************************************");
            System.out.println("All ReservationSave Entities in the Database:");
            System.out.println("*********************************************");
            for (ReservationSave reservationSave : reservationSaves) {
                System.out.println("ReservationSave ID: " + reservationSave.getReservationSaveID());
                System.out.println("Restaurant Name: " + reservationSave.getRestaurantName());
                System.out.println("Reservation Date and Time: " + reservationSave.getReservationDateTime());
                System.out.println("Number of Seats: " + reservationSave.getSeats());
                System.out.println("Is Inside: " + reservationSave.isInside());
                System.out.println("Full Name: " + reservationSave.getFullName());
                System.out.println("Phone Number: " + reservationSave.getPhoneNumber());
                System.out.println("Email: " + reservationSave.getEmail());

                // Print associated tables
                System.out.println("Associated Tables:");
                for (TableNode table : reservationSave.getTables()) {
                    System.out.println("  - Table ID: " + table.getTableID() + ", Capacity: " + table.getCapacity() + ", Status: " + table.getStatus());
                }
                System.out.println("---------------------------------------------");
            }

            session.getTransaction().commit();
        } catch (Exception e) {

            throw new RuntimeException("Failed to fetch and print ReservationSave entities.", e);
        }
    }

//    public List<Meal> getAllMeals() {
//        try (Session session = App.getSessionFactory().openSession()) {
//            session.beginTransaction();
//
//            // Fetch all meals from the database
//            Query<Meal> query = session.createQuery("FROM Meal", Meal.class);
//            List<Meal> meals = query.getResultList();
//
//            session.getTransaction().commit();
//
//            // Return a copy of the list to avoid external modifications
//            return new ArrayList<>(meals);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Return an empty list in case of an error
//            return new ArrayList<>();
//        }
//    }
    /********************adan*************************/
// Method to validate a CreditCardCheck object
    private boolean validateCreditCard(CreditCardCheck creditCardCheck) {
        // Assuming validation logic is based on basic checks for demonstration
        boolean isValidNumber = creditCardCheck.getCardNumber().matches("\\d{16}");
        boolean isValidCvv = creditCardCheck.getCvv().matches("\\d{3}");
        boolean isValidId = creditCardCheck.getCardholdersID().matches("\\d{9}");
        boolean isValidName = creditCardCheck.getCardholderName() != null && !creditCardCheck.getCardholderName().trim().isEmpty();

        return isValidNumber && isValidCvv && isValidId && isValidName;
    }
/********************************adan************************/
    /// ////work fine
    public List<MealUpdateRequest> getAllRequestsWithMealDetails() {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Use JOIN FETCH to load the Meal entity with the request
            Query<UpdatePriceRequest> query = session.createQuery(
                    "SELECT r FROM UpdatePriceRequest r JOIN FETCH r.meal",
                    UpdatePriceRequest.class
            );

            List<UpdatePriceRequest> requests = query.getResultList();

            session.getTransaction().commit();

            // Convert to MealUpdateRequest DTOs
            List<MealUpdateRequest> dtos = new ArrayList<>();
            for (UpdatePriceRequest request : requests) {
                Meal meal = request.getMeal();
                dtos.add(new MealUpdateRequest(
                        String.valueOf(meal.getId()) ,
                        meal.getName(),
                        meal.getDescription(),
                        meal.getImage(),
                        request.getOldPrice(),
                        request.getNewPrice()
                ));
            }

            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}