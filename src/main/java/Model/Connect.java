package Model;

import org.apache.log4j.Logger;

import javax.mail.*;
import java.util.Properties;

public class Connect {
    private final static Logger log = Logger.getLogger(Connect.class.getName());
    private Properties props;
    private Session session;
    private Store store;
    private Folder folder;


    public Connect() {
        props = new Properties();
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.port", "IMAP_port");
        session = Session.getInstance(props);
        try {
            store = session.getStore();
        } catch (NoSuchProviderException ex) {
            log.error("Сбой при попытке соединения с почтовым сервером", ex);
        }

    }

    public Folder getFolder() {
        return folder;
    }
// TODO скрыть логин и пароль
    public void connect() {
        try {
            store.connect("imap.ukr.net", "d.a.o.s@ukr.net", "Nikita2012");
            folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);

            log.info("Соединение с почтовым сервером установлено");
        } catch (MessagingException ex) {
            log.error("Сбой при попытке доступа к папкам", ex);
        }
    }

    public void disconnect() {
        try {
            store.close();
        } catch (MessagingException ex) {
            log.error("Сбой при закрытии коннекта", ex);
        }
    }
}

