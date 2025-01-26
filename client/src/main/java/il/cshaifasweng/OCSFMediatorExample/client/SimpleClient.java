package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.util.List;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	public static String IP = "127.0.0.1";
	public static int Port = 3000;

	private SimpleClient(String host, int port) {
		super(host, port)	;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("got a message from server " + msg);
		if(msg instanceof updatePrice) {
			System.out.println("the message is an update price");
			EventBus.getDefault().post(msg);
		}
		if(msg instanceof UserCheck)
		{
			EventBus.getDefault().post(msg);
		}
		if (msg instanceof List<?>) { // Check if msg is a list
			//System.out.println("the message is a list");
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
			}
		}
		if (msg.getClass().equals(mealEvent.class)) {
			EventBus.getDefault().post(msg);
		}
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}
		if(msg instanceof String) {
			EventBus.getDefault().post(msg);
		}

	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(IP, Port);
		}
		return client;
	}

}