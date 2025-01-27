package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.mealEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.updatePrice;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;



public class PrimaryController {
	public static List<mealEvent> meals;
	@FXML
	private Label copyr;
	@FXML
	void initialize() throws IOException {

		Platform.runLater(() ->
		{
			SimpleClient client;
			boolean temp = SimpleClient.isClientConnected();
			EventBus.getDefault().register(this);
			client = SimpleClient.getClient();
			if (!temp) {
				try {
					client.sendToServer("add client");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			if (SimpleClient.isLog()) {
				UserCheck user = SimpleClient.getUser();
				copyr.setText("logged into " + user.getUsername() + " with role " + user.getRole());
			}
		});
	}
	@Subscribe
	public void gotMeals(List<mealEvent> event) {
		meals = event;
	}

	@FXML
	void goToMenu(ActionEvent event) throws IOException {
		SimpleClient client = SimpleClient.getClient();
		client.sendToServer("toMenuPage");
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(()->{
            try {
                App.setRoot("menu");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
	}
	@FXML
	void goToLogin(ActionEvent event) throws IOException {
		App.setRoot("login");
	}
	@FXML
	void goUpdateMenu(ActionEvent event) throws IOException {
            App.setRoot("addmeal");
	}

}
