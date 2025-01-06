package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
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
            //System.out.println("sent a message!");
            //client.sendToClient(msg);
            //sned to all!!!
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
