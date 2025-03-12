# Adding a New Function to the Worker

## Steps to Add a New Function

### 1. Create the User Interface (FXML)
First, design the screen that will allow the user to perform the function. Create an FXML file for this screen inside the appropriate directory. Ensure that the layout and necessary UI components (buttons, text fields, etc.) are properly set up.

### 2. Define the Controller for the Screen
Create a controller class for the newly created FXML file. In this controller, implement the logic of the function.
- If the function requires data from the database, you must request it from the server by sending a message.

### 3. Update the Server-Side Handling
Navigate to the server-side request handler (e.g., `ServerHandler.java`) and add a new case in the request-handling logic to recognize and process this function.
- Implement the required operations (e.g., querying the database, updating records, etc.).
- Once the processing is complete, send a response back to the client with the suitable message.

### 4. Modify the Client-Side Handling
When the server sends back a response, the client must properly handle it. To do this:
- Ensure that the client receives the message and forwards the result to the controller using `eventBus`.
- Create a suitable event class that implements `Serializable`.

### 5. Subscribe to the Event in the Controller
In the controller, define a function that subscribes to the event and processes the result after the client posts it. Ensure that the event is handled correctly, updating the UI or executing additional logic as needed.

### 6. Link the Function to the Worker Controller
In the `WorkerController` class, all necessary buttons are already defined. Follow these steps:
- Find the button related to your function.
- Modify the `onButtonClick` function to handle your new function.
- Locate the `switch` statement inside `onButtonClick`. The `case` must match the **~~label of the button~~** .
- Add a new `case` and call the function:

  ```java
  //SwitchScreen("YourFxmlFile"); // Write it without .fxml
  ```

### 7. Final Checks
- Ensure all messages between the client and server are correctly formatted and handled.
- Verify that the event subscription and processing logic work correctly.
- Test the function thoroughly to confirm proper integration.

By following these steps, you will successfully add a new function to the Worker module in a structured and maintainable way.



# Restaurant Reservation System

## Overview

This project implements a reservation system for a restaurant. The system allows users to make a reservation by selecting a date and time, and the server finds the relevant available tables for the reservation. The system also handles user details like name, phone number, and email, and sends a verification email for the reservation. If there are no available tables at the selected time, it suggests other available times for the reservation.

## System Components

### Classes

1. **ReservationEvent**
   - This class represents the event when a user enters the reservation details, including the selected date and time. It is used to send reservation information to the server for processing.
   - **Functionality**: 
     - Collects the reservation date and time.
     - Sends the details to the server for table search in the specified time interval.

2. **FinalReservationEvent**
   - This class is used once the user selects a reservation from the list of available tables. The user also enters their personal details such as name, phone number, and email.
   - **Functionality**:
     - Sends the final reservation details to the server for email verification.
     - Contains the user’s name, phone, and email.

3. **DifferentReservation**
   - This class is used when no reservation is available at the selected time. The server looks for other available times for the selected date and returns them.
   - **Functionality**:
     - Contains different reservation times for the user to choose from when the preferred time is unavailable.

4. **SaveReservation**
   - This class is responsible for saving the finalized reservation and associating it with the relevant tables in the database.
   - **Functionality**:
     - Saves the final reservation to the database.
     - Maps the reservation to the relevant tables.

## Reservation Process

1. **User Inputs Reservation Details**
   - The user selects the reservation date and time. The details are sent to the server.
   
2. **Server Searches for Available Tables**
   - The server searches for available tables in the specified time window (selected hour to +1 hour).
   - If available tables are found, the server sends a list of available reservations to the client.
   
3. **User Chooses Final Reservation**
   - The user selects a reservation from the available options.
   - The user then enters their name, phone number, and email.

4. **Email Verification**
   - A verification email is sent to the user.
   
5. **Server Saves Final Reservation**
   - The server receives the `FinalReservationEvent` and saves the reservation to the database. The tables for the reservation are also recorded.

6. **Handling No Available Reservations**
   - If no reservations are available at the selected time, the system searches for available reservations at different times for the same date. 
   - If no reservations are available at all, an empty reservation is sent back to the client, and the user is notified.
   
7. **Different Reservation Times**
   - If there are different available times for the selected date, a `DifferentReservation` entity is created with the available options and sent to the client.

## Search By Functionality

- The system also supports searching for meals based on various options such as restaurant name and customizations.
- When the menu is loaded, the system loads search options, fetching available restaurants and meal customizations from the database.
- Once the user selects the search options and presses the "Search By" button, a `SearchOptions` entity is sent to the server.
- The server filters the available meals based on the selected options and sends back a filtered list of meals in a `MealList` entity, which the client displays to the user.

## Classes Involved in Search By

1. **SearchOptions**
   - Contains the user's selected search criteria (restaurant name, customization, etc.).

2. **MealList**
   - Represents the list of filtered meals based on the search options selected by the user.




