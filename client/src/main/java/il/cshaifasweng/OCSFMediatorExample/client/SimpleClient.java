package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

import java.util.List;

public class SimpleClient extends AbstractClient {
	
	private static SimpleClient client = null;

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
		if (msg instanceof List<?>) { // Check if msg is a list
			System.out.println("the message is a list");
			List<?> list = (List<?>) msg;
			if (!list.isEmpty() && list.get(0) instanceof mealEvent) { // Ensure it's a List<Meal>
				System.out.println("list of meals");
				EventBus.getDefault().post(msg);
			}
		}
		if (msg.getClass().equals(mealEvent.class)) {
			//EventBus.getDefault().post(new WarningEvent((Warning) msg));
			System.out.println("got a message !!!!");
		}
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		}

	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
