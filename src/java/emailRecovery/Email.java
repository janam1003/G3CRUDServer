package emailRecovery;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import encryption.EncryptionImplementation;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

/**
 * The {@code Email} class provides methods for generating random passwords and
 * sending email for account recovery purposes. This class utilizes the JavaMail
 * API and an encryption mechanism for secure communication.
 *
 * @author Janam
 * @version 1.0
 */
public class Email {

    //  Logger for the class.
    private static final Logger LOGGER = Logger.getLogger("emailRecovery");

    /**
     * Reads the content of a file and returns it as a string.
     *
     * @param path The path of the file to be read.
     * @return The content of the file as a string.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    /**
     * Converts an input stream to a string.
     *
     * @param inputStream The input stream to be converted.
     * @return The string representation of the input stream.
     * @throws IOException If an I/O error occurs while reading the input
     * stream.
     */
    private static String inputStreamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder stringBuilder = new StringBuilder();
            int c;
            while ((c = br.read()) != -1) {
                stringBuilder.append((char) c);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The main method for testing the email functionality with a sample email
     * address.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        sendEmail("lucasjanamsmile@gmail.com");
    }

    /**
     * Sends an email for account recovery with a randomly generated password.
     *
     * @param emailUser The email of the user to send the recovery email to.
     * @return The randomly generated password that was sent in the email.
     */
    public static String sendEmail(String emailUser) {

        final String newPassword;

        // Load Private Key
        InputStream fis = Email.class.getResourceAsStream("simetricKey.der");

        try {

            String decryptedCredentials = EncryptionImplementation.descifrarCredentials(inputStreamToString(fis));

            // Split credentials
            String[] credentials = decryptedCredentials.split("=");
            String SENDER_EMAIL = credentials[3];
            String SENDER_PASSWORD = credentials[5];

            final String ZOHO_HOST = "smtp.zoho.eu";
            final String TLS_PORT = "897";
            final String RECEIVER_EMAIL = emailUser;
            newPassword = generateRandomPassword(Integer.parseInt(credentials[1]));

            // protocol properties
            Properties props = System.getProperties();
            props.setProperty("mail.smtps.host", ZOHO_HOST);
            props.setProperty("mail.smtp.port", TLS_PORT);
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtps.auth", "true");

            // close connection upon quit being sent
            props.put("mail.smtps.quitwait", "false");

            Session session = Session.getInstance(props, null);

            // create the message
            final MimeMessage msg = new MimeMessage(session);

            // set recipients and content
            msg.setFrom(new InternetAddress(SENDER_EMAIL));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECEIVER_EMAIL, false));

            msg.setSubject("Email Recovery");

            // Set HTML content
            String emailBody = "<body style=\"font-size:16px;padding-top:20px;color:rgb(51,51,51);\">"
                    + "<table style=\"width:100%;max-width:600px\" align=\"center\"><tr><td><br/>"
                    + "<b style=\"font-size: 20px;\">G3CRUD</b><br/>"
                    + "<br/>"
                    + "Recently, we received a request to reset the account password associated with this email address. "
                    + "We understand from time to time passwords are lost or forgotten and we are here to assist you.<br/>"
                    + "<br/>"
                    + "We recommend that you update your password as soon as possible. When choosing a new password, we suggest you secure your account security by selecting a unique password that you have not used on other websites. "
                    + "Strong passwords contain more than eight characters and include small and capital letters, special characters and numbers.<br/>"
                    + "<br/>"
                    + "Your new generated login password is: <i><b>" + newPassword + "</b></i><br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<b>ADDITIONAL ASSISTANCE</b><br/>"
                    + "<br/>"
                    + "If you did not request a password reset, please notify Customer Support.<br/>"
                    + "<br/>"
                    + "Thank you for choosing G3CRUD.<br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<b>NOTE</b>: <i>Do not respond to this email. It does not accept incoming emails!</i>"
                    + "</td></tr></table><br/>"
                    + "<br/>"
                    + "<br/>"
                    + "<footer style=\"font-size:12px;line-height:150%;margin:0;text-align:center;color:rgb(51,51,51);\">This email was sent by:<br/>"
                    + "<b>G3CRUD Customer Service</b><br/>"
                    + "Calle Fanderia, 48901<br/>"
                    + "Barakaldo, Bizkaia, Spain</footer><br/>"
                    + "</body>";

            msg.setContent(emailBody, "text/html; charset=utf-8");

            msg.setSentDate(new Date());

            // send the mail
            Transport transport = session.getTransport("smtps");

            // send the mail
            transport.connect(ZOHO_HOST, SENDER_EMAIL, SENDER_PASSWORD);

            transport.sendMessage(msg, msg.getAllRecipients());

            LOGGER.info("Zoho mail sent successfully");

        } catch (IOException | NumberFormatException | MessagingException e) {

            throw new RuntimeException(e);
        }

        return newPassword;

    }

    /**
     * This method generates a random password of a specified length and is
     * intended to be sent to a user's email for account-related purposes.
     *
     * @param length The length of the generated password.
     * @return A randomly generated password as a String.
     */
    private static String generateRandomPassword(int length) {

        // Define the characters that can be used in the random password
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$*";

        // Create a SecureRandom object for generating random indices
        SecureRandom random = new SecureRandom();

        // StringBuilder to construct the random password
        StringBuilder sb = new StringBuilder();

        // Loop to generate the password of the specified length
        for (int i = 0; i < length; i++) {

            // Generate a random index within the length of the character set
            int randomIndex = random.nextInt(chars.length());

            // Append the character at the randomly generated index to the password
            sb.append(chars.charAt(randomIndex));
        }

        // Return the final randomly generated password as a String
        return sb.toString();
    }

}
