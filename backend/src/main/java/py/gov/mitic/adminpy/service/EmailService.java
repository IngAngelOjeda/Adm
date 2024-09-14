package py.gov.mitic.adminpy.service;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import py.gov.mitic.adminpy.model.dto.EmailContent;
import py.gov.mitic.adminpy.util.StringTemplate;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.password}")
    private String password;

    private Log logger = LogFactory.getLog(EmailService.class);

    @Autowired
    ResourceLoader resourceLoader;

    public void send(String toEmail, String subject, String bodyMessage) {

        try {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.transport.protocol", "mail");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.smtp.ssl.trust", host);

            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            };

            Session session = Session.getInstance(properties, auth);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, subject, "UTF-8"));
            //message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            if (toEmail.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            message.setSubject(subject, "UTF-8");

            MimeMultipart multipart = new MimeMultipart("related");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(bodyMessage, "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport.send(message);
            logger.info("Correo enviado a: " + toEmail);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carga el template en un string
     *
     * @param fileName
     * @return
     */
    public String loadTemplate(String fileName) {
        String text = null;
        try {
            URL url = Resources.getResource("mails/" + fileName);
            text = Resources.toString(url, Charsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error al obtener template de mail: " + e.getMessage());
        }
        return text;
    }

    /**
     * Reemplaza el contenido según los atributos del objeto
     * {@link EmailContent}
     *
     * @param template
     * @param contenido
     * @return
     */
    public String replaceTemplate(String template, EmailContent contenido) {
        try {
            if (InetAddress.getLocalHost() != null) {
                //System.out.println("HOST: "+InetAddress.getLocalHost().getCanonicalHostName());
                contenido.setHost(InetAddress.getLocalHost().getCanonicalHostName());
            }
            return StringTemplate.replace(loadTemplate(template), contenido);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al reemplazar contenido del mail: "
                    + e.getMessage());
        }
        return null;
    }
}
