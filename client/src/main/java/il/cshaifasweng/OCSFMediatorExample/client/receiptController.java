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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static il.cshaifasweng.OCSFMediatorExample.client.CreditDetailsController.*;
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
        if(mode.equals("Order")) {
            double paid = done_Order.getTotal_price();
            OrderNumField.setText("Order Number: " + done_Order.getId());
            branchField.setText(done_Order.getRestaurantName());
            dateField.setText(String.valueOf(done_Order.getDate()));
            String ccNumber = done_Order.getCreditCard_num();
            String lastFour = ccNumber.length() > 4 ? ccNumber.substring(ccNumber.length() - 4) : ccNumber;
            switch (done_Order.getPayment_method()) {
                case "Visa" -> PaidField.setText(String.format("Paid %.2f ₪ with Visa ends with %s", paid, lastFour));
                case "Mastercard" ->
                        PaidField.setText(String.format("Paid %.2f ₪ with MasterCard ends with %s", paid, lastFour));
                case "Cash" -> PaidField.setText(String.format("Paying %.2f ₪ with Cash", paid));
            }
            fillMealDetailsContainer();
            String[] mealsOrdered = new String[done_Order.getMeals().size()];
            int index = 0;
            for (MealInTheCart meal : done_Order.getMeals()) {
                double price = meal.getMeal().getMeal().getPrice();
                double discount = meal.getMeal().getMeal().getDiscount_percentage();
                double finalPrice = discount > 0 ? price * (1 - discount / 100) : price;

                String mealDetails = meal.getMeal().getMeal().getName() +
                        " - Quantity: " + meal.getQuantity() +
                        " - Price: " + String.format("%.2f₪", finalPrice);

                if (discount > 0) {
                    mealDetails += String.format(" (Original: %.2f₪, %d%% OFF)", price, (int)discount);
                }

                mealsOrdered[index] = mealDetails;
                index++;
            }
            sendOrderConfirmationEmail(CreditDetailsController.personalDetails, done_Order.getId(), done_Order.getRestaurantName(), mealsOrdered, done_Order.getPayment_method());
            if (done_Order.getOrderType().equals("Delivery")) {
                addDelivery();
            }
        }
        else if(mode.equals("Reservation")) {
            OrderNumField.setText("Reservation Number: " + done_Reservation.getReservationSaveID());
            branchField.setText(done_Reservation.getRestaurantName());
            dateField.setText(String.valueOf(done_Reservation.getReservationDateTime()));
            String ccNumber = done_Reservation.getCreditCard_num();
            String lastFour = ccNumber.length() > 4 ? ccNumber.substring(ccNumber.length() - 4) : ccNumber;
            PaidField.setText("Paid with Credit Card ends with " + lastFour);
            addReserved();

        }
    }


    public static void sendOrderConfirmationEmail(PersonalDetails customer, int orderNumber,
                                                  String restaurantName, String[] mealsOrdered,String paymentMethod) {
        // Create the subject
        String subject = "Order Confirmation - Order #" + orderNumber;

        // Format the date
        LocalDateTime orderTime = done_Order.getOrderTime();
        Date date = Date.from(orderTime.atZone(ZoneId.systemDefault()).toInstant());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderDate = dateFormat.format(date);

        // Construct the body of the email
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(customer.getName()).append(",\n\n");
        body.append("Thank you for your order at ").append(restaurantName).append("!\n\n");
        body.append("Here are the details of your order:\n");
        body.append("Order Number: ").append(orderNumber).append("\n");
        body.append("Order Date: ").append(orderDate).append("\n\n");
        body.append("Meals Ordered:\n");

        for (String meal : mealsOrdered) {
            body.append("- ").append(meal).append("\n");
        }


        if(done_Order.getOrderType().equals("Delivery"))
            body.append("\nDelivery Fee: ").append(deliveryPrice).append("₪\n");
        switch (paymentMethod) {
            case "Mastercard" -> {
                body.append("\nPayment Method: Mastercard\n");
            }
            case "Cash" -> {
                body.append("\nPayment Method: Cash\n");
            }
            case "Visa" -> {
                body.append("\nPayment Method: Visa\n");
            }
        }


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

        // Clear the existing content in the meal details container
        mealDetailsContainer.getChildren().clear();

        // Add the meal details to the container dynamically
        for (MealInTheCart meal : done_Order.getMeals()) {
            HBox mealRow = new HBox(10);
            mealRow.setSpacing(10);

            // Calculate price with discount if applicable
            double discount = meal.getMeal().getMeal().getDiscount_percentage();
            double price = meal.getMeal().getMeal().getPrice();
            double finalPrice = discount > 0 ? price * (1 - discount / 100) : price;
            totalAmount += finalPrice * meal.getQuantity();

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

            // Create container for all text content
            VBox textContainer = new VBox(5);

            // Add meal name and quantity
            HBox nameQuantityBox = new HBox(5);
            Text mealName = new Text(meal.getMeal().getMeal().getName() + " - ");
            Text boldX = new Text("X");
            boldX.setStyle("-fx-font-weight: bold;");
            Text quantity = new Text(String.valueOf(meal.getQuantity()));
            quantity.setStyle("-fx-font-weight: bold;");
            nameQuantityBox.getChildren().addAll(mealName, boldX, quantity);
            textContainer.getChildren().add(nameQuantityBox);

            // Add price information
            if (discount > 0) {
                // Original price with strikethrough
                HBox originalPriceBox = new HBox(5);
                Text originalPriceLabel = new Text("Original: ");
                Text originalPrice = new Text(String.format("%.2f₪", price));
                originalPrice.setStyle("-fx-strikethrough: true; -fx-fill: #999999;");
                originalPriceBox.getChildren().addAll(originalPriceLabel, originalPrice);
                textContainer.getChildren().add(originalPriceBox);

                // Discounted price
                HBox discountedPriceBox = new HBox(5);
                Text discountedPriceLabel = new Text("Price: ");
                Text discountedPrice = new Text(String.format("%.2f₪", finalPrice));
                discountedPrice.setStyle("-fx-font-weight: bold; -fx-fill: black;");
                discountedPriceBox.getChildren().addAll(discountedPriceLabel, discountedPrice);
                textContainer.getChildren().add(discountedPriceBox);

                // Discount badge
                Text discountBadge = new Text(String.format("(%d%% OFF)", (int)discount));
                discountBadge.setStyle("-fx-font-weight: bold; -fx-fill: #fe3b30;");
                textContainer.getChildren().add(discountBadge);
            } else {
                // Regular price
                HBox priceBox = new HBox(5);
                Text priceLabel = new Text("Price: ");
                Text priceText = new Text(String.format("%.2f₪", price));
                priceText.setStyle("-fx-font-weight: bold;");
                priceBox.getChildren().addAll(priceLabel, priceText);
                textContainer.getChildren().add(priceBox);
            }

            // Add meal description (if available)
            if (meal.getMeal().getMeal().getDescription() != null && !meal.getMeal().getMeal().getDescription().isEmpty()) {
                Text description = new Text(meal.getMeal().getMeal().getDescription());
                textContainer.getChildren().add(description);
            }

            // Add customizations (with check/uncheck images)
            if (meal.getMeal().getCustomizationsList() != null && !meal.getMeal().getCustomizationsList().isEmpty()) {
                Text customizationsTitle = new Text("Customizations:");
                customizationsTitle.setStyle("-fx-font-weight: bold;");
                textContainer.getChildren().add(customizationsTitle);

                Image checkedImage = new Image(getClass().getResourceAsStream("/images/newcheck.png"));
                Image uncheckedImage = new Image(getClass().getResourceAsStream("/images/newradio.png"));

                for (CustomizationWithBoolean customWithBool : meal.getMeal().getCustomizationsList()) {
                    HBox customRow = new HBox(5);
                    Text customText = new Text(customWithBool.getCustomization().getName());
                    customText.setStyle("-fx-fill: black;");

                    ImageView checkImageView = new ImageView(customWithBool.getValue() ? checkedImage : uncheckedImage);
                    checkImageView.setFitWidth(20);
                    checkImageView.setFitHeight(20);
                    checkImageView.setPreserveRatio(true);

                    customRow.getChildren().addAll(checkImageView, customText);
                    textContainer.getChildren().add(customRow);
                }
            }

            // Add the text container to the row
            mealRow.getChildren().add(textContainer);

            // Add the row to the meal details container
            mealDetailsContainer.getChildren().add(mealRow);
        }

        // Update the total amount text (now includes any discounts)
        //String totalAmountText = String.format("Total: %.2f₪", totalAmount);
        // You'll need to update whatever displays the total amount with this new value
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
    public void addReserved() {
        StringBuilder orderDetails = new StringBuilder();

        HBox deliveryRow = new HBox(10);
        deliveryRow.setSpacing(10);


        Image delivery = new Image(getClass().getResourceAsStream("/images/reserved.png"));

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

        Text text = new Text("Reservation ");
        text.setStyle("-fx-font-weight: bold;");

        DeliveryInfo.getChildren().addAll(text, new Text("\n"));

        deliveryRow.getChildren().add(DeliveryInfo);

        mealDetailsContainer.getChildren().add(deliveryRow);
    }

}