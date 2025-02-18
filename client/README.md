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

