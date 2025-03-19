package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import org.hibernate.Session;

public class OrdersDB {

    public static Order getOrderById(String orderId) {
        System.out.println("now I am in the function");
        Order order = null;
        int id=Integer.parseInt(orderId);
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            order = session.get(Order.class,id);
            if (order != null) {
                order.setOrderStatus("Cancelled");
                session.update(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
}
}