package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.done_Order;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.deliveryPrice;

public class receiptController {
    @FXML
    private Label OrderNumField;

    @FXML
    private Label PaidField;

    @FXML
    private Label branchField;

    @FXML
    private Label dateField;

    @FXML
    private VBox mealDetailsContainer;



    public void initialize() {
        int paid = done_Order.getTotal_price();
        OrderNumField.setText("Order Number: "+done_Order.getId());
        branchField.setText(done_Order.getRestaurantName());
        dateField.setText(String.valueOf(done_Order.getDate()));
        String ccNumber = done_Order.getCreditCard_num();
        String lastFour = ccNumber.length() > 4 ? ccNumber.substring(ccNumber.length() - 4) : ccNumber;
        PaidField.setText("Paid "+paid+"₪ with Credit Card ends with "+lastFour);
        fillMealDetailsContainer();
        String[] mealsOrdered = new String[done_Order.getMeals().size()];
        int index = 0;
        for (MealInTheCart meal : done_Order.getMeals()) {
            String mealDetails = meal.getMeal().getMeal().getName() + " - Quantity: " + meal.getQuantity() + " - Price: $" + meal.getMeal().getMeal().getPrice();
            mealsOrdered[index] = mealDetails;
            index++;
        }
        sendOrderConfirmationEmail(CreditDetailsController.personalDetails,done_Order.getId(), done_Order.getRestaurantName(),mealsOrdered );
        if(done_Order.getOrderType().equals("Delivery"))
        {
            addDelivery();
        }
    }


    public static void sendOrderConfirmationEmail(PersonalDetails customer, int orderNumber,
                                                  String restaurantName, String[] mealsOrdered) {
        // Create the subject
        String subject = "Order Confirmation - Order #" + orderNumber;

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());

        // Construct the body of the email
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(customer.getName()).append(",\n\n");
        body.append("Thank you for your order at ").append(restaurantName).append("!\n\n");
        body.append("Here are the details of your order:\n");
        body.append("Order Number: ").append(orderNumber).append("\n");
        body.append("Order Date: ").append(currentDate).append("\n\n");
        body.append("Meals Ordered:\n");

        for (String meal : mealsOrdered) {
            body.append("- ").append(meal).append("\n");
        }


        if(done_Order.getOrderType().equals("Delivery"))
            body.append("\nDelivery Fee: ").append(deliveryPrice).append("₪\n");

        body.append("Total Price: ").append(done_Order.getTotal_price()).append("₪\n");

        body.append("\nWe hope you enjoy your meal!\n\n");
        body.append("Best regards,\n");
        body.append("Mama's Restaurant Team");

        // Now you can call the email sender with the subject, body, and the customer's email
        EmailSender.sendEmail(subject, body.toString(), customer.getEmail());
    }


    @FXML
    void goToMainScreen() {
        System.out.println("Go to Main Screen");
        try {
            App.setRoot("mainScreen");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void fillMealDetailsContainer() {
        StringBuilder orderDetails = new StringBuilder();
        double totalAmount = 0.0;

        // Loop through the meals and update the summary
        for (MealInTheCart meal : done_Order.getMeals()) {
            totalAmount += meal.getMeal().getMeal().getPrice() * meal.getQuantity();
            orderDetails.append("\n");
        }

        // Prepare the total amount text
        String totalAmountText = "Total: " + totalAmount + "₪";

        // Clear the existing content in the meal details container
        mealDetailsContainer.getChildren().clear();

        // Add the meal details to the container dynamically
        for (MealInTheCart meal : done_Order.getMeals()) {
            HBox mealRow = new HBox(10);
            mealRow.setSpacing(10);

            // Display the meal image (if exists)
            byte[] imageBytes = meal.getMeal().getMeal().getImage();
            if (imageBytes != null && imageBytes.length > 0) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                Image mealImage = new Image(byteArrayInputStream);

                ImageView imageView = new ImageView(mealImage);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);

                Rectangle clip = new Rectangle(100, 100);
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                imageView.setClip(clip);

                mealRow.getChildren().add(imageView);
            }

            // Add meal info text (name and quantity)
            TextFlow mealInfoTextFlow = new TextFlow();

            Text mealName = new Text(meal.getMeal().getMeal().getName() + " - ");
            Text boldX = new Text("X");
            boldX.setStyle("-fx-font-weight: bold;");
            Text quantity = new Text(String.valueOf(meal.getQuantity()));
            quantity.setStyle("-fx-font-weight: bold;");

            Text mealPrice = new Text(" (" + meal.getMeal().getMeal().getPrice() + "₪)\n");
            mealPrice.setStyle("-fx-font-weight: bold;");

            mealInfoTextFlow.getChildren().addAll(mealName, boldX, quantity,mealPrice, new Text("\n"));

            // Add meal description (if available)
            if (meal.getMeal().getMeal().getDescription() != null && !meal.getMeal().getMeal().getDescription().isEmpty()) {
                Text description = new Text(meal.getMeal().getMeal().getDescription() + "\n");
                mealInfoTextFlow.getChildren().add(description);
            }

            // Add customizations (with check/uncheck images)
            if (meal.getMeal().getCustomizationsList() != null && !meal.getMeal().getCustomizationsList().isEmpty()) {
                Text customizationsTitle = new Text("Customizations:\n");
                customizationsTitle.setStyle("-fx-font-weight: bold;");
                mealInfoTextFlow.getChildren().add(customizationsTitle);

                Image checkedImage = new Image(getClass().getResourceAsStream("/images/newcheck.png"));
                Image uncheckedImage = new Image(getClass().getResourceAsStream("/images/newradio.png"));

                for (CustomizationWithBoolean customWithBool : meal.getMeal().getCustomizationsList()) {
                    HBox customRow = new HBox(5);
                    Label customLabel = new Label(customWithBool.getCustomization().getName());
                    customLabel.setStyle("-fx-text-fill: black;");

                    ImageView checkImageView = new ImageView(customWithBool.getValue() ? checkedImage : uncheckedImage);
                    checkImageView.setFitWidth(20);
                    checkImageView.setFitHeight(20);
                    checkImageView.setPreserveRatio(true);

                    customRow.getChildren().addAll(checkImageView, customLabel);
                    mealInfoTextFlow.getChildren().add(customRow);
                }
            }

            // Add the meal info to the row
            mealRow.getChildren().add(mealInfoTextFlow);

            // Add the row to the meal details container
            mealDetailsContainer.getChildren().add(mealRow);
        }
    }
    public void addDelivery() {
        StringBuilder orderDetails = new StringBuilder();

        HBox deliveryRow = new HBox(10);
        deliveryRow.setSpacing(10);


        Image delivery = new Image(getClass().getResourceAsStream("/images/delivery_icon.png"));

        ImageView imageView = new ImageView(delivery);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        Rectangle clip = new Rectangle(100, 100);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        deliveryRow.getChildren().add(imageView);

        // Add meal info text (name and quantity)
        TextFlow DeliveryInfo = new TextFlow();

        Text text = new Text("Delivery - ");

        Text deliveryPrice = new Text(" (25₪)\n");
        deliveryPrice.setStyle("-fx-font-weight: bold;");

        DeliveryInfo.getChildren().addAll(text,deliveryPrice, new Text("\n"));

        deliveryRow.getChildren().add(DeliveryInfo);

        mealDetailsContainer.getChildren().add(deliveryRow);
    }

}