package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.RestMealsList;


public class RestaurantDB {
    private static List<Restaurant> restaurants;
    private static Session session;


    public RestaurantDB() {
        restaurants = new ArrayList<>();
        //initializeSampleRestaurants();
    }

    /*************/
    /**From here all the functions have to update and talk with the database*/


    public static List<Restaurant> getAllRestaurants(Session session) {
        // Ensure the session is open
        if (session == null || !session.isOpen()) {
            try {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            } catch (HibernateException e) {
                System.err.println("Error initializing Hibernate session: " + e.getMessage());
                return null;
            }
        }

        List<Restaurant> result = new ArrayList<>();
        try {
            // Begin the transaction for querying
            //session.beginTransaction();

            // Create a query to find all meals without any constraint
            String queryString = "FROM Restaurant";  // No WHERE clause, fetch all meals
            org.hibernate.query.Query<Restaurant> query = session.createQuery(queryString, Restaurant.class);

            // Execute the query and get the result list
            result = query.getResultList();
            restaurants = result;

            // Commit the transaction
            //session.getTransaction().commit();
        } catch (Exception e) {
            // Rollback the transaction if something went wrong
            if (session.getTransaction() != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println("Error executing the query: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
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
    public static int getRestaurantIdByName(String restaurantName) {
        int restaurantId = -1;  // Default value if not found

        for (Restaurant restaurant : RestMealsList) {
            if (restaurant.getRestaurantName().equals(restaurantName)) {
                restaurantId = restaurant.getId();
            }
        }
        return restaurantId;
    }



}
