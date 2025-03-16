package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.getUser;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.isLog;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private SimpleClient client;

    @Override
    public void start(Stage stage) throws IOException {
        //EventBus.getDefault().register(this);
    	/*client = SimpleClient.getClient();
    	client.openConnection();*/
        scene = new Scene(loadFXML("ipandport"), 1000, 600);

        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /*public static <T> T setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = loader.load();

        // Change the scene root
        scene.setRoot(root);

        // Return the controller
        return loader.getController();
    }*/


    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    

    /*@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
    	EventBus.getDefault().unregister(this);
		super.stop();
	}*/
    /*@Subscribe
    public void onWarningEvent(WarningEvent event) {
    	Platform.runLater(() -> {
    		Alert alert = new Alert(AlertType.WARNING,
        			String.format("Message: %s\nTimestamp: %s\n",
        					event.getWarning().getMessage(),
        					event.getWarning().getTime().toString())
        	);
        	alert.show();
    	});
    	
    }*/
    @Override
    public void stop() {
        System.out.println("Stopped");
        try {

            if (SimpleClient.getClient() != null) {
                client = SimpleClient.getClient();
                if (client.isConnected()) {
                    System.out.println("Closing Clientttt");
                    if(isLog()) {
                        System.out.println("Logging out");
                        UserCheck us = getUser();
                        us.setState(4);
                        client.sendToServer(us);
                    }
                    client.sendToServer("remove client");
                }
                SimpleClient.getClient().closeConnection();
                super.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
        launch();
    }

}