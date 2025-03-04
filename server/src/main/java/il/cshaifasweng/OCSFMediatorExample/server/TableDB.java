package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.TableNode;
import il.cshaifasweng.OCSFMediatorExample.entities.Restaurant;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TableDB {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public void addTable(TableNode table) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(table);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TableNode> getAllTables() {
        List<TableNode> tables = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<TableNode> query = session.createQuery("FROM TableNode", TableNode.class);
            tables = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tables;
    }

    public TableNode getTableById(int tableId) {
        TableNode table = null;
        try (Session session = sessionFactory.openSession()) {
            table = session.get(TableNode.class, tableId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    public void updateTable(TableNode updatedTable) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(updatedTable);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(int tableId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            TableNode table = session.get(TableNode.class, tableId);
            if (table != null) {
                session.delete(table);
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
