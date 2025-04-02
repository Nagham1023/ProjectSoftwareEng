package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.CustomizationWithBoolean;
import il.cshaifasweng.OCSFMediatorExample.entities.MealInTheCart;
import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import org.hibernate.Session;

import java.util.List;

public class OrdersDB {

    public static Order getOrderById(int orderId) {
        System.out.println("now I am in the function");
        Order order = null;
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            order = session.get(Order.class, orderId);
            if (order != null) {
                order.setOrderStatus("Cancelled");
                session.update(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public static Order OrderById(int orderId) {
        Order order = null;
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            order = session.get(Order.class, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public static void saveOrder(Order order) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(order);  // Use saveOrUpdate to handle both new and existing entities

            session.flush();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveCustomizationsbool(List<CustomizationWithBoolean> customs) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            for (CustomizationWithBoolean custom : customs) {
                session.saveOrUpdate(custom);  // Use saveOrUpdate to handle both new and existing entities}
            }

            session.flush();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    /*public static void saveOrder2(Order order) {
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();
            // Persist CustomizationWithBoolean if necessary, using saveOrUpdate
            for (MealInTheCart mealInTheCart : order.getMeals()) {
                for (CustomizationWithBoolean customization : mealInTheCart.getMeal().getCustomizationsList()) {
                    // Save or update CustomizationWithBoolean
                    session.saveOrUpdate(customization);
                }
            }
            // Now save or update the order (this will cascade to meals and customizations)
            session.saveOrUpdate(order);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/