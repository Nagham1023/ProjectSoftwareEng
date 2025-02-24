package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;



public class RestaurantDB {
    private List<Restaurant> restaurants;


    public RestaurantDB() {
        restaurants = new ArrayList<>();
        initializeSampleRestaurants();
    }

    /*************/
    /**From here all the functions have to update and talk with the database*/

    private void initializeSampleRestaurants() {
        restaurants.add(new Restaurant(1, "Haifa", "images/restaurant.jpg", "04-9944871"));
        restaurants.add(new Restaurant(2, "Tel Aviv", "images/restaurant.jpg", "04-9825625"));
        restaurants.add(new Restaurant(3, "Old Aco", "images/restaurant.jpg", "04-9458712"));
        restaurants.add(new Restaurant(4, "Tamra", "images/restaurant.jpg", "04-9944321"));
    }

    public List<Restaurant> getAllRestaurants() {
        return new ArrayList<>(restaurants);  // Return a copy to avoid external modifications
    }

    // Deletes a restaurant by its ID
    public void deleteRestaurant(int restaurantId) {
        boolean changed = restaurants.removeIf(restaurant -> restaurant.getId() == restaurantId);

    }

    // Adds a new restaurant to the list
    public void addRestaurant(Restaurant restaurant) {
        restaurants.add(restaurant);
    }

    // Update restaurant details
    public void updateRestaurant(Restaurant updatedRestaurant) {
        int index = restaurants.indexOf(updatedRestaurant);
        if (index != -1) {
            restaurants.set(index, updatedRestaurant);
        }
    }


}
