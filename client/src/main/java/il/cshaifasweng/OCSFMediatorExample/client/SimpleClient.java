package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.ReportResponseEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.WarningEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.time.LocalTime;
import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	public static String IP = "127.0.0.1";
	public static int Port = 3000;
	private static UserCheck UserClient = null;
	private static boolean logged = false;

	private SimpleClient(String host, int port) {
		super(host, port)	;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("got a message from server " + msg);
		if (msg instanceof updatePrice) {
			System.out.println("the message is an update price");
			EventBus.getDefault().post(msg);
		}

		//handling order cancellation
		if (msg instanceof CancelOrderEvent) {
			CancelOrderEvent event = (CancelOrderEvent) msg;
			//Order order = event.getOrder();
			EventBus.getDefault().post(event);
		}

		//handling reservation cancellation
		if (msg instanceof CancelReservationEvent) {
			CancelReservationEvent event = (CancelReservationEvent) msg;
			//String id = event.getReservationId();
			//String customerEmail = event.getCustomerEmail();
			EventBus.getDefault().post(event);
		}

		if (msg instanceof UserCheck) {
			EventBus.getDefault().post(msg);
		}
		/******************************adan*****************************************/
		if (msg instanceof PaymentCheck) {
			EventBus.getDefault().post(msg);  // Handle credit card-related messages
		}
		/******************************adan*****************************************/

		if (msg instanceof ComplainList) {
			System.out.println("the message is an adding complaint");
			ComplainList complainList = (ComplainList) msg;
			//print restaurants names
			System.out.println("Received restaurant list: " + complainList.toString());
			EventBus.getDefault().post(complainList);
		}
		if (msg instanceof complainEvent) {
			System.out.println("the message is an adding complaint");
			EventBus.getDefault().post(msg);
		}
		if(msg instanceof MealsList) {
			EventBus.getDefault().post(msg);
		}
		if (msg instanceof List<?>) { // Check if msg is a list
			System.out.println("the message is a list");
			List<?> list = (List<?>) msg;
			if (!list.isEmpty() && list.get(0) instanceof mealEvent) { // Ensure it's a List<Meal>
				System.out.println("list of meals");
				EventBus.getDefault().post(msg);
			}
			else if (list.get(0) instanceof Meal) { // If the list contains Meal objects
				System.out.println("Meals found:");
				for (Object obj : list) {
					Meal meal = (Meal) obj;
					System.out.println("Meal: " + meal.getName() + " - " + meal.getDescription());
				}
				EventBus.getDefault().post(msg);
			}else if(list.get(0) instanceof ReservationEvent){
				EventBus.getDefault().post(msg);
			}
		}
		if(msg.getClass().equals(DifferentResrvation.class)){
			EventBus.getDefault().post(msg);
		}
		if (msg.getClass().equals(mealEvent.class)) {
			EventBus.getDefault().post(msg);
		}
		if (msg.getClass().equals(RestaurantList.class)) {
			EventBus.getDefault().post(msg);
		}
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		if(msg instanceof SearchOptions){
			EventBus.getDefault().post(msg);
		}
		// Check if the message starts with "ReportResponse"
		if(msg instanceof String) {
			String message = (String) msg;
			if (message.startsWith("ReportResponse")) {
				// Split the message by "\n" to extract the report content
				String[] parts = message.split("\n", 2); // Limit to 2 splits
				if (parts.length == 2) {
					String report = parts[1]; // The actual report content
					System.out.println("Received report: " + report);


					// Publish the event to the EventBus
					EventBus.getDefault().post(new ReportResponseEvent(report));
				} else {
					System.err.println("Malformed report response from server.");
				}
			} else if (message.equals("Reservation confirmed successfully.")) {
				EventBus.getDefault().post(msg);
			} else {
				System.out.println("Unhandled message: " + message);
			}
		}
        if (msg instanceof RestaurantList) {
            RestaurantList restaurantList = (RestaurantList) msg;

            //print restaurants names
            System.out.println("Received restaurant list: " + restaurantList.toString());
            EventBus.getDefault().post(restaurantList);
        }
		if (msg instanceof ListComplainList) {
			ListComplainList listComplainList = (ListComplainList) msg;
			System.out.println("Received complaint list: " + listComplainList.toString());
			EventBus.getDefault().post(listComplainList);
		}

	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(IP, Port);
		}
		return client;
	}
	public static boolean isClientConnected(){
		boolean temp = logged;
        if(!logged){
			logged = true;
		}
		return temp;
    }
	public static boolean isLog() {
        return UserClient != null;
	}
	public static UserCheck getUser() {
		return UserClient;
	}
	public static void setUser(UserCheck user) {
		UserClient = user;
	}

}