package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.Users;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;

public class UsersDB {
    private static Session session;
    public static List<Users> users;


    public static List<Users> getUsers() {
        Session localSession = null; // Local session for this method

        try {
            // Ensure the session is open or create a new session locally
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                localSession = sessionFactory.openSession(); // Use local session
            } else {
                localSession = session; // Use the shared session if available
            }

            // Perform the query
            CriteriaBuilder builder = localSession.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            users = localSession.createQuery(query).getResultList();

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        } finally {
            // Only close the local session if it was created locally
            if (localSession != null && localSession != session && localSession.isOpen()) {
                localSession.close();
            }
        }

        return users;
    }
    public static boolean checkUser(String username, String password) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        //CriteriaBuilder builder = session.getCriteriaBuilder();
        //CriteriaQuery<Long> query = builder.createQuery(Long.class);
        //Root<Users> root = query.from(Users.class);
        List<Users> root = session.createQuery("FROM Users", Users.class).list();
        int count = root.toArray().length;
/*
        query.select(builder.count(root))
                .where(
                        builder.and(
                                builder.equal(root.get("username"), username),
                                builder.equal(root.get("password"), password) // Hash password if applicable
                        )
                );

        Long count = session.createQuery(query).getSingleResult();*/
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }

        return count > 0;
    }
    public static boolean checkEmail(UserCheck us) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        // Use CriteriaBuilder to fetch the user
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Users> query = builder.createQuery(Users.class); // Change the return type to Users
        Root<Users> root = query.from(Users.class);

        query.select(root)
                .where(
                        builder.and(
                                builder.equal(root.get("username"), us.getUsername()),
                                builder.equal(root.get("email"), us.getEmail())
                        )
                );
        Users user = session.createQuery(query).uniqueResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }

        if (user != null) {
            us.setPassword(user.getPassword());
            return true;
        } else {
            return false;
        }
    }
    public static boolean checkUserName(String username) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Users> root = query.from(Users.class);

        query.select(builder.count(root))
                .where(
                        builder.and(
                                builder.equal(root.get("username"), username)// Hash password if applicable
                        )
                );

        Long count = session.createQuery(query).getSingleResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }

        return count > 0;
    }
    public static void getUserInfo(UserCheck us) throws Exception {
        if (session == null || !session.isOpen()) {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Users> query = builder.createQuery(Users.class);
        Root<Users> root = query.from(Users.class);

        query.select(root)
                .where(
                        builder.and(
                                builder.equal(root.get("username"), us.getUsername())
                        )
                );
        Users user = session.createQuery(query).uniqueResult();
        if (session != null && session.isOpen()) {
            session.close(); // Close the session after operation
        }
        if(user != null) {
            us.setEmail(user.getEmail());
            us.setUsername(user.getUsername());
            us.setPassword(user.getPassword());
            us.setAge(user.getAge());
            us.setGender(user.getGender());
            us.setId(user.getId());
            us.setRole(user.getRole());
        }
    }
    public static String AddNewUser(UserCheck newUser) {
        // Extract data from the mealEvent object
        String UserName = newUser.getUsername();
        String UserPass = newUser.getPassword();
        String UserEmail = newUser.getEmail();
        String UserGender = newUser.getGender();
        int UserAge = newUser.getAge();


        try {
            // Ensure the session is open
            if (session == null || !session.isOpen()) {
                SessionFactory sessionFactory = getSessionFactory();
                session = sessionFactory.openSession();
            }

            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }

            session.beginTransaction();

            // Check for duplicates in the database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            query.from(Users.class);
            List<Users> existingUsers = session.createQuery(query).getResultList();

            boolean isDuplicate = existingUsers.stream()
                    .anyMatch(existingMeal -> existingMeal.getUsername().equalsIgnoreCase(UserName));

            if (isDuplicate) {
                System.out.println("User already exist: " + UserName);
                return "User already exist";
            } else {
                Users newU = new Users();
                newU.setUsername(UserName);
                newU.setPassword(UserPass);
                newU.setEmail(UserEmail);
                newU.setGender(UserGender);
                newU.setAge(UserAge);
                newU.setRole("Customer");


                session.save(newU);
                users.add(newU);

                System.out.println("New User added: " + UserName);
            }

            // Commit the transaction
            session.getTransaction().commit();

        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback on error
            }
            e.printStackTrace();
        } finally {
            // Leave the session open for further operations
            if (session != null && session.isOpen()) {
                session.close(); // Close the session after operation
            }
        }
        return "Registration completed successfully";
    }
    public static void printAllUsers() {
        try {
            System.out.println("\n=== Users List ===");
            for (Users user : getUsers()) {
                System.out.println(user.toString());
            }
        } catch (Exception e) {
            System.err.println("An error occurred while fetching users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
