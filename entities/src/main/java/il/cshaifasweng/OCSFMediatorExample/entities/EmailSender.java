package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * Sends email through Mailjet SMTP.
 * Credentials are read from environment variables:
 *   MAILJET_API_KEY, MAILJET_SECRET_KEY, MAIL_FROM
 * If any of them is missing, the app runs in development mode:
 * emails (including verification codes) are printed to the console instead of being sent.
 */
public class EmailSender {

    private static final String SMTP_HOST = "in-v3.mailjet.com";
    private static final String SMTP_PORT = "587";

    public static void sendEmail(String subject, String body, String to) {
        String apiKey = System.getenv("MAILJET_API_KEY");
        String secretKey = System.getenv("MAILJET_SECRET_KEY");
        String from = System.getenv("MAIL_FROM");

        if (isBlank(apiKey) || isBlank(secretKey) || isBlank(from)) {
            printToConsole(subject, body, to);
            return;
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(apiKey, secretKey);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            System.out.println("Email sending failed: " + e.getMessage());
            printToConsole(subject, body, to);
        }
    }

    private static void printToConsole(String subject, String body, String to) {
        System.out.println("========== EMAIL (development mode - not sent) ==========");
        System.out.println("To:      " + to);
        System.out.println("Subject: " + subject);
        System.out.println(body);
        System.out.println("=========================================================");
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}