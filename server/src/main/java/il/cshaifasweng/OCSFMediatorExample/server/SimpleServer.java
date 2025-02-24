package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.ReportRequest;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;

import java.time.LocalDateTime;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

		ReservationEvent reservation;
		if (msg instanceof ReservationEvent) {
			reservation = (ReservationEvent) msg;
			if (check_Available_Reservation(reservation)) {

			}
		}
		if (msg instanceof mealEvent) {
			if (msg instanceof String && msg.equals("getAllRestaurants")) {
				try {
					RestaurantDB restaurantDB = new RestaurantDB(); // create new instance of dataBase manager
					RestaurantList restaurantList = new RestaurantList();
					restaurantList.setRestaurantList(restaurantDB.getAllRestaurants()); // Set list to send
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
	}
		//method to check if the reservation date is available
		private boolean check_Available_Reservation (ReservationEvent reservation){
			String restaurantName = reservation.getRestaurantName();
			LocalDateTime requestedTime = reservation.getReservationDateTime();

			// Fetch tables for the given restaurant
			List<TableNode> tables = getTablesByRestaurant(restaurantName);

			for (TableNode table : tables) {
				List<LocalDateTime> startTimes = table.getReservationStartTimes();
				List<LocalDateTime> endTimes = table.getReservationEndTimes();

				for (int i = 0; i < startTimes.size(); i++) {
					LocalDateTime start = startTimes.get(i);
					LocalDateTime end = endTimes.get(i);

					// Check if the requested time overlaps with an existing reservation
					if (!requestedTime.isBefore(start) && requestedTime.isBefore(end)) {
						return false; // The time slot is occupied
					}
				}
			}

			return true; // Reservation is available
		}
		// Method to get tables by restaurant
		private List<TableNode> getTablesByRestaurant (String restaurantName){
			String query = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.tables WHERE r.restaurantName = :restaurantName";
			List<Restaurant> restaurantList = App.Get_Restaurant(query);

			if (restaurantList.isEmpty()) {
				return new ArrayList<>(); // Return an empty list if no restaurant is found
			}

			// Return the tables from the first restaurant
			return restaurantList.get(0).getTables();
		}


		public static List<String> processFilters (String filters){
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
		public boolean isRestaurant (String category){
			// List of known restaurants
			List<String> restaurants = List.of("Restaurant 1", "Restaurant 2", "Restaurant 3");
			return restaurants.contains(category);
		}

		// Method to check if the category is an ingredient
		public boolean isIngredient (String category){
			// List of known ingredients
			List<String> ingredients = List.of("Lettuce", "Cheese", "Meat");
			return ingredients.contains(category);
		}
		// Method to get meals by restaurant
		private List<Meal> getMealsByRestaurant (String restaurantName){


			String query = "SELECT r FROM Restaurant r LEFT JOIN FETCH r.meals WHERE r.restaurant_Name = :restaurantName";
			List<Restaurant> restaurant = App.Get_Restaurant(query);
			return restaurant.get(0).getMeals();
		}

		// Method to get meals by ingredient
		private List<Meal> getMealsByIngredient (String ingredient) throws Exception {
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
		public void sendToAllClients (Object message){
			try {
				for (SubscribedClient subscribedClient : SubscribersList) {
					subscribedClient.getClient().sendToClient(message);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		public void sendToAll (Object message){
			try {
				for (ConnectionToClient Subscriber : Subscribers) {
					Subscriber.sendToClient(message);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
