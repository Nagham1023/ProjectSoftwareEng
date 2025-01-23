package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
			// Extract the method from the message
			String method = msg.toString().substring(8); // Remove "Sort by " prefix

			if (method.startsWith("Search by Restaurant")) {
				String restaurantName = method.substring("Search by ".length());
				List<Meal> meals = getMealsByRestaurant(restaurantName);
				try {
					client.sendToClient(meals);
				}catch(Exception e){
					e.printStackTrace();
				}
			} else if (method.startsWith("Search by Ingredients")) {
				System.out.print("Hello");
				try {
					String ingredients = "Lettuce";//example
					var meals = getMealsByIngredient(ingredients);
					client.sendToClient(meals);
				}catch(Exception e){
					e.printStackTrace();
				}
			} else {
				System.out.println("Unknown search method.");
			}
		}

	}
	// Method to get meals by restaurant
	private List<Meal> getMealsByRestaurant(String restaurantName) {

		String query = "SELECT r FROM Resturant r LEFT JOIN FETCH r.meals WHERE r.resturant_Name = :restaurantName";
		List<Resturant> resturant = App.Get_Resturant(query);
		return resturant.get(0).getMeals();

	}

	// Method to get meals by ingredient
	private List<Meal> getMealsByIngredient(String ingredient) throws Exception {
		// Get all meals
		var meals = App.GetAllMeals();

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

}
