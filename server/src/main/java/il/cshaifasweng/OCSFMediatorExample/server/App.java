package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

import il.cshaifasweng.OCSFMediatorExample.entities.Customization;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{

    private static Session session;

    private static SessionFactory getSessionFactory() throws HibernateException {
        String userinput = null;
        Configuration configuration = new Configuration();
        System.out.println("Enter your password:");
        Scanner userinput2 = new Scanner(System.in);
        userinput = userinput2.nextLine();
        configuration.setProperty("hibernate.connection.password", userinput);
        // Add Meal and Customization entities
        configuration.addAnnotatedClass(Meal.class);
        configuration.addAnnotatedClass(Customization.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
    private static void generateData() throws Exception {
        // Create Customizations
        Customization moreLettuce = new Customization();
        moreLettuce.setName("More Lettuce");

        Customization extraCheese = new Customization();
        extraCheese.setName("Extra Cheese");

        Customization moreOnion = new Customization();
        moreOnion.setName("More Onion");

        Customization highSpicyLevel = new Customization();
        highSpicyLevel.setName("High Spicy Level");


        Customization blackBread = new Customization();
        blackBread.setName("Black Bread");

        // Create Meals
        Meal burger = new Meal();
        burger.setName("Burger");
        burger.setPrice(8.00);
        burger.setCustomizations(Arrays.asList(moreLettuce));

        Meal spaghetti = new Meal();
        spaghetti.setName("Spaghetti");
        spaghetti.setPrice(10.00);
        spaghetti.setCustomizations(Arrays.asList(extraCheese));
        //spaghetti.getCustomizations().add(extraCheese);

        Meal avocadoSalad = new Meal();
        avocadoSalad.setName("Avocado Salad");
        avocadoSalad.setPrice(7.00);
        //avocadoSalad.getCustomizations().add(moreOnion);
        avocadoSalad.setCustomizations(Arrays.asList(moreOnion));

        Meal grills = new Meal();
        grills.setName("Grills");
        grills.setPrice(12.00);
        //grills.getCustomizations().add(highSpicyLevel);
        grills.setCustomizations(Arrays.asList(highSpicyLevel));

        Meal toastCheese = new Meal();
        toastCheese.setName("Toast Cheese");
        toastCheese.setPrice(5.00);
        //toastCheese.getCustomizations().add(blackBread);
        toastCheese.setCustomizations(Arrays.asList(blackBread));

        // Save the customizations and meals
        session.save(moreLettuce);
        session.save(extraCheese);
        session.save(moreOnion);
        session.save(highSpicyLevel);
        session.save(blackBread);

        session.save(burger);
        session.save(spaghetti);
        session.save(avocadoSalad);
        session.save(grills);
        session.save(toastCheese);
        System.out.println("here iykyk44");
        session.flush();
    }

    private static List<Meal> getAllMeals() throws Exception {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Meal> query = builder.createQuery(Meal.class);
        query.from(Meal.class);
        List<Meal> meals = session.createQuery(query).getResultList();
        // Force loading of customizations
        for (Meal meal : meals) {
            Hibernate.initialize(meal.getCustomizations());
        }
        return meals;
    }
    private static void printAllData() throws Exception {
        // List of meals with their customizations
        System.out.println("\n=== Meals List ===");
        for (Meal meal : getAllMeals()) {
            System.out.printf("Meal: %s | Price: %.2f%n", meal.getName(), meal.getPrice());
            System.out.println("Customizations:");
            for (Customization customization : meal.getCustomizations()) {
                System.out.printf("  - %s ", customization.getName());
            }
        }
    }
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            generateData();
            printAllData();

            session.getTransaction().commit();
        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
        //server = new SimpleServer(3000);
        //server.listen();
    }
