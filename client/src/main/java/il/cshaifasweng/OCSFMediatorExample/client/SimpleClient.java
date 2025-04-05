package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.UserManagement;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	public static String IP = "127.0.0.1";
	public static int Port = 3000;
	private static UserCheck UserClient = null;
	private static boolean logged = false;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("got a message from server " + msg);
		if (msg instanceof updatePrice) {
			System.out.println("the message is an update price");
			handleUpdatePrice(msg);
		}
		else if (msg instanceof UpdateMealRequest) {
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof MealUpdateRequest) {
			System.out.println("the message is an update price request");
			EventBus.getDefault().post((MealUpdateRequest) msg);
		}
		else if (msg instanceof PCRequestsList) {
			EventBus.getDefault().post((PCRequestsList) msg);
		}
		else if (msg instanceof UserCheck) {
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof PersonalDetails) {
			System.out.println("the message is a personal details");
			EventBus.getDefault().post(msg);  // Handle personal details-related messages
		}

		/******************************adan*****************************************/
		else if (msg instanceof PaymentCheck) {
			EventBus.getDefault().post(msg);  // Handle credit card-related messages
		}
		/******************************adan*****************************************/

		else if (msg instanceof ComplainList) {
			System.out.println("the message is an adding complaintList");
			ComplainList complainList = (ComplainList) msg;
			//print restaurants names
			System.out.println("Received restaurant list: " + complainList.toString());
			EventBus.getDefault().post(complainList);
		}
		else if (msg instanceof complainEvent) {
			System.out.println("the message is an adding complaint");
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof MealsList) {
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof List<?>) { // Check if msg is a list
			System.out.println("the message is a list");
			List<?> list = (List<?>) msg;
			handleListMessage(list);
		}
		else if (msg instanceof CancelOrderEvent) {
			CancelOrderEvent event = (CancelOrderEvent) msg;
			Order order = event.getOrder();
			EventBus.getDefault().post(event);
		}
		else if (msg instanceof UpdateMealEvent) {
			UpdateMealEvent event = (UpdateMealEvent) msg;
			EventBus.getDefault().post(event);
		}
		//***************************omar********************************************//
		else if (msg.getClass().equals(DifferentResrvation.class)) {
				EventBus.getDefault().post(msg);
		}
		else if (msg.getClass().equals(mealEvent.class)) {
				EventBus.getDefault().post(msg);
		}
		else if (msg.getClass().equals(Order.class)) {
				System.out.println("received order");
				EventBus.getDefault().post(msg);
		}
		else if (msg.getClass().equals(RestaurantList.class)) {
				EventBus.getDefault().post(msg);
		}
		else if (msg.getClass().equals(ListOfCC.class)) {
				System.out.println("received list of CC");
				EventBus.getDefault().post(msg);
		}
		else if (msg.getClass().equals(Warning.class)) {
				EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		else if (msg instanceof SearchOptions) {
				EventBus.getDefault().post(msg);
		}
		else if (msg instanceof RestaurantList) {
			RestaurantList restaurantList = (RestaurantList) msg;
			//print restaurants names
			System.out.println("Received restaurant list: " + restaurantList.toString());
			EventBus.getDefault().post(restaurantList);
		}
		else if (msg instanceof String) {
			String message = (String) msg;
			handleStringMessage((String) msg);
		}
		else if (msg instanceof tablesStatus) {
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof ReConfirmEvent) {
			EventBus.getDefault().post(msg);
		}
		else if (msg instanceof ListComplainList) {
			ListComplainList listComplainList = (ListComplainList) msg;
			System.out.println("Received complaint list: " + listComplainList.toString());
			EventBus.getDefault().post(listComplainList);
		}
		else if (msg instanceof updateResponse) {
			generateResponse((updateResponse) msg);
		}
		else if (msg instanceof UserManagement){
			handleUserManagement((UserManagement)msg);
		}
		else if (msg instanceof ReservationSave) {
			System.out.println("Third step: received reservation");
			EventBus.getDefault().post((ReservationSave)msg);
		}
		else if (msg instanceof FaildPayRes) {
			EventBus.getDefault().post((ReservationSave)msg);
		}
		else {
				System.out.println("Unhandled message: " + (String) msg);
			}
		//*********************** deleted functions?*****************//

		//                if (msg instanceof List<?>) {
//                        System.out.println("The message is a list");
//                        List<?> list = (List<?>) msg;
//                        if (!list.isEmpty() && list.get(0) instanceof CreditCard) {
//                                List<CreditCard> creditCards = new ArrayList<>(list.size());
//                                for (Object item : list) {
//                                        if (item instanceof CreditCard) {
//                                                creditCards.add((CreditCard) item);
//                                        }
//                                }
//                                EventBus.getDefault().post(creditCards);
//                        }
//                }
//                if (msg instanceof List<?>) { // Check if msg is a list
//                        System.out.println("the message is a list");
//                        List<?> list = (List<?>) msg;
//                        if (!list.isEmpty() && list.get(0) instanceof CreditCard) { // Ensure it's a List<Meal>
//                                System.out.println("list of creditcards");
//                                EventBus.getDefault().post(msg);
//                        }
//                }
		//********************************************//
		//                        else if (!list.isEmpty() && list.get(0) instanceof CreditCard) {
//                                        System.out.println("The message is a list");
//                                        List<CreditCard> creditCards = new ArrayList<>(list.size());
//                                        for (Object item : list) {
//                                                if (item instanceof CreditCard) {
//                                                        creditCards.add((CreditCard) item);
//                                                }
//                                        }
//                                System.out.println("Posting credit card details to EventBus.");
//                                        System.out.println(creditCards + " credddddit ");
//                                System.out.println(" credit card get class " + creditCards.getClass().getName());
//
//                                EventBus.getDefault().post(creditCards);
//                                }
		//******************************************************************//
		//			} else if (message.equals("Order not found.")) {
//				EventBus.getDefault().post(msg);
//			} else if (message.equals("No order!")) {
//				EventBus.getDefault().post(msg);
//			} else if (message.equals("Not same restaurant!")) {
//			} else if (message.equals("Reservation confirmed successfully.")) {
//				EventBus.getDefault().post(msg);
			/// ////////////*****************************************************///
//
//			if (msg instanceof ListComplainList) {
//				ListComplainList listComplainList = (ListComplainList) msg;
//				System.out.println("Received complaint list: " + listComplainList.toString());
//				EventBus.getDefault().post(listComplainList);
//			}
//
//		}
	}

		public static SimpleClient getClient () {
			if (client == null) {
				client = new SimpleClient(IP, Port);
			}
			return client;
		}

		public static boolean isClientConnected () {
			boolean temp = logged;
			if (!logged) {
				logged = true;
			}
			return temp;
		}

		public static boolean isLog () {
			return UserClient != null;
		}

		public static UserCheck getUser () {
			return UserClient;
		}

		public static void setUser (UserCheck user){
			UserClient = user;
		}


	//-------------------------------helper functions-------------------------------------------//
	private void handleUpdatePrice(Object msg) {

		UpdatePriceRequestEvent event = new UpdatePriceRequestEvent((updatePrice) msg);
		switch (((updatePrice) msg).getPurpose().toLowerCase()) {
			case "denying":
				// Specific handling for denial
				EventBus.getDefault().post(event);
				break;
			case "changing":
				EventBus.getDefault().post(msg);
				EventBus.getDefault().post(event);
				break;
		}
	}

	private void handleListMessage(List<?> list) {

		if (list.isEmpty()) return;

		Object firstItem = list.get(0);
		if (firstItem instanceof mealEvent) {
			System.out.println("List of meal events");
			EventBus.getDefault().post(list);
		}
		else if (firstItem instanceof Meal) {
			System.out.println("Meals list:");
			for (Object obj : list) {
				Meal meal = (Meal) obj;
				System.out.println("Meal: " + meal.getName() + " - " + meal.getDescription());
			}
			EventBus.getDefault().post(list);
		}
		else if (firstItem instanceof ReservationEvent) {
			EventBus.getDefault().post(list);
		}
		else if (firstItem instanceof TableNode) {
			EventBus.getDefault().post(list);
		}
		else if (firstItem instanceof CreditCard) {
			handleCreditCards(list);
		}
		else if(firstItem instanceof Users){
			System.out.println("Received users");
			List<Users> usersList = list.stream()
					.filter(obj -> obj instanceof Users) // Ensure only Users objects are included
					.map(obj -> (Users) obj)             // Cast each object to Users
					.collect(Collectors.toList());

			EventBus.getDefault().post(new UsersListEvent(usersList));

			//EventBus.getDefault().post(new UsersListEvent((List<Users>) list));
		}
	}

	private void handleCreditCards(List<?> list) {
		System.out.println("The message is a list of CreditCards");
		// Iterate through the list and post each CreditCard individually
		for (Object item : list) {
			if (item instanceof CreditCard) {
				CreditCard creditCard = (CreditCard) item;
				// Post the individual CreditCard to the EventBus
				System.out.println("Posting a single credit card to EventBus: " + creditCard);
				EventBus.getDefault().post(creditCard); // Posting individual CreditCard to EventBus
			}
		}
	}

	private void handleStringMessage(String message) {
		if (message.startsWith("table details: ")) {
			// Extract the table details from the message
			String tableDetails = message.substring("table details: ".length());
			// Send the table details to the EventBus
			EventBus.getDefault().post(tableDetails);
		}
		else if (message.equalsIgnoreCase("added")) {
			EventBus.getDefault().post("added");
		}
		else if (message.startsWith("ReportResponse")) {
			handleReportResponse(message);
		}
		else if (message.contains("delete")) {
			handleDeleteMessage(message);
		}
		else if (message.equals("Reservation confirmed successfully.")) {
			EventBus.getDefault().post(message);
		}
		else if(message.startsWith("Cancle Reservation ")){
			EventBus.getDefault().post(message.substring("Cancle Reservation ".length()).trim());}
		else if (message.equals("Registration completed successfully")) {
			EventBus.getDefault().post(message);

		}
		else if(message.equals("go To payment check")){
			EventBus.getDefault().post(message);
		}
		else {
			switch (message) {
				case "Reservation confirmed successfully.":
					EventBus.getDefault().post(message);
					break;
				case "Order not found.":
					EventBus.getDefault().post(message);
					break;
				case "No order!":
					EventBus.getDefault().post(message);
					break;
				case "Not same restaurant!":
					EventBus.getDefault().post(message);
					break;
				default:
					System.out.println("Unhandled message: " + message);
			}
		}
	}
	private void handleReportResponse(String message) {
		// Split the message by "\n" to extract the report content
		String[] parts = message.split("\n", 2);
		if (parts.length == 2) {
			String report = parts[1]; // The actual report content
			System.out.println("Received report: " + report);
			EventBus.getDefault().post(new ReportResponseEvent(report));
		} else {
			System.err.println("Malformed report response");
		}
	}

	private void handleDeleteMessage(String message) {
		// Extract the substring after "delete "
		String payload = message;
		System.out.println("Received delete payload: " + payload);

		String[] parts = payload.split(" ");

		// Extract values
		String mealId = parts[4];
		String mealName = parts[5];
		System.out.println("Received delete meal: " + mealName + " - " + mealId);

		if (mealId != null && mealName != null) {
			// Post the event with both ID and name
			EventBus.getDefault().post(new DeleteMealEvent(mealId, mealName));
		}
	}
	private void handleUserManagement(UserManagement msg){
		EventBus.getDefault().post(msg);
	}

	private void generateResponse(updateResponse response) {
		String orderNum = response.getOrderNumber();
		String complaintType = "Refunded";
		String ourResponse = response.getnewResponse();
		String refundAmount = String.valueOf(response.getRefundAmount());


		// Generate subject
		String subject = "Response to Your Complaint - Order #" + orderNum;

		// Generate message
		StringBuilder message = new StringBuilder("Dear customer" + ",\n\n");
		message.append("Thank you for reaching out regarding your ").append(complaintType.toLowerCase()).append(".\n\n");
		message.append("Here is a summary of your inquiry:\n\n");
		message.append("**Order Number:** ").append(orderNum).append("\n");
		message.append("**Our Response:**\n");
		message.append(ourResponse).append("\n\n");

		if (!refundAmount.isEmpty()) {
			message.append("✅ **Refund Issued:** We have processed a refund of ").append(refundAmount).append("₪ to your account.\n\n");
		}

		message.append("We value you as our customer and are here to assist with any further concerns.\n\n");
		message.append("Warm regards,\nMama's Restaurant Customer Service Team");


		EmailSender.sendEmail(subject, message.toString(), response.getEmailComplain());
	}
	public void triggerEvent(List<ReservationEvent> availableReservations) {
		EventBus.getDefault().post(availableReservations);
	}
}
