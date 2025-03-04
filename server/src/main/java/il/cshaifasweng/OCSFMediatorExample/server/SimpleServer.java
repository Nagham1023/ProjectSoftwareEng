package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.ReportRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

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

import static il.cshaifasweng.OCSFMediatorExample.server.ComplainDB.addComplainIntoDatabase;
import static il.cshaifasweng.OCSFMediatorExample.server.MealsDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.UsersDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.ReportDB.*;
import static il.cshaifasweng.OCSFMediatorExample.server.RestaurantDB.*;

import il.cshaifasweng.OCSFMediatorExample.server.RevenueReport;

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
                System.out.println("the opening hour is: "+start);
                System.out.println("the closing hour is: "+closingTime);
                System.out.println("the start time is: "+ startTime);
                System.out.println("*********************************************");


                while (startTime.toLocalTime().isBefore(closingTime)) {

                        // Check if tables are available for this time slot
                        System.out.println("*********************************************");
                        System.out.println("checking this tims slot: "+ startTime);
                        System.out.println("*********************************************");
                        List<TableNode> tablesForSlot = getAvailableTables(reservation.getRestaurantName(), startTime, reservation.getSeats(), reservation.isInside());

                        if (!tablesForSlot.isEmpty()) {
                            // If tables are available, add the time slot to the list
                            availableTimeSlots.add(startTime);
                        }else{
                            System.out.println("there is no availale tables for this slot -"+startTime);
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
                        DifferentResrvation available_Reservation_for_client = new DifferentResrvation(
                                reservation.getRestaurantName(), // Use the restaurant name from the event
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
                    LocalTime closingTime = getRestaurantByName(event.getRestaurantName()).getClosingTime();// 10:00 PM

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
                            ReservationEvent available_Reservation_for_client = new ReservationEvent(
                                    event.getRestaurantName(), // Use the restaurant name from the event
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
        if (msg instanceof mealEvent) {
            //here we're adding new meal !!
            //System.out.println("Received adding new mealEvent ");
            String addResult = AddNewMeal((mealEvent) msg);//if "added" then successed if "exist" then failed bcs there is a meal like that
            System.out.println("Added new mealEvent to the database");
            sendToAll(addResult);
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

        if (msg instanceof UserCheck) {
            System.out.println(((UserCheck) msg).getUsername());
            if (((UserCheck) msg).isState() == 1)//if login
            {
                try {
                    if (checkUser(((UserCheck) msg).getUsername(), ((UserCheck) msg).getPassword())) {
                        getUserInfo((UserCheck) msg); //to update it's info so we can save them.
                        ((UserCheck) msg).setRespond("Valid");
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
                    response = revenueReport.generate(reportRequest.getDate(), reportRequest.getTargetRestaurant(), reportRequest.getTimeFrame());
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
                updateMealPriceInDatabase((updatePrice) msg);
                client.sendToClient(msg);
                sendToAll(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else if (msg.toString().startsWith("add client")) {
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
        } else if (msg.toString().startsWith("remove client")) {
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
        } else if (msg.toString().startsWith("Sort")) {
            // Extract categories from the message
            var categories = processFilters(msg.toString());

            // List to store all meals after filtering
            List<Meal> allFilteredMeals = new ArrayList<>();

            // Search by restaurant
            for (String category : categories) {
                if (isRestaurant(category)) {
                    List<Meal> meals = getMealsByRestaurant(category);
                    allFilteredMeals.addAll(meals);  // Add these meals to the list
                }
            }

            // Search by ingredients
            for (String category : categories) {
                if (isIngredient(category)) {
                    try {
                        List<Meal> meals = getMealsByIngredient(category);
                        allFilteredMeals.addAll(meals);  // Add these meals to the list
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            // If any meals were found, send them in a single message
            if (!allFilteredMeals.isEmpty()) {
                try {
                    client.sendToClient(allFilteredMeals);  // Send all filtered meals in one batch
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No meals found for the given filters.");
            }

            // Handle reset case
            if (msg.toString().startsWith("Sort Reset")) {
                try {
                    var meals = MealsDB.GetAllMeals();
                    client.sendToClient(meals);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Unknown search method.");
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

    }

    public Restaurant getRestaurantByName(String restaurantName) {
        Restaurant restaurant = null;

        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Create a query to fetch the restaurant by name
            Query<Restaurant> query = session.createQuery(
                    "FROM Restaurant WHERE restaurantName = :restaurantName", Restaurant.class
            );
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
            Query<TableNode> query = session.createQuery(
                    "SELECT t FROM TableNode t WHERE t.restaurant.restaurantName = :restaurantName AND t.isInside = :isInside",
                    TableNode.class
            );
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
            System.out.println("Table ID: " + table.getTableID() +
                    ", Capacity: " + table.getCapacity() +
                    ", Status: " + table.getStatus() +
                    ", Available: " + isTableAvailable(table, reservationDateTime));
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
            for (LocalTime currentTimeSlot = startTime;
                 currentTimeSlot.isBefore(endTime) || currentTimeSlot.equals(endTime);
                 currentTimeSlot = currentTimeSlot.plusMinutes(15)) {

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


    public static List<String> processFilters(String filters) {
        List<String> categories = new ArrayList<>();

        // Split the message by line breaks
        String[] lines = filters.split("\n");

        // Iterate through each line and process the filters
        for (String line : lines) {
            if (line.isEmpty() || line.equals("Sort by")) {
                continue; // Skip empty lines or the "Sort by" line
            }

            // Add each valid filter line to the categories list
            categories.add(line.trim());
        }

        return categories;
    }

    // Method to check if the category is a restaurant
    public boolean isRestaurant(String category) {
        // List of known restaurants
        List<String> restaurants = List.of("Restaurant 1", "Restaurant 2", "Restaurant 3");
        return restaurants.contains(category);
    }

    // Method to check if the category is an ingredient
    public boolean isIngredient(String category) {
        // List of known ingredients
        List<String> ingredients = List.of("Lettuce", "Cheese", "Meat");
        return ingredients.contains(category);
    }

    // Method to get meals by restaurant
    private List<Meal> getMealsByRestaurant(String restaurantName) {
        String query = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.meals WHERE r.restaurant_Name = :restaurantName";
        List<Restaurant> restaurant = App.Get_Restaurant(query, restaurantName);
        return restaurant.get(0).getMeals();
    }

    // Method to get meals by ingredient
    private List<Meal> getMealsByIngredient(String ingredient) throws Exception {
        // Get all meals
        var meals = MealsDB.GetAllMeals();

        // Create a list to store meals that contain the ingredient in the description
        List<Meal> mealsWithIngredient = new ArrayList<>();

        // Iterate over all meals and check if the description contains the ingredient
        for (Meal meal : meals) {
            if (meal.getDescription() != null && meal.getDescription().toLowerCase().contains(ingredient.toLowerCase())) {
                mealsWithIngredient.add(meal);
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
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Fetch all tables for the specified restaurant
            Query<TableNode> query = session.createQuery(
                    "SELECT t FROM TableNode t WHERE t.restaurant.restaurantName = :restaurantName",
                    TableNode.class
            );
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


}