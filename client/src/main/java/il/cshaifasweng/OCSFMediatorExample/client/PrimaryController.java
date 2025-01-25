package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
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
		SimpleClient client;
		EventBus.getDefault().register(this);
		client = SimpleClient.getClient();
		client.sendToServer("add client");
	}
	@Subscribe
	public void addnewmeal(mealEvent mealEvent) {
		Platform.runLater(() -> {
			meals.add(mealEvent);
		});
	}
	@Subscribe
	public void mealEvent(updatePrice updateprice) {
		//System.out.println("We're in primary controller in mealEvent changing the list");

		String mealId = String.valueOf(updateprice.getIdMeal());
		String newPrice = String.valueOf(updateprice.getNewPrice());

		if (meals == null) {
			System.out.println("Meals list is null. Cannot update price.");
			return;
		}

		boolean updated = false;
		for (mealEvent meal : meals) {
			if (meal.getId().equals(mealId)) {
				meal.setPrice(newPrice);
				updated = true;
				//System.out.println("Meal ID " + mealId + " updated to price: " + meal.getPrice());
				break;
			}
		}

		if (updated) {
			Platform.runLater(() -> {
				System.out.println("UI update logic executed for Meal ID " + mealId);
			});
		} else {
			System.out.println("Meal ID " + mealId + " not found in the list.");
		}
	}
	@Subscribe
	public void gotMeals(List<mealEvent> event) {
		Platform.runLater(() -> {
			meals = event;
		});

	}
	@FXML
	void goToMenu(ActionEvent event) throws IOException {
            App.setRoot("menu");
	}

	@FXML
	void goUpdateMenu(ActionEvent event) throws IOException {
            App.setRoot("addmeal");
	}

}
