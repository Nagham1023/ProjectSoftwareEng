package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;

import static il.cshaifasweng.OCSFMediatorExample.server.App.*;

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
		if(msg instanceof mealEvent) {
			//here we're adding new meal !!
			//System.out.println("Received adding new mealEvent ");
			String addResult = AddNewMeal((mealEvent) msg);//if "added" then successed if "exist" then failed bcs there is a meal like that
			System.out.println("Added new mealEvent to the database");
			sendToAll(addResult);
			if(Objects.equals(addResult, "added")) {
				sendToAll(msg);
			}

		}
		if(msg instanceof UserCheck) {
			if(((UserCheck) msg).isState() == 1)//if login
			{
				try {
					if (checkUser(((UserCheck) msg).getUsername(), ((UserCheck) msg).getPassword())) {
						((UserCheck) msg).setRespond("Valid");
						client.sendToClient(msg);
					} else {
						((UserCheck) msg).setRespond("Username or password incorrect");
						client.sendToClient(msg);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			else if(((UserCheck) msg).isState() == 0) //if register
			{
				String response = AddNewUser((UserCheck) (msg));
				((UserCheck) msg).setRespond(response);
                try {
                    client.sendToClient(msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
			else if(((UserCheck) msg).isState() == 2) //if forgetpass
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
			}
			else if(((UserCheck) msg).isState() == 3) //if just a name check
			{
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

        }
		else if(msg.toString().startsWith("add client")){
			Subscribers.add(client);
			SubscribedClient connection = new SubscribedClient(client);
			SubscribersList.add(connection);
            try {
				client.sendToClient(getmealEvent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

			try {
				client.sendToClient("client added successfully");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		else if(msg.toString().startsWith("remove client")){
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
			if(!SubscribersList.isEmpty()){
				for(SubscribedClient subscribedClient: SubscribersList){
					if(subscribedClient.getClient().equals(client)){
						SubscribersList.remove(subscribedClient);
						break;
					}
				}
			}
		}
		else if (msg.toString().startsWith("Sort")) {
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
					var meals = App.GetAllMeals();
					client.sendToClient(meals);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Unknown search method.");
			}
		}
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

		String query = "SELECT r FROM Resturant r LEFT JOIN FETCH r.meals WHERE r.resturant_Name = :restaurantName";
		List<Resturant> resturant = App.Get_Resturant(query);
		return resturant.get(0).getMeals();


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

}
