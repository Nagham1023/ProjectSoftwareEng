package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RestaurantList implements Serializable {

    private List<Restaurant> restaurantList;

    public RestaurantList() {
        restaurantList = new ArrayList<Restaurant>();
    }

    public List<Restaurant> getRestaurantList() {
        return restaurantList;
    }
    public void setRestaurantList(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @Override
    public String toString() {
        if (restaurantList.isEmpty()) {
            return "";
        }
        StringBuilder restaurantNames = new StringBuilder("RestaurantList: ");
        for (Restaurant restaurant : restaurantList) {
            restaurantNames.append(restaurant.getRestaurantName()).append(", ");
        }
        // Remove the trailing comma and space
        if (restaurantNames.length() > 0) {
            restaurantNames.setLength(restaurantNames.length() - 2);
        }
        return restaurantNames.toString();
    }
}
