package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.ReportRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;

import java.time.*;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.CreditCardDetailsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.OrdersDB.*;
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


import javax.persistence.Table;

public class SimpleServer extends AbstractServer {

    private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
    private static ArrayList<ConnectionToClient> Subscribers = new ArrayList<>();
    private static boolean ReservationOperation = false;
    public static List<Restaurant> RestMealsList;
    public static List<Meal> allMeals;
    public static List<Customization> allcust;
    public static List<TableNode> allTables;
    public static List<ReservationSave> allSavedReservation;
    public static List<Users> allUsers = new ArrayList<>();
    public static List<PersonalDetails> allPersonalDetails = new ArrayList<>();
    public static List<CreditCard> allCreditCards = new ArrayList<>();

    private record ReservationPeriod(LocalDateTime start, LocalDateTime end) {}



    public SimpleServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Received message from client ");
        String msgString = msg.toString();
        System.out.println(msgString);

        //Reservation Management - omar
        if (msg instanceof ReservationEvent) {
            while(ReservationOperation) {}
            ReservationOperation = true;
            ReservationEvent reservation = (ReservationEvent) msg;
            List<ReservationEvent> availableReservation = check_Available_Reservation(reservation);
            if (!availableReservation.isEmpty()) {
                try {
                    client.sendToClient(availableReservation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Define restaurant opening and closing times
                LocalTime closingTime = getRestaurantByName(reservation.getRestaurantName()).getClosingTime();// 10:00 PM
                LocalTime start = getRestaurantByName(reservation.getRestaurantName()).getOpeningTime().plusMinutes(15); // e.g., 10:00 AM
                LocalDate currentDate = reservation.getReservationDateTime().toLocalDate(); // Get the current date
                LocalDateTime startTime = LocalDateTime.of(currentDate, start); // Combine date and time
                // Iterate through all time slots from the current time to the closing time
                List<LocalDateTime> availableTimeSlots = new ArrayList<>();
                while (startTime.toLocalTime().isBefore(closingTime.minusMinutes(30))) {
                    // Check if tables are available for this time slot
                    List<TableNode> tablesForSlot = getAvailableTables(reservation.getRestaurantName(), startTime, reservation.getSeats(), reservation.isInside());
                    if (!tablesForSlot.isEmpty() && startTime.isAfter(LocalDateTime.now())) {
                        // If tables are available, add the time slot to the list
                        availableTimeSlots.add(startTime);
                    }
                    // Move to the next time slot (e.g., increment by 0.5 hour)
                    startTime = startTime.plusMinutes(30);
                }
                if (!availableTimeSlots.isEmpty()) {
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
            ReservationOperation = false;
        }
        else if (msg instanceof FinalReservationEvent) {
            while(ReservationOperation) {}
            ReservationOperation = true;
            try {
                FinalReservationEvent event = (FinalReservationEvent) msg;

                // Print all the tables in the specified restaurant

                // Fetch available tables for the restaurant and time
                List<TableNode> availableTables = getAvailableTables(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside());

                if (availableTables.isEmpty()) {
                    System.out.println("*********************************************");
                    System.out.println("No available tables for the selected time and seats.");
                    System.out.println("Checking other time slots...");
                    System.out.println("*********************************************");

                    // Define restaurant opening and closing times
                    LocalTime closingTime = getRestaurantByName(event.getRestaurantName()).getClosingTime(); // 10:00 PM
                    LocalTime start = getRestaurantByName(event.getRestaurantName()).getOpeningTime().plusMinutes(15); // e.g., 10:00 AM
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
                }
                else {
                    System.out.println("*********************************************");
                    System.out.println("The available tables are: " + availableTables);
                    System.out.println("*********************************************");

                    // Assign tables to the reservation

//                    assignTablesToReservation(availableTables, event.getReservationDateTime(), event.isInside(), event.getRestaurantName());

                    // Create a new ReservationSave entity to save the reservation and tables
                    ReservationSave reservationSave = new ReservationSave(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside(), event.getFullName(), event.getPhoneNumber(), event.getEmail(), availableTables);

                    // Save the reservation to the database
                    //saveReservationToDatabase(reservationSave);
                    // Notify the client that the reservation was successful
                    System.out.println("Reservation confirmed successfully.");
                    //client.sendToClient("Reservation confirmed successfully.");
                    /// in zoom
                    System.out.println(" Step 2: go To payment check");
                    client.sendToClient(reservationSave);
                    //wait(500);
                    //sendToAll(new ReConfirmEvent());
                    //printAllReservationSaves();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Notify the client of the error
                System.out.println("Failed to save reservation.");
            }
            ReservationOperation = false;
        }
        else if (msg instanceof String && ((String) msg).startsWith("Cancel Reservation:")) {
            while(ReservationOperation) {}
            ReservationOperation = true;
            try{
                handleCancellationRequest((String) msg, client);
            }catch(Exception e){
                e.printStackTrace();
            }
            sendToAll(new ReConfirmEvent());
            ReservationOperation = false;

        }
        else if(msg instanceof FaildPayRes){
            try{
            client.sendToClient(msg);}
            catch(Exception e){
                e.printStackTrace();
            }
        }

        //Restaurant & Menu Operations - Yousef Adan Nagham Shada
        else if (msg instanceof String && msg.equals("getAllRestaurants")) {
            try {
                RestaurantList restaurantList = new RestaurantList();
                restaurantList.setRestaurantList(getAllRestaurants()); // Set list to send
                System.out.println(restaurantList.getRestaurantList());
                client.sendToClient(restaurantList); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof mealEvent) {
            //here we're adding new meal !!
            //System.out.println("Received adding new mealEvent ");
            Meal addResult = AddNewMeal((mealEvent) msg);//if "added" then successed if "exist" then failed bcs there is a meal like that
            System.out.println("Added new mealEvent to the database");
            ((mealEvent) msg).setMeal(addResult);
            sendToAll(msg);


        }
        else if (msg instanceof MealEventUpgraded) {
            MealEventUpgraded UpdateMealEvent = (MealEventUpgraded) msg;
            Meal addResult=AddNewMealUpgraded((MealEventUpgraded) msg);
            mealEvent messagee= new mealEvent(UpdateMealEvent.getMealName(), UpdateMealEvent.getPrice(), String.valueOf(addResult.getId()),addResult);
            sendToAll(messagee);
            //if (addResult != null)
            //sendToAll("added");
        }
        else if (msg instanceof UpdateMealRequest) {
            String addResult = updateMeal((UpdateMealRequest) msg);//if "added" then successed if "not exist" then failed bcs there is no meal like that
            System.out.println("Added new UpdateMealRequest to the database");
            sendToAll(msg);

        }
        else if (msg instanceof String && msgString.equals("toMenuPage")) {
            try {
                client.sendToClient(getmealEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else if (msg instanceof String && msgString.startsWith("menu")) {
            String branch = msgString.substring(4);
            System.out.println("getting a menu to " + branch);
            List<Meal> ml=null;
            try {
                if(branch.equals("ALL")) {
                    ml = MealsDB.GetAllMeals();
                }
                else{
                    ml = getmealsb(branch);
                }
                MealsList sending = new MealsList(ml);
                client.sendToClient(sending);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else if (msg instanceof UpdateMealEvent) {
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
        else if (msg.toString().startsWith("DeleteMeal")){
            System.out.println("Deleting MEAL");
            String mealId = msgString.substring(6);
            int mealIdn = Integer.parseInt(msgString.replaceAll("\\D+", ""));
            System.out.println(mealIdn);
            String S=deleteMeal(mealIdn);


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

        //User & Authentication - Yousef
        else if (msg instanceof UserCheck) {
            System.out.println(((UserCheck) msg).getUsername());
            if (((UserCheck) msg).isState() == 1)//if login
            {
                try {

                    signIn((UserCheck) msg);
                    client.sendToClient(msg);
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
            } else if (((UserCheck) msg).isState() == 3 || (((UserCheck) msg).isState() == 8)) {//if just a name check
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
            } else if (((UserCheck) msg).isState() == 4) { //if logout
                try {
                    //System.out.println("Logging out from the database");
                    SignOut((UserCheck) msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (((UserCheck) msg).isState() == 5) {//if changing the info
                try {
                    UpdateEmailAndPassword((UserCheck) msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (((UserCheck) msg).isState() == 9) {//Update in db
                try {
                    UsersDB.UpdateUser((UserCheck) msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else if (msg instanceof String && ((String)msg).equals("Get all users")) {
            System.out.println("Getting all users");
            //List<Users> users= UsersDB.getUsers();
            try {
                client.sendToClient(allUsers);
                //sendToAll(allUsers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(msg instanceof UserManagement){
            if(((UserManagement) msg).getMethod().equals("update")){
               // String response = UsersDB.UpdateUser((UserManagement) msg);
            }
            else {
                System.out.println("Invalid method BUT I WILL DELETE THE USER");
                try {
                    UsersDB.delete(((UserManagement) msg).getUsername());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                client.sendToClient((UserManagement) msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Payment Processing -Adan
        else if (msg instanceof PaymentCheck) {
            PaymentCheck paymentCheck = (PaymentCheck) msg;
            try {
                PersonalDetails personalDetailsDB = getPersonalDetailsByEmail(paymentCheck.getPersonalDetails().getEmail());
                CreditCard cc = getCreditCardDetailsByCardNumber(paymentCheck.getCreditCard().getCardNumber());
                CreditCard newCC = paymentCheck.getCreditCard();
                PersonalDetails newPD = paymentCheck.getPersonalDetails();

                if(paymentCheck.getMode().equals("Order")){
                    Order newOrder = paymentCheck.getOrder();
                    newOrder.setRestaurantId(getRestaurantIdByName(newOrder.getRestaurantName()));
                    if (cc == null) {


                        if (personalDetailsDB == null) {
                            //System.out.println("the personal details is null and cc is null");
                            newOrder.setCreditCard_num(newCC.getCardNumber());
                            paymentCheck.setResponse("Added the personal details and the Credit Card to the database");
                            addCreditCardDetails(newCC, newPD,newOrder);
                        }
                        else {
                            //System.out.println("the personal details is already added but cc is null");
                            paymentCheck.setResponse("Added the Credit Card to the database.");

                            newOrder.setCreditCard_num(newCC.getCardNumber());
                            addCreditCardToExistingPersonalDetails(newCC, personalDetailsDB,newOrder);
                        }
                        client.sendToClient(paymentCheck);
                    } else {
                        //System.out.println("not new credit card");
                        if(personalDetailsDB == null) {
                            //System.out.println("personal details null but cc is not null");
                            //System.out.println("new personal details");
                            newOrder.setCreditCard_num(cc.getCardNumber());

                            paymentCheck.setResponse("Added the personal details to the database");
                            addPersonalDetailsAndAssociateWithCreditCard(newPD, cc,newOrder);
                        }

                        else {
                            //System.out.println("not null both");
                            paymentCheck.setResponse("Updated the personal details to the database.");

                            newOrder.setCreditCard_num(cc.getCardNumber());
                            System.out.println("cc num is : " + cc.getCardNumber());
                            addCreditCardToPersonalDetailsIfBothExists(personalDetailsDB, cc, newOrder);
                        }
                        client.sendToClient(paymentCheck);
                    }
                }
                else {
                    ReservationSave newReservation= ((PaymentCheck) msg).getReservationEvent();
                    boolean result;
                    if (cc == null) {


                        if (personalDetailsDB == null) {
                            newReservation.setCreditCard_num(newCC.getCardNumber());
                            result= handleSavingReservation(newReservation, client);
                            if(result){
                                paymentCheck.setResponse("Added the personal details and the Credit Card to the database");
                                addCreditCardDetailsForRes(newCC, newPD,newReservation);}
                            else{
                                paymentCheck.setResponse("Payment Failed");
                            }
                        }
                        else {
                            //System.out.println("the personal details is already added but cc is null");
                            newReservation.setCreditCard_num(newCC.getCardNumber());
                            result= handleSavingReservation(newReservation, client);
                            if(result){
                                paymentCheck.setResponse("Added the Credit Card to the database.");
                                addCreditCardToExistingPersonalDetailsForRes(newCC, personalDetailsDB,newReservation);
                            }
                            else{
                                paymentCheck.setResponse("Payment Failed");
                            }

                        }
                        client.sendToClient(paymentCheck);
                    } else {
                        //System.out.println("not new credit card");
                        if(personalDetailsDB == null) {
                            //System.out.println("personal details null but cc is not null");
                            //System.out.println("new personal details");
                            newReservation.setCreditCard_num(cc.getCardNumber());
                            result= handleSavingReservation(newReservation, client);
                            if(result){
                                paymentCheck.setResponse("Added the personal details to the database");
                                addPersonalDetailsAndAssociateWithCreditCardForRes(newPD, cc,newReservation);
                            }
                            else{
                                paymentCheck.setResponse("Payment Failed");
                            }
                        }
                        else {
                            //System.out.println("not null both");
                            newReservation.setCreditCard_num(cc.getCardNumber());
                            result =handleSavingReservation(newReservation, client);
                            if(result){
                                paymentCheck.setResponse("Updated the personal details to the database.");
                                addCreditCardToPersonalDetailsIfBothExistsForRes(personalDetailsDB, cc,newReservation);
                            }
                            else{
                                paymentCheck.setResponse("Payment Failed");
                            }
                        }
                        client.sendToClient(paymentCheck);
                    }
                }
            } catch (Exception e) {
                paymentCheck.setResponse("Error processing request: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else if (msg instanceof PersonalDetails) {
            try {
                PersonalDetails personal = (PersonalDetails) msg;
                // Log the actual name of the PersonalDetails object
                System.out.println(personal.getName() + " simple server personal details");
                // Fetch personal details from the database based on the email
                PersonalDetails personalDetailsDB = getPersonalDetailsByEmail(personal.getEmail());
                if (personalDetailsDB != null) {
                    // Process credit card details if found
                    List<CreditCard> creditCards = personalDetailsDB.getCreditCardDetails();
                    if (creditCards != null && !creditCards.isEmpty()) {
                        System.out.println("in personal details simple server");
                        System.out.println(creditCards + " ^6666^^^^^^^^^");
                        ListOfCC loc= new ListOfCC(creditCards);
                        client.sendToClient(loc);  // Send credit card details back to the client
                    } else {
                        client.sendToClient("No credit card details found for " + personal.getEmail());
                    }
                } else {
                    client.sendToClient("No details found for email: " + personal.getEmail());
                }
            } catch (Exception e) {
                System.err.println("Error processing PersonalDetails: " + e.getMessage());
            }
        }

        //Order Management -Yousef Lamis Omar
        else if (msg instanceof CancelOrderEvent) {
            CancelOrderEvent cancelEvent = (CancelOrderEvent) msg;
            int orderNumber = Integer.parseInt(cancelEvent.getOrderNumber());
            Order order = OrdersDB.OrderById(orderNumber);
            String email= cancelEvent.getCustomerEmail();

            try {
                CancelOrderEvent event = new CancelOrderEvent(order, cancelEvent.getOrderNumber());
                if (order == null) {
                    event.setStatus("Order not found");
                } else {
                    if (order.getCustomerEmail().equals(email)) {
                        if(order.getOrderStatus().equals("Cancelled")) {
                            event.setStatus("Cancelled before");
                        }
                        else {
                            getOrderById(orderNumber);
                            event.setStatus("Order found");
                        }

                    }
                    else {
                        event.setStatus("Order not found with email: " + email);
                    }

                }
                client.sendToClient(event);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if (msg instanceof String && ((String) msg).startsWith("showorder")) {
            //System.out.println("im in show order");
            int orderNum = Integer.parseInt(((String) msg).substring(9)); // Extract from index 9 onwards
            try {
                Order order = OrderById(orderNum);
                if (order == null) {
                    System.out.println("Order not found.");
                    client.sendToClient("Order not found.");
                }
                else client.sendToClient(order);
                //System.out.println("sending back the order to the client "+order);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        //Complaints & Feedback - Hala, checked by: Yousef
        else if (msg instanceof complainEvent) {
            System.out.println("Received adding new complainEvent ");
            complainEvent ce = (complainEvent) msg;
            if(Objects.equals(ce.getKind(), "Complaint"))
            {
                Order order = OrderById(Integer.parseInt(ce.getOrderNum()));
                if(order == null)
                {
                    try {
                        client.sendToClient("No order!");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    if(order.getOrderStatus().equals("Cancelled")) {
                        try {
                            client.sendToClient("This order has been cancelled");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if(Objects.equals(order.getRestaurantName(), ce.getRestaurant().getRestaurantName())) {
                        addComplainIntoDatabase(ce,client);
                        sendToAll(msg);
                    }
                    else
                    {
                        try {
                            client.sendToClient("Not same restaurant!");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
            else {
                addComplainIntoDatabase(ce, client);
                sendToAll(msg);
            }
        }
        else if (msg instanceof String && msg.equals("getAllComplaints6")) {
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
                    //System.out.println("fill list 1");
                }

                // for complain amd done
                List<Complain> List2 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Complaint") && complain.getStatus().equals("Done"))
                        List2.add(complain);
                    //System.out.println("fill list 2");
                }
                // for Feedback and do
                List<Complain> List3 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Feedback") && complain.getStatus().equals("Do"))
                        List3.add(complain);
                    //System.out.println("fill list 3");
                }

                // for Feedback amd done
                List<Complain> List4 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Feedback") && complain.getStatus().equals("Done"))
                        List4.add(complain);
                    //System.out.println("fill list 4");
                }
                // for Suggestion and do
                List<Complain> List5 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Suggestion") && complain.getStatus().equals("Do"))
                        List5.add(complain);
                    //System.out.println("fill list 5");
                }

                // for Suggestion amd done
                List<Complain> List6 = new ArrayList<>();
                for (Complain complain : complainList) {
                    if (complain.getKind().equals("Suggestion") && complain.getStatus().equals("Done"))
                        List6.add(complain);
                    //System.out.println("fill list 6");
                }
                ListComplainList result = new ListComplainList(List1, List2, List3, List4, List5, List6);
                client.sendToClient(result); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof String && msg.equals("getAllComplaints")) {
            try {
                ComplainList complist = new ComplainList();
                complist.setComplainList(getAllComplains()); // Set list to send
                System.out.println(complist.getComplainList());
                client.sendToClient(complist); // send to client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof specificComplains) {
            try {
                specificComplains specificComplains = (specificComplains) msg;
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
        else if (msg instanceof updateResponse) {
            updateResponse response = (updateResponse) msg;
            //System.out.println("Received updateResponse from client: " + response.getnewResponse());


            //System.out.println("Sent updateResponse to all clients");

            //String subject = "Thanks For Contacting MAMA's Kitchen";
            //String email = response.getEmailComplain();
            //String body = response.getnewResponse();
            //EmailSender emailSender = new EmailSender();
            //emailSender.sendEmail(subject, body, email);
            //System.out.println("Email sent to: " + email);

            updateComplainResponseInDatabase(response);
            System.out.println("Updating Complain Response In Database");

            //client.sendToClient(msg);
        }

        //Reports & Analytics- Nagham Shada
        else if (msg instanceof ReportRequest) {

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
                    response = deliveryReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "Delivery");
                    break;
                case "pickupReport":
                    OrderTypeReport pickupReport = new OrderTypeReport();
                    response = pickupReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "Self PickUp");
                    break;
                case "allOrdersReport":
                    OrderTypeReport allOrdersReport = new OrderTypeReport();
                    response = allOrdersReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "ALL");
                    break;
                case "ComplainReport":
                    ComplainReport complainReport = new ComplainReport();
                    if(reportRequest.getTargetRestaurant().equals("ALL"))
                        response = complainReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "ALL");
                    else
                        response = complainReport.generate(reportRequest.getDate() , reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame(), "ONE");
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

        //Table & Reservation Status -Omar
        else if (msg.toString().startsWith("getTablesForRestaurant: ")) {
            try {
                // Extract the restaurant name from the message
                String restaurantName = msg.toString().substring("getTablesForRestaurant: ".length());

                // Call getTablesStatus with the extracted restaurant name
                tablesStatus tablesStatus = getTablesStatus(restaurantName);

                // Send the tablesStatus object back to the client
                client.sendToClient(tablesStatus);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (msg instanceof TableNode) {
            try {
                System.out.println("hellllllllo123");
                debugAllTablesDetails();
                client.sendToClient("table details: " + getTableDetails(((TableNode) msg).getTableID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Search & Filtering -Omar
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
                List<Meal> currentMeals;

                if (options.getBranchName().equals("ALL")) {
                    currentMeals = getAllMeals();
                }
                else{
                    currentMeals = getRestaurantByName(options.getBranchName()).getMeals();
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
                    for (Meal ml : currentMeals) {
                        if (ml.getName().equals(meal.getName())) {
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
                System.out.println("*");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg.toString().equals("Fetching SearchBy Options")) {
            try {
                // Fetch all restaurants
                List<Restaurant> restaurants;
                if(SimpleServer.RestMealsList == null)
                    restaurants = getAllRestaurants();
                else restaurants = SimpleServer.RestMealsList;
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

        //Price Updates- Nagham
        else if (msg instanceof updatePrice) {
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
        else if (msg instanceof String && msgString.equals("show change price requests")){
            List<MealUpdateRequest> requests=null;
            try {
                requests = getAllRequestsWithMealDetails();
                PCRequestsList sending = new PCRequestsList(requests);
                client.sendToClient(sending);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        //Real-Time Updates
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
        else if (msgString.startsWith("#warning")) {
            Warning warning = new Warning("Warning from server!");
            try {
                client.sendToClient(warning);
                System.out.format("Sent warning to client %s\n", client.getInetAddress().getHostAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        if (msg instanceof Order) {

            Order order = (Order) msg;
            //System.out.println("the message is new order");
            order.setRestaurantId(getRestaurantIdByName(order.getRestaurantName()));
            order.setOrderType("ss");
            for(MealInTheCart meal : order.getMeals())
                saveCustomizationsbool(meal.getMeal().getCustomizationsList());
            saveOrder(order);
            //System.out.println("The order has been saved");
        }*/
        else System.out.println("unknown message");
    }

    public Restaurant getRestaurantByName(String restaurantName) {
        // Search for the restaurant in the cached list
        for (Restaurant restaurant : RestMealsList) {
            if (restaurant.getRestaurantName().equalsIgnoreCase(restaurantName)) {
                return restaurant;
            }
        }

        // If not found, do nothing (no DB access)
        return null;
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

        return selectedTables;
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


    private void assignTablesToReservation(List<TableNode> tables, LocalDateTime reservationDateTime, boolean isInside, String restaurantName,ReservationSave reservation) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Get the closing time of the restaurant
            LocalTime closingHour = getRestaurantByName(restaurantName).getClosingTime();

            // Set the end time based on the reservation duration
            LocalDateTime endTime = reservationDateTime.plusHours(1); // Assume reservation lasts 1.5 hours
            endTime = endTime.plusMinutes(30);

            if (reservationDateTime.toLocalTime().plusHours(1).equals(closingHour) || reservationDateTime.toLocalTime().plusHours(1).isAfter(closingHour)) {
                endTime = reservationDateTime.plusHours(1); // Assume reservation lasts 1 hour
            } else {
                endTime = reservationDateTime.plusHours(1); // Default to 1.5 hours
                endTime = endTime.plusMinutes(30);
            }

            // Iterate through each table and update reservations
            for (TableNode table : tables) {
                // Ensure the collections are initialized
                Hibernate.initialize(table.getReservationStartTimes());
                Hibernate.initialize(table.getReservationEndTimes());

                // Add the reservation times
                table.getReservationStartTimes().add(reservationDateTime);
                table.getReservationEndTimes().add(endTime);

                // Save the updated table to the database
                session.update(table);

//                // Update the in-memory allTables list to keep it synchronized
//                if (allTables != null) {
//                    for (TableNode cachedTable : allTables) {
//                        if (cachedTable.getTableID() == table.getTableID()) {
//                            cachedTable.getReservationStartTimes().add(reservationDateTime);
//                            cachedTable.getReservationEndTimes().add(endTime);
//                            break;
//                        }
//                    }
//                }
            }
            session.save(reservation);
            allSavedReservation.add(reservation);
            // Commit the transaction to save changes to the database
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("i am in assignTablesToReservation");
    }




    private List<ReservationEvent> check_Available_Reservation(ReservationEvent reservation) {
        List<ReservationEvent> availableReservations = new ArrayList<>();

        try {
            String restaurantName = reservation.getRestaurantName();
            LocalDateTime requestedTime = reservation.getReservationDateTime();
            int requestedSeats = reservation.getSeats();
            boolean isInside = reservation.isInside();

            // Fetch restaurant opening and closing hours

            LocalTime openingTime = getRestaurantByName(restaurantName).getOpeningTime().plusMinutes(15);
            LocalTime closingTime = getRestaurantByName(restaurantName).getClosingTime().minusHours(1); // One hour before closing

            // Validate that the requested reservation time is within business hours
            LocalTime requestedLocalTime = requestedTime.toLocalTime();
            if (requestedLocalTime.isBefore(openingTime) || requestedLocalTime.isAfter(closingTime)) {
                return availableReservations; // Return empty list
            }

            // Fetch tables for the restaurant
            List<TableNode> tables = getTablesByRestaurant(restaurantName);

            // Define the time range (from requested time to one hour after)
            LocalTime startTime = requestedLocalTime;
            LocalTime endTime = startTime.plusHours(1);

            // Ensure end time does not exceed the last valid reservation slot
            if (endTime.isAfter(closingTime)) {
                endTime = closingTime;
            }

            // Iterate over 15-minute time slots within the valid range
//            for (LocalTime currentTimeSlot = startTime; currentTimeSlot.isBefore(endTime) || currentTimeSlot.equals(endTime); currentTimeSlot = currentTimeSlot.plusMinutes(15)) {
//                if (currentTimeSlot.isBefore(openingTime) || currentTimeSlot.isAfter(closingTime)) {
//                    System.out.println("Requested time is outside of business hours!");
//                    continue;
//                }
//                LocalTime nextTimeSlot = currentTimeSlot.plusMinutes(15);
//                boolean isAvailable = false;
//                int totalAvailableSeats = 0;
//
//                System.out.println("Checking time slot: " + currentTimeSlot + " to " + nextTimeSlot);
//
//                for (TableNode table : tables) {
//                    System.out.println("Checking table: " + table);
//
//                    if (table.isInside() != isInside) continue; // Skip tables that don't match the preference
//
//                    List<LocalDateTime> startTimes = table.getReservationStartTimes();
//                    List<LocalDateTime> endTimes = table.getReservationEndTimes();
//                    if (startTimes == null) startTimes = new ArrayList<>();
//                    if (endTimes == null) endTimes = new ArrayList<>();
//
//                    boolean slotOccupied = false;
//
//                    for (int i = 0; i < startTimes.size(); i++) {
//                        try {
//                            LocalTime start = (startTimes.get(i) != null) ? startTimes.get(i).toLocalTime() : null;
//                            LocalTime end = (endTimes.get(i) != null) ? endTimes.get(i).toLocalTime() : null;
//
//                            if (start == null || end == null) continue;
//
//                            if (!(nextTimeSlot.isBefore(start) || currentTimeSlot.isAfter(end))) {
//                                slotOccupied = true;
//                                break;
//                            }
//                        } catch (Exception e) {
//                            System.out.println("Error while checking reservation times: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                    if (!slotOccupied) {
//                        totalAvailableSeats += table.getCapacity();
//                    }
//
//                    if (totalAvailableSeats >= requestedSeats) {
//                        isAvailable = true;
//                        break;
//                    }
//                }
//
//                if (isAvailable) {
//                    LocalDateTime availableDateTime = LocalDateTime.of(requestedTime.toLocalDate(), currentTimeSlot);
//                    ReservationEvent availableReservation = new ReservationEvent(restaurantName, availableDateTime, requestedSeats, isInside);
//                    availableReservations.add(availableReservation);
//                    System.out.println("Available reservation found: " + availableReservation);
//                }
//            }
            for (LocalTime currentTimeSlot = startTime; currentTimeSlot.isBefore(endTime) || currentTimeSlot.equals(endTime); currentTimeSlot = currentTimeSlot.plusMinutes(15)) {

                // Skip if the time slot is outside business hours
                if (currentTimeSlot.isBefore(openingTime) || currentTimeSlot.isAfter(closingTime)) {

                    requestedTime = requestedTime.plusMinutes(15);
                    continue;
                }

                LocalTime nextTimeSlot = currentTimeSlot.plusMinutes(15);

                // Check for available tables in this time slot
                List<TableNode> tablesForSlot = getAvailableTables(restaurantName, requestedTime, requestedSeats, isInside);

                if (!tablesForSlot.isEmpty()) {
                    // If tables are available, create a reservation event
                    ReservationEvent availableReservation = new ReservationEvent(restaurantName, requestedTime, requestedSeats, isInside);
                    availableReservations.add(availableReservation);
                }
                requestedTime = requestedTime.plusMinutes(15);
            }

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
        // Ensure restaurant is available (but don't fetch from DB if missing)
        Restaurant restaurant = getRestaurantByName(restaurantName);
        if (restaurant == null) {
            return new ArrayList<>(); // No such restaurant, return empty list
        }

        // Filter and return tables that belong to the restaurant
        return allTables.stream()
                .filter(table -> table.getRestaurant().getRestaurantName().equalsIgnoreCase(restaurantName))
                .collect(Collectors.toList());
    }


    // Method to get meals by restaurant
    private List<Meal> getMealsByRestaurant(String restaurantName) throws Exception {

        for (Restaurant restaurant : RestMealsList) {
            if (restaurant.getRestaurantName().equalsIgnoreCase(restaurantName)) {
                return restaurant.getMeals();
            }
        }
        if(restaurantName.equals("ALL"))
            return getAllMeals();

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
        RestMealsList.add(restaurant);
        return restaurant.getMeals();
    }

    // Method to get meals by ingredient
    private List<Meal> getMealsByIngredient(String ingredient) throws Exception {
        // Get all meals (detached)
        List<Meal> meals = MealsDB.GetAllMeals();

        // Create a list to store meals that contain the ingredient in the description
        List<Meal> mealsWithIngredient = new ArrayList<>();

        // Iterate over all meals and reattach them to the session
        for (Meal meal : meals) {
            //session.update(meal); // Reattach the meal to the session
            //Hibernate.initialize(meal.getCustomizations()); // Initialize the collection
            Set<Customization> customizations = meal.getCustomizations();
            for (Customization customization : customizations) {
                if (customization.getName().equals(ingredient)) {
                    mealsWithIngredient.add(meal);
                }
            }
        }

        return mealsWithIngredient;
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
        try (Session session = getSessionFactory().openSession()) {
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
        if(RestMealsList != null) {
            System.out.println("the RestMeals is not null");
            return RestMealsList;
        }
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all restaurants from the database
            Query<Restaurant> query = session.createQuery("FROM Restaurant", Restaurant.class);
            List<Restaurant> restaurants = query.getResultList();

            session.getTransaction().commit();
            RestMealsList = restaurants;

            // Return a copy of the list to avoid external modifications
            return RestMealsList;
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return new ArrayList<>();
        }
    }

    public List<Complain> getAllComplainss() {
        try (Session session = getSessionFactory().openSession()) {
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

    public static List<Customization> getAllCustomizations() {
        if(allcust != null)
            return allcust;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();


            // Fetch all customizations from the database
            Query<Customization> query = session.createQuery("FROM Customization", Customization.class);
            List<Customization> customizations = query.getResultList();

            allcust = new ArrayList<>(customizations);
            System.out.println("printing all customizations");
            System.out.println(allcust);


            session.getTransaction().commit();


            return allcust;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<TableNode> getAllTables(String restaurantName) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch tables for the specified restaurant
            Query<TableNode> query = session.createQuery(
                    "FROM TableNode t WHERE t.restaurant.restaurantName = :restaurantName", TableNode.class
            );
            query.setParameter("restaurantName", restaurantName); // Set the restaurant name parameter
            List<TableNode> tables = query.getResultList();

            session.getTransaction().commit();

            // Return a copy of the list to avoid external modifications
            return new ArrayList<>(tables);
        } catch (Exception e) {
            e.printStackTrace();
            // Return an empty list in case of an error
            return new ArrayList<>();
        }
    }

    private void saveReservationToDatabase(ReservationSave reservationSave) {
        try (Session session = getSessionFactory().openSession()) {
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
        Session session = getSessionFactory().openSession();

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

    private String getTableDetails(int tableID) {
        StringBuilder details = new StringBuilder();
        TableNode table = allTables.stream()
                .filter(t -> t.getTableID() == tableID)
                .findFirst()
                .orElse(null);

        if (table == null) {
            return "Error: Table not found.";
        }

        // Basic table info
        appendSectionHeader(details, "Table Details");
        details.append(String.format("ID: %d\nRestaurant: %s\nLocation: %s\nCapacity: %d\nStatus: %s\n",
                table.getTableID(),
                table.getRestaurant().getRestaurantName(),
                table.isInside() ? "Inside" : "Outside",
                table.getCapacity(),
                table.getStatus().toUpperCase()));

        LocalDateTime now = LocalDateTime.now();
        List<LocalDateTime> starts = table.getReservationStartTimes() != null ?
                new ArrayList<>(table.getReservationStartTimes()) : Collections.emptyList();
        List<LocalDateTime> ends = table.getReservationEndTimes() != null ?
                new ArrayList<>(table.getReservationEndTimes()) : Collections.emptyList();

        if ("OCCUPIED".equalsIgnoreCase(table.getStatus())) {
            // Current reservation
            appendSectionHeader(details, "Current Reservation");
            findCurrentReservation(starts, ends, now)
                    .ifPresentOrElse(
                            period -> details.append(String.format("Started: %s\nEnds: %s\n",
                                    formatDateTime(period.start()),
                                    formatDateTime(period.end()))),
                            () -> details.append("No active reservation found\n")
                    );

            // Upcoming reservations
            appendSectionHeader(details, "Upcoming Reservations");
            appendReservationList(details, starts, ends, now, false);
        } else {
            // All future reservations
            appendSectionHeader(details, "Future Reservations");
            appendReservationList(details, starts, ends, now, true);
        }

        return details.toString();
    }

    // Helper methods
    private void appendReservationList(StringBuilder details,
                                       List<LocalDateTime> starts,
                                       List<LocalDateTime> ends,
                                       LocalDateTime now,
                                       boolean includeAllFuture) {
        if (starts.isEmpty()) {
            details.append("No reservations\n");
            return;
        }

        int count = 0;
        for (int i = 0; i < starts.size(); i++) {
            LocalDateTime start = starts.get(i);
            LocalDateTime end = i < ends.size() ? ends.get(i) : null;

            if (includeAllFuture ? start.isAfter(now) : start.isAfter(now)) {
                details.append(String.format("  %d. %s", ++count, formatReservationPeriod(start, end)));
            }
        }

        if (count == 0) {
            details.append("No upcoming reservations\n");
        }
    }

    private Optional<ReservationPeriod> findCurrentReservation(List<LocalDateTime> starts,
                                                               List<LocalDateTime> ends,
                                                               LocalDateTime now) {
        for (int i = 0; i < starts.size(); i++) {
            LocalDateTime start = starts.get(i);
            LocalDateTime end = i < ends.size() ? ends.get(i) : null;

            if ((start.isBefore(now) || start.isEqual(now)) &&
                    (end == null || end.isAfter(now))) {
                return Optional.of(new ReservationPeriod(start, end));
            }
        }
        return Optional.empty();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ofPattern("EEE, MMM dd HH:mm")) :
                "Not specified";
    }

    private String formatReservationPeriod(LocalDateTime start, LocalDateTime end) {
        return String.format("%s - %s\n",
                formatDateTime(start),
                end != null ? formatDateTime(end) : "Open-ended");
    }

    private void appendSectionHeader(StringBuilder sb, String title) {
        sb.append("\n--- ").append(title).append(" ---\n");
    }

    // CORRECTED helper method (was missing end parameter)
    private String formatReservationPeriod1(LocalDateTime start, LocalDateTime end) {
        return String.format("%s - %s",
                formatDateTime(start),
                end != null ? formatDateTime(end) : "Open-ended");
    }

    // UPDATED debug method (fixed parameter passing)
    public void debugAllTablesDetails() {
        if (allTables.isEmpty()) {
            System.out.println("[DEBUG] No tables found in the system");
            return;
        }

        System.out.println("\n=== DEBUG ALL TABLES (" + allTables.size() + " tables) ===");

        for (int i = 0; i < allTables.size(); i++) {
            TableNode table = allTables.get(i);
            System.out.println("\n--- Table " + (i+1) + " of " + allTables.size() + " ---");
            System.out.println("ID: " + table.getTableID());
            System.out.println("Restaurant: " + table.getRestaurant().getRestaurantName());
            System.out.println("Location: " + (table.isInside() ? "Inside" : "Outside"));
            System.out.println("Capacity: " + table.getCapacity());
            System.out.println("Status: " + table.getStatus().toUpperCase());

            List<LocalDateTime> starts = table.getReservationStartTimes();
            List<LocalDateTime> ends = table.getReservationEndTimes();

            System.out.println("\nRaw reservation starts: " + starts);
            System.out.println("Raw reservation ends: " + ends);

            System.out.println("Formatted reservations:");
            for (int j = 0; j < starts.size(); j++) {
                LocalDateTime endTime = j < ends.size() ? ends.get(j) : null;
                String status = getReservationStatus(starts.get(j), endTime, LocalDateTime.now());

                System.out.println(String.format("  %d. %s (%s)",
                        j+1,
                        formatReservationPeriod1(starts.get(j), endTime),  // Fixed parameter passing
                        status));
            }
        }
        System.out.println("\n=== END DEBUG - " + allTables.size() + " tables shown ===\n");
    }

    private String getReservationStatus(LocalDateTime start, LocalDateTime end, LocalDateTime now) {
        if (start.isAfter(now)) return "UPCOMING";
        if (end != null && end.isAfter(now)) return "ACTIVE";
        return "COMPLETED";
    }

    public tablesStatus getTablesStatus(String restaurantName) {
        try {
            // Step 1: Filter tables by restaurant name from in-memory list
            List<TableNode> tables = allTables.stream()
                    .filter(table -> table.getRestaurant().getRestaurantName().equalsIgnoreCase(restaurantName))
                    .collect(Collectors.toList());

            // Step 2: Extract statuses using the new logic (getTodayOrCurrentStatus)
            List<String> statuses = tables.stream()
                    .map(TableNode::getTodayStatus)
                    .collect(Collectors.toList());

            // Step 3: Return the status object
            return new tablesStatus(tables, statuses);

        } catch (Exception e) {
            e.printStackTrace();
            return new tablesStatus(new ArrayList<>(), new ArrayList<>());
        }
    }




    /********************adan*************************/
// Method to validate a CreditCardCheck object
    /*private boolean validateCreditCard(CreditCardCheck creditCardCheck) {
        // Assuming validation logic is based on basic checks for demonstration
        boolean isValidNumber = creditCardCheck.getCardNumber().matches("\\d{16}");
        boolean isValidCvv = creditCardCheck.getCvv().matches("\\d{3}");
        boolean isValidId = creditCardCheck.getCardholdersID().matches("\\d{9}");
        boolean isValidName = creditCardCheck.getCardholderName() != null && !creditCardCheck.getCardholderName().trim().isEmpty();

        return isValidNumber && isValidCvv && isValidId && isValidName;
    }*/
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
    private void handleCancellationRequest(String msg, ConnectionToClient client)throws Exception{
        try {
            // Parse the message
            String data = msg.substring("Cancel Reservation:".length()).trim();
            String[] parts = data.split(",");

            if (parts.length != 2) {
                client.sendToClient("Error: Invalid cancellation request format");
                return;
            }

            String name = parts[0].trim();
            int reservationId;

            try {
                reservationId = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                client.sendToClient("Error: Reservation ID must be a number");
                return;
            }

            // Process cancellation
            String result = cancelReservation(name, reservationId);
            client.sendToClient("Cancel Reservation " + result);

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient("Error: " + e.getMessage());
        }
    }

    public String cancelReservation(String name, int reservationId) {
        if(allSavedReservation==null){
            App.fetching_reservation();
        }
        // Step 1: Find the reservation in memory
        ReservationSave reservationToCancel = null;

        for (ReservationSave reservation : allSavedReservation) {
            if (reservation.getReservationSaveID() == reservationId) {
                reservationToCancel = reservation;
                break;
            }
        }

        if (reservationToCancel == null) {
            return "Error: No reservation found with ID " + reservationId;
        }

        if (!reservationToCancel.getFullName().equalsIgnoreCase(name)) {
            return "Error: Name doesn't match the reservation";
        }

        // Step 2: Check if the reservation is in the future
        LocalDateTime now = LocalDateTime.now();
        if (reservationToCancel.getReservationDateTime().isBefore(now)) {
            return "Error: Cannot cancel past reservation";
        }

        // Step 3: Free up the tables in memory
        for (TableNode table : reservationToCancel.getTables()) {
            for (TableNode t : allTables) {
                if (t.getTableID() == table.getTableID()) {
                    List<LocalDateTime> starts = t.getReservationStartTimes();
                    List<LocalDateTime> ends = t.getReservationEndTimes();

                    for (int i = 0; i < starts.size(); i++) {
                        if (starts.get(i).equals(reservationToCancel.getReservationDateTime())) {
                            starts.remove(i);
                            ends.remove(i);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        // Step 4: Remove from memory
        allSavedReservation.remove(reservationToCancel);

        // Step 5: Remove from database
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            ReservationSave dbReservation = session.get(ReservationSave.class, reservationId);
            if (dbReservation != null) {
                session.delete(dbReservation);
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            return "Error: Failed to remove from database - " + e.getMessage();
        }

        // Step 6: Send email confirmation
        int charge = calculateCancellationCharge(reservationToCancel, now);
        String emailContent = buildCancellationEmail(reservationToCancel, charge);
        EmailSender.sendEmail("Reservation Cancellation Confirmation", emailContent, reservationToCancel.getEmail());

        return charge > 0 ?
                "Success: Cancelled with charge of " + charge + " ILS" :
                "Success: Cancelled with no charge";
    }


    private String buildCancellationEmail(ReservationSave reservation, int charge) {
        return String.format(
                "Dear %s,\n\n" +
                        "We confirm your reservation cancellation at %s:\n\n" +
                        "Reservation Details:\n" +
                        "- Date/Time: %s\n" +
                        "- Number of Guests: %d\n" +
                        "- Cancellation Processed: %s\n\n" +
                        "%s\n\n" + // Charge information
                        "Cancellation Policy:\n" +
                        "- Cancellations within 1 hour of reservation incur 10 ILS per seat\n\n" +
                        "Thank you for your patronage. We hope to serve you again soon.\n\n" +
                        "Best regards,\n" +
                        "Customer Service Team\n" +
                        "%s",
                reservation.getFullName(),
                reservation.getRestaurantName(),
                reservation.getReservationDateTime().toString(),
                reservation.getSeats(),
                LocalDateTime.now().toString(),
                charge > 0 ? String.format("Cancellation Charge: %d ILS", charge)
                        : "No cancellation charges applied",
                reservation.getRestaurantName()
        );
    }

    private int calculateCancellationCharge(ReservationSave reservation, LocalDateTime cancellationTime) {
        // Check if cancellation is within 1 hour of reservation time
        LocalDateTime reservationTime = reservation.getReservationDateTime();
        Duration timeDifference = Duration.between(cancellationTime, reservationTime);

        // If cancellation is within 1 hour before reservation
        if (!timeDifference.isNegative() && timeDifference.toHours() < 1) {
            // Charge 10 ILS per seat
            return reservation.getSeats() * 10;
        }
        return 0;
    }

    private List<ReservationSave> findReservations(Session session, String name,
                                                   String phone, String email) {
        return session.createQuery(
                        "FROM ReservationSave r WHERE " +
                                "r.fullName = :name AND " +
                                "r.phoneNumber = :phone AND " +
                                "r.email = :email", ReservationSave.class)
                .setParameter("name", name)
                .setParameter("phone", phone)
                .setParameter("email", email)
                .getResultList();
    }

    private void cancelSingleReservation(Session session, ReservationSave reservation) {
        // Initialize lazy-loaded collections
        Hibernate.initialize(reservation.getTables());

        // Process each table in the reservation
        for (TableNode table : reservation.getTables()) {
            // Initialize time collections
            Hibernate.initialize(table.getReservationStartTimes());
            Hibernate.initialize(table.getReservationEndTimes());

            // Remove the reservation times
            removeReservationTimes(table, reservation.getReservationDateTime());

            // Update table status
            table.setStatus(table.getStatus()); // Triggers status recalculation
            session.update(table);
        }

        // Delete the reservation
        session.delete(reservation);
    }

    private void removeReservationTimes(TableNode table, LocalDateTime reservationTime) {
        List<LocalDateTime> starts = table.getReservationStartTimes();
        List<LocalDateTime> ends = table.getReservationEndTimes();

        // Find matching time slot (assuming exact match on start time)
        for (int i = 0; i < starts.size(); i++) {
            if (starts.get(i).equals(reservationTime)) {
                starts.remove(i);
                ends.remove(i);
                break;
            }
        }
    }
    private boolean handleSavingReservation(ReservationSave reservation, ConnectionToClient client) {
        FinalReservationEvent event = new FinalReservationEvent(reservation.getRestaurantName(), reservation.getReservationDateTime(), reservation.getSeats()
                , reservation.isInside(), reservation.getFullName(), reservation.getPhoneNumber(), reservation.getEmail());

        // Print all the tables in the specified restaurant
        //printAllTablesInRestaurant(event.getRestaurantName());

        // Fetch available tables for the restaurant and time
        List<TableNode> availableTables = getAvailableTables(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside());
        try{
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
                System.out.println("*********************************************");
                System.out.println("hello");

                System.out.println("*********************************************");
                while (startTime.toLocalTime().isBefore(closingTime)) {
                    // Check if tables are available for this time slot
                    System.out.println("hello1");
                    List<TableNode> tablesForSlot = getAvailableTables(event.getRestaurantName(), startTime, event.getSeats(), event.isInside());
                    System.out.println("hello2");

                    if (!tablesForSlot.isEmpty()) {
                        // If tables are available, add the time slot to the list
                        availableTimeSlots.add(startTime);
                    }
                    System.out.println("hello3");

                    // Move to the next time slot (e.g., increment by 1 hour)
                    startTime = startTime.plusMinutes(15);
                    System.out.println("hello4");
                }
                System.out.println("*********************************************");
                System.out.println("helllllllo");

                System.out.println("*********************************************");
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
                        FaildPayRes object = new FaildPayRes(reservationList);
                        // Send the available reservation to the client
                        client.sendToClient(object);
                        return false;
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
                    FaildPayRes object = new FaildPayRes(reservationList);
                    try {
                        client.sendToClient(object);
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                System.out.println("*********************************************");
                System.out.println("The available tables are: " + availableTables);
                System.out.println("*********************************************");

                // Assign tables to the reservation and save the reservatio in the DB

                assignTablesToReservation(availableTables, event.getReservationDateTime(), event.isInside(), event.getRestaurantName(),reservation);

                // Create a new ReservationSave entity to save the reservation and tables
                //ReservationSave reservationSave = new ReservationSave(event.getRestaurantName(), event.getReservationDateTime(), event.getSeats(), event.isInside(), event.getFullName(), event.getPhoneNumber(), event.getEmail(), availableTables);

                // Notify the client that the reservation was successful
                System.out.println("Reservation confirmed successfully.");
                //client.sendToClient("Reservation confirmed successfully.");
                /// in zoom

                try {
                    System.out.println("wa Save the reservation after payment");
                    client.sendToClient(reservation);
                    wait(500);
                    sendToAll(new ReConfirmEvent());
                    return true;
                    //printAllReservationSaves();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Notify the client of the error
                    System.out.println("Failed to save reservation.");
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public List<TableNode> fetchAvailableTables(String restaurantName, LocalDateTime time, int seats, boolean isInside) {
        return allTables.stream()
                .filter(t -> t.getRestaurant().getRestaurantName().equals(restaurantName)
                        && t.getCapacity() >= seats
                        && t.isInside() == isInside
                        && isTableAvailable(t, time))
                .collect(Collectors.toList());
    }

    private List<TableNode> getAvailableTables(String restaurantName, LocalDateTime reservationDateTime, int seats, boolean isInside) {
        // Filter tables by restaurant name and inside/outside preference
        List<TableNode> matchingTables = allTables.stream()
                .filter(t -> t.getRestaurant().getRestaurantName().equals(restaurantName)
                        && t.isInside() == isInside)
                .collect(Collectors.toList());

        // Filter by availability
        List<TableNode> availableForReservation = matchingTables.stream()
                .filter(t -> isTableAvailable(t, reservationDateTime))
                .sorted((t1, t2) -> Integer.compare(t2.getCapacity(), t1.getCapacity())) // Sort descending by capacity
                .collect(Collectors.toList());

        // Find best table combination
        return findMinimalTableCombination(availableForReservation, seats, reservationDateTime);
    }

}