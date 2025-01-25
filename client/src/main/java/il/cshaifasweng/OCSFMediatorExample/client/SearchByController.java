package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.io.IOException;

public class SearchByController {

    @FXML
    private CheckBox Cheese_check;

    @FXML
    private Label Error_Label;

    @FXML
    private CheckBox Lettuce_check;

    @FXML
    private CheckBox Meat_check;

    @FXML
    private CheckBox Resturant1_check;

    @FXML
    private CheckBox Resturant2_check;

    @FXML
    private CheckBox Resturant3_check;

    @FXML
    private Button ApplyFilterButton;

    @FXML
    void Apply_filter(javafx.event.ActionEvent event) throws IOException {
        StringBuilder selectedFilters = new StringBuilder("Sort by \n");

        // Check which checkboxes are selected
        if (Resturant1_check.isSelected()) {
            selectedFilters.append("Restaurant 1\n");
        }
        if (Resturant2_check.isSelected()) {
            selectedFilters.append("Restaurant 2\n");
        }
        if (Resturant3_check.isSelected()) {
            selectedFilters.append("Restaurant 3\n");
        }
        if (Lettuce_check.isSelected()) {
            selectedFilters.append("Lettuce\n");
        }
        if (Cheese_check.isSelected()) {
            selectedFilters.append("Cheese\n");
        }
        if (Meat_check.isSelected()) {
            selectedFilters.append("Meat\n");
        }

        // If no filters are selected, show an error message
        if (selectedFilters.toString().equals("Sort by \n")) {
            Error_Label.setText("Please select at least one filter.");
            Error_Label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            // Display the selected filters
            Error_Label.setText(""); // Clear error message
            System.out.println(selectedFilters);
            SimpleClient.getClient().sendToServer(selectedFilters.toString());
            Platform.runLater(() -> {
                try {
                    App.setRoot("menu");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            ((javafx.stage.Stage) ApplyFilterButton.getScene().getWindow()).close();
        }
    }
}
