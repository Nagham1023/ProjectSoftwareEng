package il.cshaifasweng.OCSFMediatorExample.server;
import il.cshaifasweng.OCSFMediatorExample.entities.Meal;
import il.cshaifasweng.OCSFMediatorExample.entities.UserCheck;
import il.cshaifasweng.OCSFMediatorExample.entities.UserManagement;
import il.cshaifasweng.OCSFMediatorExample.entities.Users;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import static il.cshaifasweng.OCSFMediatorExample.server.App.getSessionFactory;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.allUsers;

public class UsersDB {
    private static Session session;

    public static List<Users> getUsers() {
        Session localSession = null; // Local session for this method
        List<Users> users = null;
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
                    user.setSigned(true); // Assuming there's a setSigned(boolean) method in the Users class

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
        for (Users user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(us.getUsername())) {
                user.setSigned(false);
            }
        }
    }

    public static void UpdateEmailAndPassword (UserCheck us) throws Exception {
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

                user.setEmail(us.getEmail());
                user.setPassword(us.getPassword());

                session.update(user);
                session.getTransaction().commit();
                //updateUserDetailsById(user);
                for (Users temp : allUsers) {
                    if (temp.getUsername().equalsIgnoreCase(user.getUsername())) {
                        temp.setEmail(us.getEmail());
                        temp.setPassword(us.getPassword());
                    }
                }
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


    /*public static void updateUserDetailsById(Users UserU) {
        // Check if the users list is initialized
        if (users == null || users.isEmpty()) {
            System.out.println("The users list is empty or not initialized.");
            return;
        }

        // Search for the user with the given ID
        for (Users user : users) {
            if (user.getId() == UserU.getId()) {
                user=UserU;
                return; // Exit the loop after updating
            }
        }
    }*/




    public static void signIn(UserCheck user) throws Exception {
        for (Users temp : allUsers) {
            if (temp.getUsername().equalsIgnoreCase(user.getUsername())&& temp.getPassword().equals(user.getPassword())) {
                if (temp.getSigned()) {
                    user.setRespond("Already Signed in");
                    return;
                }
                temp.setSigned(true);
                user.setRespond("Valid");
                getUserInfo(user,temp);
                return;
            }
        }
        user.setRespond("Username or password incorrect");
    }


    public static boolean checkEmail(UserCheck us) throws Exception {

        for (Users user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(us.getUsername())) {
                if (user.getEmail().equalsIgnoreCase(us.getEmail())) {
                    us.setPassword(user.getPassword());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkUserName(String username) throws Exception {
        for (Users user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void getUserInfo(UserCheck us,Users user) throws Exception {
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
        String role= newUser.getRole();
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
                newU.setRole(role);
                session.save(newU);
                allUsers.add(newU);
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

    public static void UpdateUser (UserCheck us) throws Exception {
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
            query.select(root).where(builder.equal(root.get("username"), us.getFirstName()));
            System.out.println(us.getFirstName()+" to make sure it is the right old userName");
            // Execute the query to get the user
            Users user = session.createQuery(query).uniqueResult();

            if (user != null) {

                user.setEmail(us.getEmail());
                user.setPassword(us.getPassword());
                user.setAge(us.getAge());
                user.setGender(us.getGender());
                user.setRole(us.getRole());
                user.setUsername(us.getUsername());
                System.out.println(user);

                session.update(user);
                session.getTransaction().commit();
                //updateUserDetailsById(user);
                for (Users temp : allUsers) {
                    if (temp.getUsername().equalsIgnoreCase(us.getUsername())) {
                        temp.setEmail(us.getEmail());
                        temp.setPassword(us.getPassword());
                        temp.setAge(us.getAge());
                        temp.setGender(us.getGender());
                        temp.setRole(us.getRole());
                        temp.setUsername(us.getUsername());
                    }
                }
            } else {
                // User not found
                return;
            }
        } catch (Exception e) {
            if (session != null && session.getTransaction() != null) {
                System.out.println("hhhhhhhhhhh no saved user");
                session.getTransaction().rollback(); // Rollback in case of an error
            }
            throw e; // Re-throw the exception
        } finally {
            if (session != null && session.isOpen()) {
                session.close(); // Close the session
            }
        }
    }

    public static void generateBasicUser1() throws Exception {
        // Helper function to read image as byte[]
        if (session == null || !session.isOpen()) { // hala added to Ensure session is opened before calling generateOrders().
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }

        /*if (session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }*/
        session.beginTransaction(); // Start a transaction
        try {
            // Create orders

            Users nagham = new Users();
            nagham.setRole("CompanyManager");
            nagham.setEmail("naghammnsor@gmail.com");
            nagham.setPassword("NaghamYes");
            nagham.setUsername("naghamTheManager");
            nagham.setGender("other");
            nagham.setAge(22);
            Users salha = new Users();
            salha.setRole("CustomerService");
            salha.setEmail("salhasalha121314@gmail.com");
            salha.setPassword("salha121314");
            salha.setUsername("salhaTheCustomerService");
            salha.setGender("female");
            salha.setAge(55);
            Users knani = new Users();
            knani.setRole("CustomerService");
            knani.setEmail("yousefknani9@gmail.com");
            knani.setPassword("123");
            knani.setUsername("yousef");
            knani.setGender("male");
            knani.setAge(32);
            Users yousef = new Users();
            yousef.setRole("CompanyManager");
            yousef.setEmail("yousefknani9@gmail.com");
            yousef.setPassword("212");
            yousef.setUsername("ceo");
            yousef.setGender("male");
            yousef.setAge(24);
            Users shada = new Users();
            shada.setRole("Dietation");
            shada.setEmail("shadamazzawi@gmail.com");
            shada.setPassword("123");
            shada.setUsername("shada");
            shada.setGender("other");
            shada.setAge(22);
            if (!isUserExists("naghamTheManager") ) {
                allUsers.add(nagham);
                session.save(nagham);
                System.out.println("User has been created: " + nagham.getUsername());
            }

            // Ensure you are checking for existing user
            if (!isUserExists("salhaTheCustomerService")) {
                allUsers.add(salha);
                // Save the new user to the database
                session.save(salha);
                System.out.println("User has been created: " + salha.getUsername());
            }

            // Create orders
            if (!isUserExists("ceo") ) {
                allUsers.add(yousef);
                session.save(yousef);
                System.out.println("User has been created: " + yousef.getUsername());
            }
            if (!isUserExists("shada") ) {
                allUsers.add(shada);
                session.save(shada);
                System.out.println("User has been created: " + shada.getUsername());
            }
            if (!isUserExists("yousef") ) {
                allUsers.add(knani);
                session.save(knani);
                System.out.println("User has been created: " + knani.getUsername());
            }
            session.flush();
            session.getTransaction().commit(); // Commit the transaction
        }
        catch (Exception e) {
            // Rollback transaction in case of an error
            e.printStackTrace();
            throw new Exception("An error occurred while generating the user.", e);
        }
    }

    private static boolean isUserExists(String username) {

        for (Users user : allUsers) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void delete(String userName) throws Exception {
        System.out.println("now I am in the function to delete the user: " + userName);
        try (Session session = App.getSessionFactory().openSession()) {
            session.beginTransaction();
           // Users user = session.get(Users.class, userName);
            // Create a Criteria query to find the user by username
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Users> query = builder.createQuery(Users.class);
            Root<Users> root = query.from(Users.class);
            query.select(root).where(builder.equal(root.get("username"), userName));
            // Execute the query to get the user
            Users user = session.createQuery(query).uniqueResult();
            if (user != null) {
                allUsers.remove(user);
                session.delete(user);
                session.getTransaction().commit();
                System.out.println("Deleted user: " + userName);
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
}