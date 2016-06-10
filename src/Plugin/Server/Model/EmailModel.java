package Plugin.Server.Model;

import Core.Http.Code;
import Core.Model;
import Core.Singleton.ServerSingleton;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by teddy on 27/05/2016.
 */
public class EmailModel extends Model {
    private Properties mailServerProperties;
    private Session getMailSession;
    private MimeMessage generateMailMessage;

    public EmailModel send(String socket, String subject, String emailBody) {
        try {
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> setup Mail ServerModel Properties..");
            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", "587");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> Mail ServerModel Properties have been setup successfully..");
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> get Mail Session..");
            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);
            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("tf.sheol@gmail.com"));
            generateMailMessage.setSubject(subject);
            generateMailMessage.setContent(emailBody, "text/html");
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> Mail Session has been created successfully..");
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> Get Session and Send mail");
            Transport transport = getMailSession.getTransport("smtp");
            transport.connect("smtp.gmail.com", "tf.sheol", "hvubwmmjdusluqon");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();
            ServerSingleton.getInstance().log(socket, "[SYSTEM] -> mail as sent !");
        } catch (MessagingException e) {
            setCode(socket, Code.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return this;
    }
}