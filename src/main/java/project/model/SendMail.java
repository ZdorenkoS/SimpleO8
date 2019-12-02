package project.model;
import org.apache.log4j.Logger;

import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
        private String username;
        private String password;
        private Properties props;
        private final static Logger log = Logger.getLogger(GetMail.class.getName());

        public SendMail(String username, String password) {
            this.username = username;
            this.password = password;

            props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        }

        public void send(String subject, String text, String toEmail){
            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("zsa@allo.ua"));

                message.setSubject(subject);
                message.setText(text);
                Transport.send(message);
                log.info("Номера О8 отправлены");
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }


