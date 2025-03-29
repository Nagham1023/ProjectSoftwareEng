package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Order;
import il.cshaifasweng.OCSFMediatorExample.entities.ReservationSave;
import org.hibernate.Session;

public class ReservationsDB {

    //searching for the reservation in the database by its id and returning it as an object if it exists
    public static ReservationSave getReservationById(String reservationId) {
        System.out.println("now I am in getReservationById");
        ReservationSave reservation = null;
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            reservation = session.get(ReservationSave.class, reservationId);
            if (reservation != null) {
                return reservation;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } return null;
    }

    //deleting the reservation from the database
     public static boolean cancelReservationById(String reservationSaveID) {
        System.out.println("now I am in the cancelReservationById method");
        ReservationSave reservation = null;
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();

            reservation = session.get(ReservationSave.class,reservationSaveID );
            if (reservation != null) {
                session.delete(reservation);
                System.out.println("Reservation " + reservationSaveID + " was successfully canceled and deleted.");
                return true;
            }else {
                System.out.println("Reservation " + reservationSaveID + " not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
