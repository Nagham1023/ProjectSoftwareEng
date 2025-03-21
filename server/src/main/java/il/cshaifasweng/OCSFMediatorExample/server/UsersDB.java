package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.Users;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
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
    public static boolean checkAndUpdateUserSignInStatus(String username) throws Exception {
        Session session = null;
        try {
            // Open a session if it's not already open
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            // Begin a transaction
            session.beginTransaction();

            // Create a Criteria query to find the user by username
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            Root<Users> root = query.from(Users.class);
            query.select(root).where(builder.equal(root.get("username"), username));

            // Execute the query to get the user
            Users user = session.createQuery(query).uniqueResult();

            if (user != null) {
                // Check if the user is signed in
                if (!user.getSigned()) {
                    // If not signed in, update the status to true
                    user.setSigned(false); // Assuming there's a setSigned(boolean) method in the Users class

                    // Save the updated user to the database
                    session.update(user);
                    session.getTransaction().commit();
                    return false; // User was not signed in, but now they are
                } else {
                    // User is already signed in
                    return true;
                }
            } else {
                // User not found
                return false;
            }
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback in case of an error
            }
            throw e; // Re-throw the exception
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Close the session
            }
        }
    }
    public static void SignOut(UserCheck us) throws Exception {
        Session session = null;
        try {
            // Open a session if it's not already open
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();

            // Begin a transaction
            session.beginTransaction();

            // Create a Criteria query to find the user by username
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            Root<Users> root = query.from(Users.class);
            query.select(root).where(builder.equal(root.get("username"), us.getUsername()));

            // Execute the query to get the user
            Users user = session.createQuery(query).uniqueResult();

            if (user != null) {
                // Check if the user is signed in
                    // If not signed in, update the status to true
                    user.setSigned(false); // Assuming there's a setSigned(boolean) method in the Users class

                    // Save the updated user to the database
                    session.update(user);
                    session.getTransaction().commit();
            } else {
                // User not found
                return;
            }
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                session.getTransaction().rollback(); // Rollback in case of an error
            }
            throw e; // Re-throw the exception
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Close the session
            }
        }
    }
    public static boolean checkUser(String username, String password) throws Exception {
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
                                builder.equal(root.get("username"), username),
                                builder.equal(root.get("password"), password) // Hash password if applicable
                        )
                );

        Long count = session.createQuery(query).getSingleResult();
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

    public static void generateBasicUser1() throws Exception {
                // Helper function to read image as byte[]
        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

            session.beginTransaction(); // Start a transaction
            try {
            // Create orders
            if (!isUserExists("naghamTheManager") ) {

                Users nagham = new Users();
            nagham.setRole("CompanyManager");
            nagham.setEmail("naghammnsor@gmail.com");
            nagham.setPassword("NaghamYes");
            nagham.setUsername("naghamTheManager");
            nagham.setGender("other");
            nagham.setAge(22);

                session.save(nagham);
                session.flush();
                session.getTransaction().commit(); // Commit the transaction
            } }catch (Exception e) {
                    // Rollback transaction in case of an error
                    if (session.getTransaction() != null) {
                        session.getTransaction().rollback();
                    }
                    e.printStackTrace();
                    throw new Exception("An error occurred while generating the user.", e);
                }
        try {
            // Create orders
            if (!isUserExists("salhaTheCustomerService") ) {

                Users salha = new Users();
                salha.setRole("CustomerService");
                salha.setEmail("salhasalha121314@gmail.com");
                salha.setPassword("salha121314");
                salha.setUsername("salhaTheCustomerService");
                salha.setGender("female");
                salha.setAge(55);

                session.save(salha);
                session.flush();
                session.getTransaction().commit(); // Commit the transaction
                System.out.println("User has been created: " + salha.getUsername());
            } }catch (Exception e) {
            // Rollback transaction in case of an error
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new Exception("An error occurred while generating the user.", e);
        }



            // List of Orders to add
            //List<Users> newOrders = Arrays.asList(nagham,salha);

//            // Fetch existing meals from the database
//            CriteriaBuilder builder = session.getCriteriaBuilder();
//            CriteriaQuery<Users> query = builder.createQuery(Users.class);
//            query.from(Users.class);
//            List<Users> existingMeals = session.createQuery(query).getResultList();

            // Save customizations
    }
    private static boolean isUserExists(String username) {
        System.out.println("Checking if user exists: " + username);
        Query query = session.createQuery("FROM Users WHERE username = :username");
        query.setParameter("username", username);
        List<?> result = query.list();
        System.out.println("result check");
        return !result.isEmpty();
    }
}
